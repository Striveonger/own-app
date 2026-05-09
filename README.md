# own-app


## 压测记录

### PlanA: 测试 Vlog 列表接口
```bash
docker run --rm -v ./test/:/tmp/test striveonger/python:3.12 /bin/bash -c 'cd /tmp/test ; locust -f locust-case/browse-vlog.py --headless -u 100 -r 10 -t 60s'

========== 最终测试报告 ==========
总请求数: 1905
平均响应时间: 830.06 毫秒
失败率: 0.00%
平均RPS: 31.78
===================================
```

### PlanB: 测试 Vlog 列表接口

```bash
docker run --rm -v ./test/:/tmp/test striveonger/python:3.12 /bin/bash -c 'cd /tmp/test ; locust -f locust-case/browse-vlog.py --headless -u 100 -r 10 -t 60s'

========== 最终测试报告 ==========
总请求数: 2759
平均响应时间: 28.74 毫秒
失败率: 0.00%
平均RPS: 46.10
===================================
```

### PlanC: 测试 Vlog 列表接口

```bash
docker run --rm -v ./test/:/tmp/test striveonger/python:3.12 /bin/bash -c 'cd /tmp/test ; locust -f locust-case/browse-vlog.py --headless -u 100 -r 10 -t 60s'

========== 最终测试报告 ==========
总请求数: 2524
平均响应时间: 129.09 毫秒
失败率: 0.00%
平均RPS: 42.34
===================================
```
