#!/bin/bash
set -x

# JVM Configuration
JAVA_OPT="${JAVA_OPT}"

APP_PORT=18081
# APP_JAR="app.jar"

JAVA_OPT="${JAVA_OPT} -Dserver.port=${APP_PORT} -Dloader.path=BOOT-INF/classes:BOOT-INF/lib"

exec ${JAVA_HOME}/bin/java -cp /opt/app ${JAVA_OPT} org.springframework.boot.loader.launch.JarLauncher