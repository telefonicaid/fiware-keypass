ARG  IMAGE_TAG=12.10-slim
FROM debian:${IMAGE_TAG}

MAINTAINER IoT team

ARG CLEAN_DEV_TOOLS

# DB_ENDPOINT host[:port]
ENV DB_ENDPOINT localhost
ENV KEYPASS_VERSION 1.14.0
ENV JAVA_VERSION "1.17.0"
ENV JAVA_HOME /usr/lib/jvm/java-${JAVA_VERSION}-openjdk-amd64
ENV CLEAN_DEV_TOOLS ${CLEAN_DEV_TOOLS:-1}

COPY . /opt/keypass/
WORKDIR /opt/keypass

RUN \
    apt-get -y update && \
    apt-get -y upgrade && \
    # Install dependencies
    apt-get -y install openjdk-17-jdk && \
    apt-get -y install postgresql-client && \
    apt-get -y install \
      curl \
      netcat-traditional \
      maven && \
      # Build keypass
    mvn clean package && \
    mkdir -p /opt/keypass/log && \
    # Copy jar and conf to proper location
    cp target/keypass-$KEYPASS_VERSION.jar /opt/keypass/keypass.jar && \
    cp ./conf/config.yml_docker /opt/keypass/config.yml && \
    cp ./bin/keypass-daemon.sh /opt/keypass/ && \
    cp ./bin/keypass-entrypoint.sh /opt/keypass/ && \
    sed -i "s/port: 8080/port: 7070/g" /opt/keypass/config.yml && \
    sed -i "s/port: 8081/port: 7071/g" /opt/keypass/config.yml && \
    sed -i "s/bindHost: 127.0.0.1/bindHost: 0.0.0.0/g" /opt/keypass/config.yml && \
    sed -i "s/mysql:\/\/localhost/mysql:\/\/"$DB_ENDPOINT"/g" /opt/keypass/config.yml && \
    # Cleaning unused files...
    mvn clean && rm -rf /opt/maven && rm -rf ~/.m2 && \
    #echo "INFO: Cleaning unused software..." && \
    apt-get clean && \
    apt-get -y autoremove --purge && \
    if [ ${CLEAN_DEV_TOOLS} -eq 0 ] ; then exit 0 ; fi && \
    # remove the same packages we installed at the beginning to build Keypass
    apt-get -y remove maven && \
    apt-get -y autoremove --purge && \
    # Don't need old log files inside docker images
    rm -f /var/log/*log && \
    chown -R 1000:1000 /opt/keypass

# Define the entry point
ENTRYPOINT ["/opt/keypass/keypass-entrypoint.sh"]

EXPOSE 7070 7071

HEALTHCHECK --interval=60s --timeout=5s --start-period=10s \
            CMD curl --fail http://localhost:7070/pap/v1/subject/healthcheck  -H 'fiware-service: healthcheck' || exit 1
