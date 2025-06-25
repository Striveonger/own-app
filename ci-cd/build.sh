#!/bin/bash
set -x

# select arch in "amd64" "arm64"; do
#     break;
# done

# echo $arch

# 获取当前脚本的上一级目录的绝对路径, 并将其赋值给 APP_WORKSHOP 变量
APP_WORKSHOP=$(realpath "$(dirname "$0")/..")

pushd "${APP_WORKSHOP}" || exit

# build service
mvn -f internal/service/pom.xml clean package -DskipTests -P k8s

# --------------------------------------------------------------------------------------------
# uninstall helm
helm uninstall own-example-app -n own
# kubectl delete ns own
# remove image
docker rmi striveonger/own-example-app:$(cat ./ci-cd/VERSION)

# --------------------------------------------------------------------------------------------
# build image
docker build -f ./ci-cd/docker/Dockerfile -t striveonger/own-example-app:$(cat ./ci-cd/VERSION) .

# docker push docker.io/striveonger/own-example-app:$(cat ./ci-cd/VERSION)

# package helm
helm package ci-cd/helm
mv own-example-app-$(cat ./ci-cd/VERSION).tgz ci-cd/package
helm show values ci-cd/helm > ci-cd/package/values.yaml

# deploy
# helm uninstall own-example-app -n own
helm upgrade --install own-example-app ci-cd/package/own-example-app-$(cat ./ci-cd/VERSION).tgz \
      --values ci-cd/package/values.yaml \
      --create-namespace --namespace own \
      --set app.config.applicationYaml.own.example-app.storage.memory.max-rows=3 \
      --set env[1].value=$SF_API_KEY

popd || exit