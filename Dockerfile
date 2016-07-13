FROM centos:6

MAINTAINER IoT team

ENV DB_HOST localhost
ENV KEYPASS_VERSION 1.1.0
ENV JAVA_HOME /usr/lib/jvm/java-1.7.0-openjdk.x86_64

COPY . /opt/keypass/
WORKDIR /opt/keypass

RUN \
    # Install dependencies
    yum update -y && yum install -y wget nc unzip && \
    yum install -y epel-release && yum update -y epel-release && \
    yum install -y java-1.7.0-openjdk java-1.7.0-openjdk-devel && \
    # Install Maven
    wget -c http://ftp.cixug.es/apache/maven/maven-3/3.2.5/binaries/apache-maven-3.2.5-bin.zip && \
    unzip -oq apache-maven-3.2.5-bin.zip  && \
    mv apache-maven-3.2.5 /opt/maven && rm -f apache-maven-3.2.5-bin.zip && \
    ln -fs /opt/maven/bin/mvn /usr/bin/mvn && \
    # Build keypass
    mvn clean package && \
    mkdir -p /opt/keypass/log && \
    # Copy jar and conf to proper location
    cp target/keypass-$KEYPASS_VERSION.jar /opt/keypass/keypass.jar && \
    cp ./conf/config.yml /opt/keypass/ && \
    cp ./bin/keypass-daemon.sh /opt/keypass/ && \
    cp ./bin/keypass-entrypoint.sh /opt/keypass/ && \
    sed -i "s/port: 8080/port: 7070/g" /opt/keypass/config.yml && \
    sed -i "s/port: 8081/port: 7071/g" /opt/keypass/config.yml && \
    sed -i "s/bindHost: 127.0.0.1/bindHost: 0.0.0.0/g" /opt/keypass/config.yml && \
    sed -i "s/mysql:\/\/localhost/mysql:\/\/"$DB_HOST"/g" /opt/keypass/config.yml && \
    # Cleaning unused files...
    mvn clean && rm -rf /opt/maven && rm -rf ~/.m2 && \
    yum erase -y java-1.7.0-openjdk-devel && unset JAVA_HOME && \
    rpm -qa redhat-logos gtk2 pulseaudio-libs libvorbis jpackage-utils groff alsa-lib atk cairo libX* | xargs -r rpm -e --nodeps && yum -y erase libss && yum clean all && rpm -vv --rebuilddb && \
    rm -rf /var/lib/yum/yumdb && \
    rm -rf /var/lib/yum/history && find /usr/share/locale -mindepth 1 -maxdepth 1 ! -name 'en' ! -name 'es' ! -name 'es_ES' | xargs rm -r && \
    rm -f /var/log/*log

# Define the entry point
ENTRYPOINT ["/opt/keypass/keypass-entrypoint.sh"]

EXPOSE 7070 7071

