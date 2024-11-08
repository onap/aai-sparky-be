#!/bin/sh

APP_HOME="/opt/app/sparky"
CONFIG_HOME=${APP_HOME}/config

PROPS="-DAPP_HOME=${APP_HOME} -DCONFIG_HOME=${CONFIG_HOME} -Dlogging.config=${CONFIG_HOME}/logging/logback.xml"

find ${MICRO_HOME}  -name "*.sh" -exec chmod +x {} +

JAVA_CMD="exec java";
JAR_CMD="exec jar";

###
set -x
{JAR_CMD} ufv ${APP_HOME}/lib/sparkybe-onap-application*.jar \
    -C ${CONFIG_HOME}/portal/ BOOT-INF/classes/portal.properties \
    -C ${CONFIG_HOME}/portal/ BOOT-INF/classes/key.properties > /dev/null 2>&1

#
# change the working directory so that Spring-Boot will pick up the config folder from the right path
#
cd $APP_HOME

${JAVA_CMD} ${PRE_JVM_ARGS} ${JVM_ARGS} $PROPS -jar ${APP_HOME}/lib/sparkybe-onap-application*.jar
