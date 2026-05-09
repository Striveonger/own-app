#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
Locust 压测脚本
基于 test/own.config.yaml 配置，复用 test/common/api_client.py

用法:
    locust -f locustfile.py --headless -u 100 -r 10 -t 60s
    locust -f locustfile.py --list  # 查看任务列表
"""

import random
import yaml
from pathlib import Path
from locust import HttpUser, TaskSet, task, between, events
from common.common_tools import load_config

# ==================== 配置加载 ====================
CONFIG = load_config()
BASE_URL = CONFIG['base']['url']


# ==================== 测试结束报告 ====================
@events.quitting.add_listener
def on_quitting(environment, **kwargs):
    stats = environment.stats.total
    print("\n========== 最终测试报告 ==========")
    print(f"总请求数: {stats.num_requests}")
    print(f"平均响应时间: {stats.avg_response_time:.2f} 毫秒")
    print(f"失败率: {stats.fail_ratio * 100:.2f}%")
    print(f"平均RPS: {stats.total_rps:.2f}")
    print("===================================")

# ==================== 任务集 ====================
class BrowseVlogTaskSet(TaskSet):
    """浏览 Vlog 任务集"""
    def on_start(self):
        """用户启动时初始化"""
        self.user_id = random.randint(1000000, 9999999)

    @task
    def get_vlog_list(self):
        """获取用户推荐 Vlog 列表"""
        response = self.client.get(
            f"{BASE_URL}/api/v1/vlog/list",
            params={"userId": self.user_id, "plan": "B"}
        )
        assert response.status_code == 200, f"获取用户推荐 Vlog 列表失败，状态码: {response.status_code}"
        ### 判断data数组是否为空
        assert response.json()['data'], "data数组为空，响应体中未包含用户推荐 Vlog 列表"


# ==================== 用户类 ====================
class BrowseVlogUser(HttpUser):
    """
    模拟浏览 Vlog 用户

    - tasks: 关联的任务集
    - wait_time: 任务间隔时间（秒）
    - host: 目标服务地址（可通过命令行 --host 覆盖）
    """
    tasks = [BrowseVlogTaskSet]
    wait_time = between(1, 3)
    host = BASE_URL
