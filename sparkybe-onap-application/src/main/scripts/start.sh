#!/bin/sh

BASEDIR="/opt/app/sparky"
MICRO_HOME="$BASEDIR"
CONFIG_HOME=${MICRO_HOME}/config

if [ -z "$CONFIG_HOME" ]; then
	echo "CONFIG_HOME must be set in order to start up process"
	exit 1
fi
 
PROPS="-DCONFIG_HOME=${CONFIG_HOME}"

#echo $CLASSPATH

set -x
exec java -Xms1024m -Xmx4096m $PROPS -jar ${MICRO_HOME}/sparky-be.jar --spring.config.name=sparky-be-application  




#if [ -z "$KEY_STORE_PASSWORD" ]; then
#	echo "KEY_STORE_PASSWORD must be set in order to start up process"
#	exit 1
#else
#        sed -i /"KEY_STORE_PASSWORD"/d $AJSC_CONF_HOME/etc/sysprops/sys-props.properties
#	echo "KEY_STORE_PASSWORD=$KEY_STORE_PASSWORD" >> $AJSC_CONF_HOME/etc/sysprops/sys-props.properties
#fi

#if [ -z "$KEY_MANAGER_PASSWORD" ]; then
#	echo "KEY_MANAGER_PASSWORD must be set in order to start up process"
#	exit 1
#else
#        sed -i /"KEY_MANAGER_PASSWORD"/d $AJSC_CONF_HOME/etc/sysprops/sys-props.properties
#	echo "KEY_MANAGER_PASSWORD=$KEY_MANAGER_PASSWORD" >> $AJSC_CONF_HOME/etc/sysprops/sys-props.properties
#fi

#CLASSPATH="$AJSC_HOME/lib/ajsc-runner-2.0.0.jar"
#CLASSPATH="$CLASSPATH:$AJSC_HOME/extJars/"
#CLASSPATH="$CLASSPATH:$CONFIG_HOME/portal/"
#PROPS="-DAJSC_HOME=$AJSC_HOME"
#PROPS="$PROPS -DAJSC_CONF_HOME=$BASEDIR/bundleconfig/"
#PROPS="$PROPS -Dlogback.configurationFile=$BASEDIR/bundleconfig/etc/logback.xml"
#PROPS="$PROPS -DAJSC_SHARED_CONFIG=$AJSC_CONF_HOME"
#PROPS="$PROPS -DAJSC_EXTERNAL_LIB_FOLDERS=$AJSC_HOME/commonLibs"
#PROPS="$PROPS -DAJSC_EXTERNAL_PROPERTIES_FOLDERS=$AJSC_HOME/ajsc-shared-config/etc"
#PROPS="$PROPS -DAJSC_SERVICE_NAMESPACE=ajsc-tier-support-ui"
#PROPS="$PROPS -DAJSC_SERVICE_VERSION=v1"
#PROPS="$PROPS -DSOACLOUD_SERVICE_VERSION=0.0.0"
#PROPS="$PROPS -Dserver.port=8000"
#PROPS="$PROPS -DCONFIG_HOME=$CONFIG_HOME"

#echo $CLASSPATH

#if [ "$UI_HTTPS_PORT" ]; then
#	/usr/lib/jvm/java-8-openjdk-amd64/bin/java -Xms1024m -Xmx4096m  $PROPS -classpath $CLASSPATH com.att.ajsc.runner.Runner context=/ sslport=$UI_HTTPS_PORT
#elif [ "$UI_HTTP_PORT" ]; then
#	/usr/lib/jvm/java-8-openjdk-amd64/bin/java -Xms1024m -Xmx4096m  $PROPS -classpath $CLASSPATH com.att.ajsc.runner.Runner context=/ port=$UI_HTTP_PORT
#fi
