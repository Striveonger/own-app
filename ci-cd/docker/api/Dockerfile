FROM striveonger/ubuntu:22.04
LABEL maintainer="striveonger@163.com"
LABEL version="1.0.0"

WORKDIR /opt/app

COPY ci-cd/docker/api/app.sh /opt/app/app.sh
RUN chmod +x /opt/app/app.sh

# COPY internal/service/target/own-app.jar /opt/app/app.jar
# 拆分jar包, 分层缓存
COPY internal/service/target/app/org /opt/app/org
COPY internal/service/target/app/META-INF /opt/app/META-INF
COPY internal/service/target/app/BOOT-INF/layers.idx /opt/app/BOOT-INF/layers.idx
COPY internal/service/target/app/BOOT-INF/classpath.idx /opt/app/BOOT-INF/classpath.idx
COPY internal/service/target/app/BOOT-INF/lib /opt/app/BOOT-INF/lib
# 有变动的文件放在最后
COPY internal/service/target/app/BOOT-INF/classes /opt/app/BOOT-INF/classes

EXPOSE 18081

ENTRYPOINT ["sh", "/opt/app/app.sh"]
