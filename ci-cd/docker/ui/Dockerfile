FROM nginx:1.27.2
LABEL maintainer="striveonger@163.com"
LABEL version="1.0.0"

WORKDIR /opt/app

COPY website/dist /opt/app
COPY ci-cd/docker/ui/nginx.conf /etc/nginx/conf.d/app.conf

RUN chmod -R 755 /opt/app

EXPOSE 80