#!/bin/sh

APP_HOME="/opt/app/sparky"
CONFIG_HOME=${APP_HOME}/config

PROPS="-DAPP_HOME=${APP_HOME} -DCONFIG_HOME=${CONFIG_HOME} -Dlogging.config=${CONFIG_HOME}/logging/logback.xml"

set -x
jar ufv ${APP_HOME}/lib/sparkybe-onap-application*.jar -C ${CONFIG_HOME}/portal/ BOOT-INF/classes/portal.properties -C ${CONFIG_HOME}/portal/ BOOT-INF/classes/key.properties

#
# change the working directory so that Spring-Boot will pick up the config folder from the right path
#
cd $APP_HOME

java -Xms1024m -Xmx4096m $PROPS -jar ${APP_HOME}/lib/sparkybe-onap-application*.jar 