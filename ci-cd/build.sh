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

# build ui
rm -rf website/dist website/node_modules
pnpm -C website install
pnpm -C website run build

# --------------------------------------------------------------------------------------------
# kubectl delete ns own

# uninstall helm
helm uninstall own-app -n own --wait

# remove image
docker rmi striveonger/own-app-api:$(cat ./ci-cd/VERSION)
docker rmi striveonger/own-app-ui:$(cat ./ci-cd/VERSION)
# --------------------------------------------------------------------------------------------
# build image
docker build -f ./ci-cd/docker/api/Dockerfile -t striveonger/own-app-api:$(cat ./ci-cd/VERSION) .
docker build -f ./ci-cd/docker/ui/Dockerfile -t striveonger/own-app-ui:$(cat ./ci-cd/VERSION) .

# docker push docker.io/striveonger/own-app-api:$(cat ./ci-cd/VERSION)
# docker push docker.io/striveonger/own-app-ui:$(cat ./ci-cd/VERSION)

# package helm
# helm package ci-cd/helm
# mv own-app-$(cat ./ci-cd/VERSION).tgz ci-cd/package
# helm show values ci-cd/helm > ci-cd/package/values.yaml

# deploy
# helm upgrade --install own-app ci-cd/package/own-app$(cat ./ci-cd/VERSION).tgz \
#      --create-namespace --namespace own \
#      --values ci-cd/package/values.yaml \
#      --set own-app-api.env[1].value=$SF_API_KEY \
#      --set global.config.app.own.app.storage.memory.max-rows=3

helm upgrade --install own-app ci-cd/helm \
    --create-namespace --namespace own \
    --values ci-cd/helm/values.yaml \
    --set own-app-api.env[1].value=$SF_API_KEY \
    --set global.config.app.own.app.storage.memory.max-rows=3

popd || exit