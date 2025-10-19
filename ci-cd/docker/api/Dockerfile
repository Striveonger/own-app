FROM ubuntu:22.04
# FROM debian:trixie-slim
LABEL maintainer="striveonger@163.com"
LABEL version="1.0.0"

COPY --from=ubuntu/jre:17_edge /opt/java /opt/java
# COPY --from=bitnami/java:21 /opt/bitnami/java /opt/java

# 设置时区变量及符号链接
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /opt/app

# COPY internal/service/target/own-app.jar /opt/app/app.jar
COPY internal/service/target/app/org /opt/app/org
COPY internal/service/target/app/META-INF /opt/app/META-INF
COPY internal/service/target/app/BOOT-INF/layers.idx /opt/app/BOOT-INF/layers.idx
COPY internal/service/target/app/BOOT-INF/classpath.idx /opt/app/BOOT-INF/classpath.idx
COPY internal/service/target/app/BOOT-INF/lib /opt/app/BOOT-INF/lib
COPY internal/service/target/app/BOOT-INF/classes /opt/app/BOOT-INF/classes

COPY ci-cd/docker/api/app.sh /opt/app/app.sh

ENV JAVA_HOME=/opt/java

EXPOSE 18081

ENTRYPOINT ["sh", "/opt/app/app.sh"]