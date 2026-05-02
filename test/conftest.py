import pytest
import random
import time
import yaml
from pathlib import Path


def pytest_configure(config):
    """加载配置文件到 pytest"""
    config_file = Path(__file__).parent / "own.config.yaml"
    with open(config_file, 'r', encoding='utf-8') as f:
        config._cfg = yaml.safe_load(f)


@pytest.fixture(scope="session")
def config(pytestconfig):
    return pytestconfig._cfg


@pytest.fixture(scope="session")
def base_url(config):
    return config['base']['url']


@pytest.fixture
def user_id():
    return random.randint(1000000, 9999999)
