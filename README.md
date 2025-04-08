# FIWARE-KeyPass

[![FIWARE Security](https://nexus.lab.fiware.org/static/badges/chapters/security.svg)](https://www.fiware.org/developers/catalogue/)
[![License: Apache 2.0](https://img.shields.io/github/license/telefonicaid/fiware-keypass.svg)](https://opensource.org/licenses/Apache-2.0)
<br/>
[![Quay badge](https://img.shields.io/badge/quay.io-fiware%2Fkeyspass-grey?logo=red%20hat&labelColor=EE0000)](https://quay.io/repository/fiware/keyspass)
[![Docker badge](https://img.shields.io/badge/docker-telefonicaiot%2Ffiware--keypass-blue?logo=docker)](https://hub.docker.com/r/telefonicaiot/fiware-keypass/)
<br/>
![Status](https://nexus.lab.fiware.org/static/badges/statuses/incubating.svg)

Keypass is multi-tenant XACML server with PAP (Policy Administration Point) and
PDP (Policy Decision Point) capabilities.

KeyPass is based mainly on:

* [Balana](https://github.com/wso2/commons/tree/master/balana),
  a complete implementation of both XACML v2 and v3 specs
* [Dropwizard](http://dropwizard.io), a framework for developing
  high-performance, RESTful web services.

In this README document you will find how to get started with the application and
basic concepts. For a more detailed information you can read the following docs:

* [API](API.md)
* [Installation guide](INSTALL.md)
* [Troubleshooting](TROUBLESHOOTING.md)
* [Behaviour Tests](https://github.com/telefonicaid/fiware-keypass/tree/master/src/behavior/README.md)
* [Performance Tests](https://github.com/telefonicaid/fiware-keypass/tree/master/jmeter-test-plan.jmx)
* [Docker configuraton](DOCKER.md)


# Building

Building requires Java 6+ and Maven 3.

```
$ mvn package
```

Building RPM (needs native `rpmbuild` installed in your box, tested on MacOSX
and Redhat Linux. May work on other platforms as well):

```
$ mvn -Prpm package
```

Building ZIP file

```
$ mvn -Pzip package
```

# Running

```
$ java -jar target/keypass-<VERSION>.jar server conf/config.yml
```

# Migrate from MySQL to PostgreSQL

Keypass versions 1.14.0 and later can be migrated from MySQL to PostgreSQL.

## Prerequisites

Default auth plugin in MySQL 8 is `caching_sha2_password` which is not supported by pgloader tool needed by this procedure. During this procedure MySQL should use `mysql_native_password` plugin. To achieve that set in `[mysqld]` section add:

    default-authentication-plugin=mysql_native_password

Then restart your MySQL server and execute:

    ALTER USER 'youruser'@'localhost' IDENTIFIED WITH mysql_native_password BY 'yourpassword';

## Procedure

1. Create new Keypass database and user in PostgreSQL:
```sh
PGPASSWORD=postgresUser psql -h 172.17.0.1 -p 5432 -U postgresPass <<EOF
CREATE DATABASE keypassDb;
CREATE USER keypassUser WITH PASSWORD 'keypassPass';
GRANT ALL PRIVILEGES ON DATABASE keypassDb TO keypassUser;
ALTER DATABASE keypassDb OWNER TO keypassUser;
EOF
```

2. Migrate with [pgloader](https://pgloader.io/) which is commonly available in linux distributions like Debian.
```sh
pgloader mysql://keypassUser:keypassPass@172.17.0.1:3306/keypassDb postgresql://keypassUser:keypassPass@172.17.0.1:5432/keypassDb
```

3. Rename policy table to Policy (in camelcase)
```sh
PGPASSWORD=postgresUser psql -h 172.17.0.1 -p 5432 -U postgresPass -d keypassDb <<EOF
ALTER TABLE policy RENAME TO "Policy";
EOF
```

4. Restart Keypass Docker container
```sh
docker restart keypass
```


# Usage

## Create a policy

```
curl -i -H "Accept: application/xml" -H "Content-type: application/xml" \
    -H "Fiware-Service: myTenant" \
    -X POST -d @src/test/resources/es/tid/fiware/iot/ac/xacml/policy01.xml \
    http://localhost:8080/pap/v1/subject/role12345
```

Response should be something like this:

```
HTTP/1.1 201 Created
Date: Mon, 15 Sep 2014 20:02:35 GMT
Location: http://localhost:8080/pap/v1/subject/role12345/policy/policy01
Content-Type: application/xml
Content-Length: 0
```

## Retrieve a policy

```
curl -i -H "Fiware-Service: myTenant" \
    http://localhost:8080/pap/v1/subject/role12345/policy/policy01
```

Response will be the previously uploaded policy.

## Evaluate XACML request

```
curl -i -H "Accept: application/xml" -H "Content-type: application/xml" \
    -H "Fiware-Service: myTenant" \
    -X POST -d @src/test/resources/es/tid/fiware/iot/ac/xacml/policy01_request01.xml \
    http://localhost:8080/pdp/v3
```
Response:

```
HTTP/1.1 200 OK
Date: Mon, 15 Sep 2014 20:10:45 GMT
Content-Type: application/xml
Transfer-Encoding: chunked

<Response xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17"><Result><Decision>Permit</Decision><Status><StatusCode Value="urn:oasis:names:tc:xacml:1.0:status:ok"/></Status></Result></Response>
```
