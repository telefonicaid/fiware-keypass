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

## Install MySQL (client at least?)
RUN yum -y install mysql

RUN mkdir -p /opt/keypass
COPY . /opt/keypass
WORKDIR /opt/keypass
RUN mvn package

RUN sed -i -e "s/port: 8080/port: 7070/g" conf/config.yml
RUN sed -i -e "s/port: 8081/port: 7071/g" conf/config.yml
RUN sed -i -e "s/localhost/db/g" conf/config.yml
RUN cat conf/config.yml

ADD target/keypass-0.4.3.jar /data/keypass.jar
ADD conf/config.yml /data/config.yml

RUN sed -i -e "s/port: 8080/port: 7070/g" /data/config.yml
RUN sed -i -e "s/port: 8081/port: 7071/g" /data/config.yml
RUN sed -i -e "s/localhost/db/g" /data/config.yml
RUN cat /data/config.yml

RUN java -jar /data/keypass.jar db migrate /data/config.yml
CMD java -jar /data/keypass.jar server /data/config.yml

EXPOSE 7070
EXPOSE 7071
