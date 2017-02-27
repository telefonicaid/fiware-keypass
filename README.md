# FIWARE-KeyPass

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
