FROM centos:6

MAINTAINER IoT team

RUN yum update -y && yum install -y wget
RUN wget http://dl.fedoraproject.org/pub/epel/6/x86_64/epel-release-6-8.noarch.rpm
RUN yum localinstall -y --nogpgcheck epel-release-6-8.noarch.rpm
RUN yum install -y rpm-build git java-1.7.0-openjdk java-1.7.0-openjdk-devel
ENV JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk.x86_64

# Install Maven
RUN wget -c http://ftp.cixug.es/apache/maven/maven-3/3.2.5/binaries/apache-maven-3.2.5-bin.zip
RUN unzip -oq apache-maven-3.2.5-bin.zip
RUN cp -rf apache-maven-3.2.5 /opt/maven
RUN ln -fs /opt/maven/bin/mvn /usr/bin/mvn


RUN mkdir /github && mkdir /github/telefonicaid

WORKDIR /github/telefonicaid
RUN git clone https://github.com/telefonicaid/fiware-keypass 

WORKDIR /github/telefonicaid/fiware-keypass
RUN git fetch && git checkout develop && mvn package

#java -jar target/keypass-<VERSION>.jar server conf/config.yml

RUN sed -i "s/port: 8080/port: 7070/g" /opt/keypass/config.yml
RUN sed -i "s/port: 8081/port: 7071/g" /opt/keypass/config.yml
EXPOSE 7070
EXPOSE 7071
