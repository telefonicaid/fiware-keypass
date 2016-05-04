FROM centos:6

MAINTAINER IoT team

ENV DB_HOST localhost

RUN \
    # Install dependencies
    yum update -y && yum install -y wget  && \
    wget http://dl.fedoraproject.org/pub/epel/6/x86_64/epel-release-6-8.noarch.rpm && \
    yum localinstall -y --nogpgcheck epel-release-6-8.noarch.rpm  && \
    yum install -y rpm-build git java-1.7.0-openjdk java-1.7.0-openjdk-devel

ENV JAVA_HOME /usr/lib/jvm/java-1.7.0-openjdk.x86_64

RUN \
    # Install Maven
    wget -c http://ftp.cixug.es/apache/maven/maven-3/3.2.5/binaries/apache-maven-3.2.5-bin.zip  && \
    unzip -oq apache-maven-3.2.5-bin.zip  && \
    cp -rf apache-maven-3.2.5 /opt/maven  && \
    ln -fs /opt/maven/bin/mvn /usr/bin/mvn && \
    # Install MySQL (client at least?)
    yum -y install mysql


RUN mkdir -p /opt/keypass
COPY . /opt/keypass
WORKDIR /opt/keypass
RUN mvn package

RUN mkdir -p /opt/keypass
RUN mkdir -p /opt/keypass/log
COPY ./target/keypass-*.jar /opt/keypass/keypass.jar
COPY ./conf/config.yml /opt/keypass/
COPY ./bin/keypass-daemon.sh /opt/keypass/


RUN sed -i "s/port: 8080/port: 7070/g" /opt/keypass/config.yml
RUN sed -i "s/port: 8081/port: 7071/g" /opt/keypass/config.yml
RUN sed -i "s/mysql:\/\/localhost/mysql:\/\/'"$DB_HOST"'/g" /opt/keypass/config.yml

WORKDIR /opt/keypass

RUN java -jar keypass.jar db migrate config.yml

CMD ["java -jar keypass.jar server config.yml"]

EXPOSE 7070
EXPOSE 7071
