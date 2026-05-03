## 测试

### pytest 测试命令

```bash
docker run --rm -v ./test/:/tmp/test striveonger/pytest:1.0.0 /bin/bash -c 'cd /tmp/test ; pytest -v pytest-case/*'
```

### locust 测试命令

```bash
docker run --rm -v ./test/:/tmp/test striveonger/python:3.12 /bin/bash -c 'cd /tmp/test ; locust -f locust-case/browse-vlog.py --headless -u 100 -r 10 -t 60s'
```

### 配置文件

- [conftest.py](conftest.py) - pytest 配置文件
 
