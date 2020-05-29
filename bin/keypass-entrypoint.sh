#!/bin/bash

echo "INFO: keypass entrypoint start"

[[ "${DB_HOST_VALUE}" == "" ]] && DB_HOST_VALUE=localhost:3306
# Default MySQL host name
[[ "${DB_HOST_NAME}" == "" ]] && DB_HOST_NAME=localhost
# Default MySQL port 3306
[[ "${DB_HOST_PORT}" == "" ]] && DB_HOST_PORT=3306
# DBTIMEOUT in seconds. Default to 60 seconds
[[ "${DBTIMEOUT}" == "" ]] && export DBTIMEOUT=60

# Check argument DB_HOST if provided
while [[ $# -gt 0 ]]; do
    PARAM=`echo $1`
    VALUE=`echo $2`
    case "$PARAM" in
        -dbhost)
            DB_HOST_VALUE=$VALUE
            DB_HOST_NAME="$(echo "${DB_HOST_VALUE}" | awk -F: '{print $1}')"
            DB_HOST_PORT="$(echo "${DB_HOST_VALUE}" | awk -F: '{print $2}')"
            ;;
        *)
            echo "not found"
            # Do nothing
            ;;
    esac
    shift
    shift
done

echo "INFO: MySQL endpoint <${DB_HOST_VALUE}>"
echo "INFO: DB_HOST_NAME <${DB_HOST_NAME}>"
echo "INFO: DB_HOST_PORT <${DB_HOST_PORT}>"

sed -i "s/mysql:\/\/localhost/mysql:\/\/${DB_HOST_VALUE}/g" /opt/keypass/config.yml

# Wait until DB is up or exit if timeout
# Current time in seconds
STARTTIME=$(date +%s)
while ! tcping -t 1 ${DB_HOST_NAME} ${DB_HOST_PORT}
do
    [[ $(($(date +%s) - ${DBTIMEOUT})) -lt ${STARTTIME} ]] || { echo "ERROR: Timeout MySQL endpoint <${DB_HOST_NAME}:${DB_HOST_PORT}>" >&2; exit 3; }
    echo "INFO: Wait for MySQL endpoint <${DB_HOST_NAME}:${DB_HOST_PORT}>"
    sleep 2
done
java -jar /opt/keypass/keypass.jar db migrate /opt/keypass/config.yml

java -jar /opt/keypass/keypass.jar server /opt/keypass/config.yml
