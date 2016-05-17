#!/bin/bash

# Check argument DB_HOST
DB_HOST_ARG=${1}
DB_HOST_VALUE=${2}
DB_HOST_PORT=3306


if [ "$DB_HOST_ARG" == "-dbhost" ]; then
    sed -i "s/mysql:\/\/localhost/mysql:\/\/"$DB_HOST_VALUE"/g" /opt/keypass/config.yml
    # Wait until DB is up
    while ! nc -z $DB_HOST_VALUE $DB_HOST_PORT ; do sleep 10; done
    java -jar /opt/keypass/keypass.jar db migrate /opt/keypass/config.yml
fi

java -jar /opt/keypass/keypass.jar server /opt/keypass/config.yml
