# Fiware-Iot-AC

AccessControl prototype for IoT Platform.

**ALPHA project**: Many features may not work as expected, the API may change
without previous notice, the persistence is stored in memory (so all policies
will be lost after server restart).

# Build

```
$ mvn package
```

# Running

```
$ java -jar target/fiware-iot-ac-0.0.1-SNAPSHOT.jar server configs/in-memory-database.yml
```

# Usage

## Create a policy

```
curl -i -H "Accept: application/xml" -H "Content-type: application/xml" \
    -X POST -d @src/test/resources/es/tid/fiware/iot/ac/xacml/policy01.xml \
    http://localhost:8080/pap/v1/myOwnTenant/aRoleId
```

Response should be something like this:

```
HTTP/1.1 201 Created
Date: Mon, 15 Sep 2014 20:02:35 GMT
Location: http://localhost:8080/pap/v1/myOwnTenant/aRoleId/1
Content-Type: application/xml
Content-Length: 0
```

## Retrieve a policy

```
curl -i http://localhost:8080/pap/v1/myOwnTenant/aRoleId/1
```

Response will be the previously uploaded policy.

## Retrieve all the policies for a tenant / subject

**Not yet implemented**

## Evaluate XACML request

```
curl -i -H "Accept: application/xml" -H "Content-type: application/xml" \
    -X POST -d @src/test/resources/es/tid/fiware/iot/ac/xacml/policy01_request01.xml \
    http://localhost:8080/pdp/v3/myOwnTenant
```
Response:

```
HTTP/1.1 200 OK
Date: Mon, 15 Sep 2014 20:10:45 GMT
Content-Type: application/xml
Transfer-Encoding: chunked

<Response xmlns="urn:oasis:names:tc:xacml:3.0:core:schema:wd-17"><Result><Decision>Permit</Decision><Status><StatusCode Value="urn:oasis:names:tc:xacml:1.0:status:ok"/></Status></Result></Response>
```

# Future work

* First code complete with all functionality implemented
* Persistence
* API method: policy validation

