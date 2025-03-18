#!/bin/bash

echo "INFO: keypass entrypoint start"

# Default DB host name
[[ "${KEYPASS_DB_HOST_NAME}" == "" ]] && KEYPASS_DB_HOST_NAME=localhost
# Default DB port 3306
[[ "${KEYPASS_DB_HOST_PORT}" == "" ]] && KEYPASS_DB_HOST_PORT=3306
# DB_TIMEOUT in seconds. Default to 60 seconds
[[ "${KEYPASS_DB_TIMEOUT}" == "" ]] && export KEYPASS_DB_TIMEOUT=60
# Default DB user
[[ "${KEYPASS_DB_USER}" == "" ]] && export KEYPASS_DB_USER=keypass
# Default DB password
[[ "${KEYPASS_DB_PASSWORD}" == "" ]] && export KEYPASS_DB_PASSWORD=keypass
# LOG_LEVEL. Default INFO
[[ "${KEYPASS_LOG_LEVEL}" == "" ]] && export KEYPASS_LOG_LEVEL=INFO
# Default DB type
[[ "${KEYPASS_DB_TYPE}" == "" ]] && export KEYPASS_DB_TYPE=mysql

export JDK_JAVA_OPTIONS='--add-opens java.base/java.lang=ALL-UNNAMED'

# Check argument DB_HOST if provided
while [[ $# -gt 0 ]]; do
    PARAM=`echo $1`
    VALUE=`echo $2`
    case "$PARAM" in
        -dbhost)
            KEYPASS_DB_HOST_VALUE=$VALUE
            KEYPASS_DB_HOST="$(echo "${KEYPASS_DB_HOST_VALUE}" | awk -F: '{print $1}')"
            KEYPASS_DB_PORT="$(echo "${KEYPASS_DB_HOST_VALUE}" | awk -F: '{print $2}')"
            # Ensure DB valid hostname
            if [ "${KEYPASS_DB_HOST}" != "" ]; then
                KEYPASS_DB_HOST_NAME=${KEYPASS_DB_HOST}
            fi
            # Ensure DB valid port
            if [ "${KEYPASS_DB_PORT}" != "" ]; then
                KEYPASS_DB_HOST_PORT=${KEYPASS_DB_PORT}
            fi
            ;;
        *)
            echo "not found"
            # Do nothing
            ;;
    esac
    shift
    shift
done

echo "INFO: LOG LEVEL <${KEYPASS_LOG_LEVEL}>"
sed -i "s/INFO/${KEYPASS_LOG_LEVEL}/g" /opt/keypass/config.yml

echo "INFO: DB endpoint <${KEYPASS_DB_HOST_VALUE}>"
echo "INFO: KEYPASS_DB_HOST_NAME <${KEYPASS_DB_HOST_NAME}>"
echo "INFO: KEYPASS_DB_HOST_PORT <${KEYPASS_DB_HOST_PORT}>"
echo "INFO: KEYPASS_DB_USER <${KEYPASS_DB_USER}>"
echo "INFO: KEYPASS_DB_PASSWORD <${KEYPASS_DB_PASSWORD}>"

if [ "$KEYPASS_DB_TYPE" == "psql" ]; then
    sed -i "s/com.mysql.cj.jdbc.Driver/org.postgresql.Driver/g" /opt/keypass/config.yml
    sed -i "s/mysql:\/\/localhost/postgresql:\/\/${KEYPASS_DB_HOST_NAME}:${KEYPASS_DB_HOST_PORT}/g" /opt/keypass/config.yml
    sed -i "s/org.hibernate.dialect.MySQLDialect/org.hibernate.dialect.PostgreSQLDialect/g" /opt/keypass/config.yml
else
    sed -i "s/mysql:\/\/localhost/mysql:\/\/${KEYPASS_DB_HOST_NAME}:${KEYPASS_DB_HOST_PORT}/g" /opt/keypass/config.yml
fi

sed -i "s/user: keypass/user: ${KEYPASS_DB_USER}/g" /opt/keypass/config.yml
sed -i "s/password: keypass/password: ${KEYPASS_DB_PASSWORD}/g" /opt/keypass/config.yml

# Wait until DB is up or exit if timeout
# Current time in seconds
STARTTIME=$(date +%s)
while ! nc -zvw10 ${KEYPASS_DB_HOST_NAME} ${KEYPASS_DB_HOST_PORT}
do
    [[ $(($(date +%s) - ${KEYPASS_DB_TIMEOUT})) -lt ${STARTTIME} ]] || { echo "ERROR: Timeout DB endpoint <${KEYPASS_DB_HOST_NAME}:${KEYPASS_DB_HOST_PORT}>" >&2; exit 3; }
    echo "INFO: Wait for DB endpoint <${KEYPASS_DB_HOST_NAME}:${KEYPASS_DB_HOST_PORT}>"
    sleep 2
done
java -jar /opt/keypass/keypass.jar db migrate /opt/keypass/config.yml

java -jar /opt/keypass/keypass.jar server /opt/keypass/config.yml
