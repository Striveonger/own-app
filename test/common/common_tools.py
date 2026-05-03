#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""API 客户端 - pytest 和 Locust 共享"""
from pathlib import Path
import yaml

# ==================== 全局配置 ====================
def load_config():
    config_file = Path(__file__).parent.parent / "own.config.yaml"
    with open(config_file, 'r', encoding='utf-8') as f:
        return yaml.safe_load(f)
