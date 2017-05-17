#!/bin/bash

echo "INFO: keypass entrypoint start"

# Check argument DB_HOST
DB_HOST_ARG=${1}
# DB_HOST_VALUE can be hostname[:port]
DB_HOST_VALUE=${2}
DB_HOST_NAME="$(echo "${DB_HOST_VALUE}" | awk -F: '{print $1}')"
DB_HOST_PORT="$(echo "${DB_HOST_VALUE}" | awk -F: '{print $2}')"
# Default MySQL port 3306
[[ "${DB_HOST_PORT}" == "" ]] && DB_HOST_PORT=3306
# DBTIMEOUT in seconds. Default to 60 seconds
[[ "${DBTIMEOUT}" == "" ]] && export DBTIMEOUT=60

if [ "${DB_HOST_ARG}" == "-dbhost" ]; then
    echo "INFO: MySQL endpoint <${DB_HOST_VALUE}>"
    echo "INFO: DB_HOST_NAME <${DB_HOST_NAME}>"
    echo "INFO: DB_HOST_PORT <${DB_HOST_PORT}>"
    [[ "${DB_HOST_NAME}" == "" ]] && echo "ERROR: MySQL hostname not provided" >&2 && exit 2
    sed -i "s/mysql:\/\/localhost/mysql:\/\/${DB_HOST_VALUE}/g" /opt/keypass/config.yml
    # Wait until DB is up or exit if timeout
    # Current time in seconds
    STARTTIME=$(date +%s)
    while ! nc -z ${DB_HOST_NAME} ${DB_HOST_PORT}
    do
        [[ $(($(date +%s) - ${DBTIMEOUT})) -lt ${STARTTIME} ]] || { echo "ERROR: Timeout MySQL endpoint <${DB_HOST_NAME}:${DB_HOST_PORT}>" >&2; exit 3; }
        echo "INFO: Wait for MySQL endpoint <${DB_HOST_NAME}:${DB_HOST_PORT}>"
        sleep 2
    done
    java -jar /opt/keypass/keypass.jar db migrate /opt/keypass/config.yml
fi

java -jar /opt/keypass/keypass.jar server /opt/keypass/config.yml
