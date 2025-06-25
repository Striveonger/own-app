FROM ubuntu:22.04
# FROM alpine:3.22.0
LABEL maintainer="striveonger@163.com"
LABEL version="1.0.0"

COPY --from=ubuntu/jre:17_edge /opt/java /opt/java
# RUN apk add --no-cache openjdk17-jre


# RUN apt-get update && \
#     apt-get install -y curl && \
#     apt-get install -y wget && \
#     apt-get install -y vim && \
#     apt-get install -y net-tools && \
#     apt-get install -y iputils-ping && \
#     apt-get install -y iproute2 && \
#     apt-get install -y telnet && \
#     apt-get install -y netcat && \
#     apt-get install -y lsof
# RUN apt-get install -y tzdata

# 设置时区变量及符号链接
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /opt/app

COPY internal/service/target/own-example-app.jar /opt/app/app.jar
COPY ci-cd/docker/app.sh /opt/app/app.sh

ENV JAVA_HOME=/opt/java

EXPOSE 18081

ENTRYPOINT ["sh", "/opt/app/app.sh"]