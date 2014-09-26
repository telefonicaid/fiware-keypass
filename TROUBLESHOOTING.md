# Troubleshooting

## Log location

Depending on how the application was launched the logs location may vary.

If you are using the `rpm` distribution the logs are located in `/var/log/keypass`.

Please note that the previous directory is a symlink to `/opt/keypass/log`.

## Log rotation

Logs are configured by default to rotate every day, and maintains the last 5 days.

## Version, launch date and listen ports

You can easily discover what version was launched, at what date it was launched,
and on what ports are listening. Just search for the keyword `AcService` on logs
files and you will see that information:

```
INO  [2014-09-24 16:54:15,172] io.dropwizard.server.ServerFactory: Starting AcService
  _  __          _____
 | |/ /         |  __ \
 | ' / ___ _   _| |__) |_ _ ___ ___
 |  < / _ \ | | |  ___/ _` / __/ __|
 | . \  __/ |_| | |  | (_| \__ \__ \
 |_|\_\___|\__, |_|   \__,_|___/___/
            __/ |
           |___/

 v0.2.0-SNAPSHOT

INFO  [2014-09-24 16:54:15,528] io.dropwizard.jersey.DropwizardResourceConfig: The following paths were found for the configured resources:

    DELETE  /pap/v1/{tenant} (es.tid.fiware.iot.ac.pap.TenantEndpoint)
    DELETE  /pap/v1/{tenant}/subject/{subject} (es.tid.fiware.iot.ac.pap.SubjectEndpoint)
    GET     /pap/v1/{tenant}/subject/{subject} (es.tid.fiware.iot.ac.pap.SubjectEndpoint)
    POST    /pap/v1/{tenant}/subject/{subject} (es.tid.fiware.iot.ac.pap.SubjectEndpoint)
    DELETE  /pap/v1/{tenant}/subject/{subject}/policy/{policyId} (es.tid.fiware.iot.ac.pap.PoliciesEndpoint)
    GET     /pap/v1/{tenant}/subject/{subject}/policy/{policyId} (es.tid.fiware.iot.ac.pap.PoliciesEndpoint)
    PUT     /pap/v1/{tenant}/subject/{subject}/policy/{policyId} (es.tid.fiware.iot.ac.pap.PoliciesEndpoint)
    POST    /pdp/v3/{tenant} (es.tid.fiware.iot.ac.pdp.PdpEndpoint)

INFO  [2014-09-24 16:54:15,965] org.eclipse.jetty.server.ServerConnector: Started application@246fb611{HTTP/1.1}{0.0.0.0:8080}
INFO  [2014-09-24 16:54:15,968] org.eclipse.jetty.server.ServerConnector: Started admin@221a28c7{HTTP/1.1}{127.0.0.1:8081}
```

## Database connection errors

If the database connection is lost for whatever reason, the following error entry
will appear in the logs.

```
ERROR [2014-09-24 13:21:02,099] org.hibernate.engine.jdbc.spi.SqlExceptionHelpr: Communications link failure
```

The application tries to reconnect automatically.

## Error provisioning a policy

In case the client user provision a invalid policy, the server returns a 400
HTTP response code.

But if for whatever reason you want to debug the details about this error, search
the logs for the following message:

```
ERROR [2014-09-24 13:20:11,820] es.tid.fiware.iot.ac.pap.SubjectEndpoint: Cannot parse policy:
```

## Monitoring API

You can inspect the application status through its admin interface, by default
configured to listen in <http://localhost:8081>.

The admin interface expose several metrics and health check status.
