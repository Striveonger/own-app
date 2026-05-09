## 测试

### pytest 测试命令

```bash
docker run --rm -v ./test/:/tmp/test striveonger/pytest:1.0.0 /bin/bash -c 'cd /tmp/test ; pytest -v pytest-case/*'
```

### locust 测试命令

```bash
docker run --rm -v ./test/:/tmp/test striveonger/python:3.12 /bin/bash -c 'cd /tmp/test ; locust -f locust-case/browse-vlog.py --headless -u 100 -r 10 -t 60s'
```

- `--headless`: 非交互模式运行，不打开浏览器
- `-u 100`: 模拟 100 个用户并发访问
- `-r 10`: 每个用户并发请求数为 10
- `-t 60s`: 测试持续 60 秒


### 配置文件

- [conftest.py](conftest.py) - pytest 配置文件
 
