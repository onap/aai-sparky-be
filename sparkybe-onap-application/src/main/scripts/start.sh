#!/bin/sh

APP_HOME="/opt/app/sparky"
CONFIG_HOME=${APP_HOME}/appconfig

PROPS="-DAPP_HOME=${APP_HOME} -DCONFIG_HOME=${CONFIG_HOME}"

set -x
java -Xms1024m -Xmx4096m $PROPS -jar ${APP_HOME}/lib/sparkybe-onap-application*.jar --sparky.ssl.enabled=${UI_SSL_ENABLED}

