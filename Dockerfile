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

# Install MySQL
RUN yum -y install mysql mysql-server
RUN service mysqld start

RUN mkdir /github && mkdir /github/telefonicaid

WORKDIR /github/telefonicaid
RUN git clone https://github.com/telefonicaid/fiware-keypass 

WORKDIR /github/telefonicaid/fiware-keypass
RUN git fetch && git checkout develop && mvn package

RUN sed -i "s/port: 8080/port: 7070/g" conf/config.yml
RUN sed -i "s/port: 8081/port: 7071/g" conf/config.yml

ADD target/keypass-0.4.3.jar /data/keypass-0.4.3.jar
ADD conf/config.yml /data/config.yml

RUN echo 'CREATE DATABASE keypass;' | /usr/bin/mysql -u root -u localhost

RUN echo "GRANT ALL PRIVILEGES ON keypass.* TO '$MYSQL_USER'@'localhost' IDENTIFIED BY '$MYSQL_PASSWORD';" | /usr/bin/mysql -u root -u localhost

RUN echo "CREATE DATABASE keypass; GRANT ALL ON keypass.* TO keypass@localhost IDENTIFIED BY 'keypass';" | /usr/bin/mysql -u root -u localhost

RUN java -jar target/keypass-0.4.3.jar db migrate conf/config.yml
CMD java -jar target/keypass-0.4.3.jar server conf/config.yml

EXPOSE 7070
EXPOSE 7071
