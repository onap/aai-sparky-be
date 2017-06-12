#!/bin/sh

BASEDIR="/opt/app/sparky"
AJSC_HOME="$BASEDIR"

if [ -z "$CONFIG_HOME" ]; then
	echo "CONFIG_HOME must be set in order to start up process"
	exit 1
fi

if [ -z "$KEY_STORE_PASSWORD" ]; then
	echo "KEY_STORE_PASSWORD must be set in order to start up process"
	exit 1
else
	echo -e "KEY_STORE_PASSWORD=$KEY_STORE_PASSWORD\n" >> $AJSC_CONF_HOME/etc/sysprops/sys-props.properties
fi

if [ -z "$KEY_MANAGER_PASSWORD" ]; then
	echo "KEY_MANAGER_PASSWORD must be set in order to start up process"
	exit 1
else
	echo -e "KEY_MANAGER_PASSWORD=$KEY_MANAGER_PASSWORD\n" >> $AJSC_CONF_HOME/etc/sysprops/sys-props.properties
fi

CLASSPATH="$AJSC_HOME/lib/ajsc-runner-2.0.0.jar"
CLASSPATH="$CLASSPATH:$AJSC_HOME/extJars/"
CLASSPATH="$CLASSPATH:$CONFIG_HOME/portal/"
PROPS="-DAJSC_HOME=$AJSC_HOME"
PROPS="$PROPS -DAJSC_CONF_HOME=$BASEDIR/bundleconfig/"
PROPS="$PROPS -Dlogback.configurationFile=$BASEDIR/bundleconfig/etc/logback.xml"
PROPS="$PROPS -DAJSC_SHARED_CONFIG=$AJSC_CONF_HOME"
PROPS="$PROPS -DAJSC_EXTERNAL_LIB_FOLDERS=$AJSC_HOME/commonLibs"
PROPS="$PROPS -DAJSC_EXTERNAL_PROPERTIES_FOLDERS=$AJSC_HOME/ajsc-shared-config/etc"
PROPS="$PROPS -DAJSC_SERVICE_NAMESPACE=ajsc-tier-support-ui"
PROPS="$PROPS -DAJSC_SERVICE_VERSION=v1"
PROPS="$PROPS -DSOACLOUD_SERVICE_VERSION=0.0.0"
PROPS="$PROPS -Dserver.port=8000"
PROPS="$PROPS -DCONFIG_HOME=$CONFIG_HOME"

echo $CLASSPATH

/usr/lib/jvm/java-8-openjdk-amd64/bin/java -Xms1024m -Xmx4096m  $PROPS -classpath $CLASSPATH com.att.ajsc.runner.Runner context=/ port=9517
