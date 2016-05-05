#!/bin/bash

# Check argument DB_HOST
DB_HOST_ARG=${1}
DB_HOST_VALUE=${2}

if [ $DB_HOST_ARG == "-dbhost"] ; then
    sed -i "s/mysql:\/\/localhost/mysql:\/\/"$DB_HOST_VALUE"/g" /opt/keypass/config.yml
    java -jar keypass.jar db migrate config.yml
fi

java -jar keypass.jar server config.yml
