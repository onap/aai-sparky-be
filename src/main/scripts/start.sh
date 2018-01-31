#!/bin/sh

BASEDIR="/opt/app/sparky"
AJSC_HOME="$BASEDIR"
AJSC_CONF_HOME="$BASEDIR/bundleconfig/"

if [ -z "$CONFIG_HOME" ]; then
	echo "CONFIG_HOME must be set in order to start up process"
	exit 1
fi

if [ -z "$KEY_STORE_PASSWORD" ]; then
	echo "KEY_STORE_PASSWORD must be set in order to start up process"
	exit 1
else
        sed -i /"KEY_STORE_PASSWORD"/d $AJSC_CONF_HOME/etc/sysprops/sys-props.properties
	echo "KEY_STORE_PASSWORD=$KEY_STORE_PASSWORD" >> $AJSC_CONF_HOME/etc/sysprops/sys-props.properties
fi

if [ -z "$KEY_MANAGER_PASSWORD" ]; then
	echo "KEY_MANAGER_PASSWORD must be set in order to start up process"
	exit 1
else
        sed -i /"KEY_MANAGER_PASSWORD"/d $AJSC_CONF_HOME/etc/sysprops/sys-props.properties
	echo "KEY_MANAGER_PASSWORD=$KEY_MANAGER_PASSWORD" >> $AJSC_CONF_HOME/etc/sysprops/sys-props.properties
fi

if [ -z "$UI_HTTPS_PORT" ] && [ -z "$UI_HTTP_PORT" ]; then
	echo "Either UI_HTTPS_PORT or UI_HTTP_PORT must be set in order to start up process"
	exit 1
fi

# Add any routes configured at deploy time to the sparky deployment
if [ -n "$DYNAMIC_ROUTES" ]; then
   if [ -e /opt/app/sparky/services/inventory-ui-service_v1.zip ]; then
      echo "Adding the following dynamic routes to the deployment: "
      mkdir -p /tmp/sparky/v1/routes
      for f in `ls $DYNAMIC_ROUTES`
         do
            cp $DYNAMIC_ROUTES/$f /tmp/sparky/v1/routes
            echo "Adding dynamic route $DYNAMIC_ROUTES/$f"
     done
     jar uf /opt/app/sparky/services/inventory-ui-service_v1.zip* -C /tmp/ sparky
     rm -rf /tmp/sparky
  fi
fi

# Add any spring bean configuration files to the sparky deployment
if [ -n "$SERVICE_BEANS" ]; then
   if [ -e /opt/app/sparky/services/inventory-ui-service_v1.zip ]; then
      echo "Adding the following dynamic service beans to the deployment: "
      mkdir -p /tmp/sparky/v1/conf
      for f in `ls $SERVICE_BEANS`
      do
         cp $SERVICE_BEANS/$f /tmp/sparky/v1/conf
         echo "Adding dynamic service bean $SERVICE_BEANS/$f"
      done
      jar uf /opt/app/sparky/services/inventory-ui-service_v1.zip* -C /tmp/ sparky
      rm -rf /tmp/sparky
   fi
fi

# Add any dynamic component configuration files to the sparky deployment
if [ -n "$COMPLIB" ]; then
   if [ -e /opt/app/sparky/services/inventory-ui-service_v1.zip ]; then
      echo "Adding the following dynamic libraries to the deployment: "
      mkdir -p /tmp/sparky/v1/lib
      for f in `ls $COMPLIB`
      do
           cp $COMPLIB/$f /tmp/sparky/v1/lib
           echo "Adding dynamic library $COMPLIB/$f"
      done
      jar uf /opt/app/sparky/services/inventory-ui-service_v1.zip* -C /tmp/ sparky
      rm -rf /tmp/sparky
   fi
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

if [ "$UI_HTTPS_PORT" ]; then
	/usr/lib/jvm/java-8-openjdk-amd64/bin/java -Xms1024m -Xmx4096m  $PROPS -classpath $CLASSPATH com.att.ajsc.runner.Runner context=/ sslport=$UI_HTTPS_PORT
elif [ "$UI_HTTP_PORT" ]; then
	/usr/lib/jvm/java-8-openjdk-amd64/bin/java -Xms1024m -Xmx4096m  $PROPS -classpath $CLASSPATH com.att.ajsc.runner.Runner context=/ port=$UI_HTTP_PORT
fi
