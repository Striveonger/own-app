#!/bin/bash
set -x

# JVM Configuration
JAVA_OPT="${JAVA_OPT}"

APP_PORT=18081
APP_JAR="app.jar"

JAVA_OPT="${JAVA_OPT} -Dserver.port=${APP_PORT}"

exec ${JAVA_HOME}/bin/java ${JAVA_OPT} -jar ${APP_JAR}