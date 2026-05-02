#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""API 客户端 - pytest 和 Locust 共享"""
import random
import requests

def get_vlog_list(base_url, user_id):
    """获取 Vlog 列表"""
    response = requests.get(
        f"{base_url}/api/v1/vlog/list",
        params={"userId": user_id}
    )
    return response