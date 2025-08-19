FROM ubuntu:22.04
#FROM debian:trixie-slim
LABEL maintainer="striveonger@163.com"
LABEL version="1.0.0"

COPY --from=ubuntu/jre:17_edge /opt/java /opt/java
#COPY --from=bitnami/java:21 /opt/bitnami/java /opt/bitnami/java

# 设置时区变量及符号链接
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /opt/app

COPY internal/service/target/own-app.jar /opt/app/app.jar
COPY ci-cd/docker/app.sh /opt/app/app.sh

ENV JAVA_HOME=/opt/bitnami/java

EXPOSE 80 18081

ENTRYPOINT ["sh", "/opt/app/app.sh"]