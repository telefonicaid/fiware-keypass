FROM centos:7.9.2009

MAINTAINER IoT team

# DB_ENDPOINT host[:port]
ENV DB_ENDPOINT localhost

ENV KEYPASS_VERSION 1.8.0
ENV JAVA_VERSION "1.8.0"
ENV JAVA_HOME /usr/lib/jvm/java-${JAVA_VERSION}-openjdk

COPY . /opt/keypass/
WORKDIR /opt/keypass

RUN \
    # Install dependencies
    yum update -y && yum install -y wget curl unzip && \
    yum install -y epel-release && yum update -y epel-release && \
    yum install -y java-${JAVA_VERSION}-openjdk java-${JAVA_VERSION}-openjdk-devel && \
    yum install -y tcping && \
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
    cp ./conf/config.yml_docker /opt/keypass/config.yml && \
    cp ./bin/keypass-daemon.sh /opt/keypass/ && \
    cp ./bin/keypass-entrypoint.sh /opt/keypass/ && \
    sed -i "s/port: 8080/port: 7070/g" /opt/keypass/config.yml && \
    sed -i "s/port: 8081/port: 7071/g" /opt/keypass/config.yml && \
    sed -i "s/bindHost: 127.0.0.1/bindHost: 0.0.0.0/g" /opt/keypass/config.yml && \
    sed -i "s/mysql:\/\/localhost/mysql:\/\/"$DB_ENDPOINT"/g" /opt/keypass/config.yml && \
    # Cleaning unused files...
    mvn clean && rm -rf /opt/maven && rm -rf ~/.m2 && \
    yum erase -y java-${JAVA_VERSION}-openjdk-devel libss && unset JAVA_HOME && \
    # Clean yum data
    yum clean all && rm -rf /var/lib/yum/yumdb && rm -rf /var/lib/yum/history && \
    # Erase without dependencies unneded rpm packages
    rpm -qa alsa-lib atk cairo cups-libs flac fontconfig gdk-pixbuf2 giflib groff gtk2 jasper-libs jpackage-utils \
      libjpeg-turbo libsndfile libthai libtiff libvorbis libxcb libX* \
      pango pixman pulseaudio-libs redhat-logos xorg-x11* | xargs -r rpm -e --nodeps && \
    # Rebuild rpm data files
    rpm --rebuilddb && \
    # Delete unused locales. Only preserve en_US and the locale aliases
    rm -rf /usr/share/zoneinfo && \
    find /usr/share/locale -mindepth 1 -maxdepth 1 ! -name 'en_US' ! -name 'locale.alias' | xargs -r rm -r && \
    bash -c 'localedef --list-archive | grep -v -e "en_US" | xargs localedef --delete-from-archive' && \
    # We use cp instead of mv as to refresh locale changes for ssh connections
    # We use /bin/cp instead of cp to avoid any alias substitution, which in some cases has been problematic
    /bin/cp -f /usr/lib/locale/locale-archive /usr/lib/locale/locale-archive.tmpl && \
    build-locale-archive && \
    find /opt/keypass -name '.[^.]*' 2>/dev/null | xargs -r rm -rf && \
    # We don't need glibc locale data
    # This cannot be removed using yum as yum uses hard dependencies and doing so will uninstall essential packages
    rm -rf /usr/share/i18n /usr/{lib,lib64}/gconv && \
    # We don't need wallpapers
    rm -rf /usr/share/wallpapers/* && \
    # Don't need old log files inside docker images
    rm -rf /root/*log* /tmp/* /var/log/*log* && \
    chown -R 1000:1000 /opt/keypass

# Define the entry point
ENTRYPOINT ["/opt/keypass/keypass-entrypoint.sh"]

EXPOSE 7070 7071

HEALTHCHECK CMD curl --fail http://localhost:7070/pap/v1/subject/test  -H 'fiware-service: test' || exit 1
