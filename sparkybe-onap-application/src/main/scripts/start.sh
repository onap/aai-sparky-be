#!/bin/sh

APP_HOME="/opt/app/sparky"
CONFIG_HOME=${APP_HOME}/appconfig

PROPS="-DAPP_HOME=${APP_HOME} -DCONFIG_HOME=${CONFIG_HOME} -Dlogging.config=${CONFIG_HOME}/logging/logback.xml"

set -x
jar ufv ${APP_HOME}/lib/sparkybe-onap-application*.jar -C ${CONFIG_HOME}/portal/ BOOT-INF/classes/portal.properties
java -Xms1024m -Xmx4096m $PROPS -jar ${APP_HOME}/lib/sparkybe-onap-application*.jar 

