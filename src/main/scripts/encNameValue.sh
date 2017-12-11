# The script invokes the com.amdocs.aai.audit.security.encryption.EncryptedPropValue class to generate an encrypted value
# e.g
# ./encNameValue.sh odl.auth.password admin
# will return:
# odl.auth.password.x=f1e2c25183ef4b4ff655e7cd94d0c472
#
if [ "$#" -ne 2 ]; then
    echo "Illegal number of parameters (expected 2)"
    echo "Usage: `basename $0` <property name> <property value>" 1>&2
    exit 1
fi

# On Windows we must use a different CLASSPATH separator character
if [ "$(expr substr $(uname -s) 1 5)" == "MINGW" ]; then
	CPSEP=\;
else
	CPSEP=:
fi

java -cp ".${CPSEP}../extJars/*" com.att.aai.util.EncryptedPropValue -n $1 -v $2
