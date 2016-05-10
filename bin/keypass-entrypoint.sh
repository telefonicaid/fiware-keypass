#!/bin/bash

# Check argument DB_HOST
DB_HOST_ARG=${1}
DB_HOST_VALUE=${2}

if [ "$DB_HOST_ARG" == "-dbhost" ]; then
    sed -i "s/mysql:\/\/localhost/mysql:\/\/"$DB_HOST_VALUE"/g" /opt/keypass/config.yml
    sleep 30
    java -jar /opt/keypass/keypass.jar db migrate /opt/keypass/config.yml
fi

java -jar /opt/keypass/keypass.jar server /opt/keypass/config.yml
