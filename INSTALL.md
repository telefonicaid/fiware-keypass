# Installation guide

## Install from rpm

Just install as usual:

```
rpm -iVh keypass.rpm
```

Once installed, configure your environment settings in `/etc/keypass.yml`, like
database connection. Please note that currently the application is shipped only
with Mysql drivers.

If needed, create the database and user with the following SQL commands:

```sql
CREATE DATABASE keypass;
CREATE USER keypass;
GRANT ALL ON keypass.* TO keypass@localhost IDENTIFIED BY 'keypass';
```

Populate database

```
$ sudo service keypass upgradedb
```

Start the server

```
$ sudo service keypass start
```

## Running manually

The application can be started from the command line in any OS (with Java
installed). It's pakaged as a single jar with all libraries embedded.

First, adjust the configuration file to fit your setup (mainly database
connectivity configuration).

If needed, create the database and user with the following SQL commands:

```sql
CREATE DATABASE keypass;
CREATE USER keypass;
GRANT ALL ON keypass.* TO keypass@localhost IDENTIFIED BY 'keypass';
```

Populate database

```
java -jar keypass-0.2.0.jar db migrate config.yml
```

Start the server

```
java -jar keypass-0.2.0.jar server
```

## Using a different database

Keypass already ships with Mysql drivers. You can use any other database
configuring it and adding the drivers.

To configure, change the database driver, JDBC URL and dialect.

```yaml
driverClass: com.mysql.jdbc.Driver

url: jdbc:mysql://localhost/keypass

properties:
  hibernate.dialect: org.hibernate.dialect.MySQLDialect
```

The driver class name and URL are provided by the driver vendor.

The dialects are defined by Hibernate: http://docs.jboss.org/hibernate/orm/4.3/javadocs/org/hibernate/dialect/Dialect.html

Perform the usual operations (database setup, server launch) adding the driver
to the classpath.

```
java -cp <driver.jar> -jar keypass-0.2.0.jar db migrate config.yml
java -cp <driver.jar> -jar keypass-0.2.0.jar server
```
