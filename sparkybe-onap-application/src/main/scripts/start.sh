#!/bin/sh

APP_HOME="/opt/app/sparky"
CONFIG_HOME=${APP_HOME}/config

PROPS="-DAPP_HOME=${APP_HOME} -DCONFIG_HOME=${CONFIG_HOME} -Dlogging.config=${CONFIG_HOME}/logging/logback.xml"

# Changes related to:AAI-2180
# Change aai sparky container processes to run as non-root on the host
USER_ID=${LOCAL_USER_ID:-9001}
GROUP_ID=${LOCAL_GROUP_ID:-9001}
UI_LOGS=/var/log/onap/AAI-UI

if [ $(cat /etc/passwd | grep aaiadmin | wc -l) -eq 0 ]; then

        groupadd aaiadmin -g ${GROUP_ID} || {
                echo "Unable to create the group id for ${GROUP_ID}";
                exit 1;
        }
        useradd --shell=/bin/bash -u ${USER_ID} -g ${GROUP_ID} -o -c "" -m aaiadmin || {
                echo "Unable to create the user id for ${USER_ID}";
                exit 1;
        }
fi;
chown -R aaiadmin:aaiadmin ${MICRO_HOME}
chown -R aaiadmin:aaiadmin ${APP_HOME}
chown -R aaiadmin:aaiadmin ${UI_LOGS}
find ${MICRO_HOME}  -name "*.sh" -exec chmod +x {} +

gosu aaiadmin ln -s /logs $MICRO_HOME/logs
JAVA_CMD="exec gosu aaiadmin java";
JAR_CMD="exec gosu aaiadmin jar";

###
set -x
{JAR_CMD} ufv ${APP_HOME}/lib/sparkybe-onap-application*.jar \
    -C ${CONFIG_HOME}/portal/ BOOT-INF/classes/portal.properties \
    -C ${CONFIG_HOME}/portal/ BOOT-INF/classes/key.properties > /dev/null 2>&1

#
# change the working directory so that Spring-Boot will pick up the config folder from the right path
#
cd $APP_HOME

${JAVA_CMD} -Xms1024m -Xmx4096m $PROPS -jar ${APP_HOME}/lib/sparkybe-onap-application*.jar 
