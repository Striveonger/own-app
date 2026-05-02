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
from locust import HttpUser, TaskSet, task, between
from common.api_client import get_vlog_list

# ==================== 配置加载 ====================
def load_config():
    config_file = Path(__file__).parent / "own.config.yaml"
    with open(config_file, 'r', encoding='utf-8') as f:
        return yaml.safe_load(f)

CONFIG = load_config()
BASE_URL = CONFIG['base']['url']

# ==================== 任务集 ====================
class BrowseVlogTaskSet(TaskSet):
    """浏览 Vlog 任务集"""
    def on_start(self):
        """用户启动时初始化"""
        self.user_id = random.randint(1000000, 9999999)

    @task
    def get_vlog_list(self):
        """获取用户推荐 Vlog 列表"""
        response = get_vlog_list(BASE_URL, self.user_id)
        # Locust 自动统计 request/response，无需手动 success()
        if response.status_code != 200:
            response.failure(f"HTTP {response.status_code}")

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
