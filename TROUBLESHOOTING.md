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
time=15:12:54.743 | lvl=INFO | corr= | trans=n/a | op=ServerFactory | msg=Starting AcService
  _  __          _____
 | |/ /         |  __ \
 | ' / ___ _   _| |__) |_ _ ___ ___
 |  < / _ \ | | |  ___/ _` / __/ __|
 | . \  __/ |_| | |  | (_| \__ \__ \
 |_|\_\___|\__, |_|   \__,_|___/___/
            __/ |
           |___/

 v0.3.0-SNAPSHOT

time=15:12:55.106 | lvl=INFO | corr= | trans=n/a | op=DropwizardResourceConfig | msg=The following paths were found for the configured resources:

    DELETE  /pap/v1/{tenant} (es.tid.fiware.iot.ac.pap.TenantEndpoint)
    DELETE  /pap/v1/{tenant}/subject/{subject} (es.tid.fiware.iot.ac.pap.SubjectEndpoint)
    GET     /pap/v1/{tenant}/subject/{subject} (es.tid.fiware.iot.ac.pap.SubjectEndpoint)
    POST    /pap/v1/{tenant}/subject/{subject} (es.tid.fiware.iot.ac.pap.SubjectEndpoint)
    DELETE  /pap/v1/{tenant}/subject/{subject}/policy/{policyId} (es.tid.fiware.iot.ac.pap.PoliciesEndpoint)
    GET     /pap/v1/{tenant}/subject/{subject}/policy/{policyId} (es.tid.fiware.iot.ac.pap.PoliciesEndpoint)
    PUT     /pap/v1/{tenant}/subject/{subject}/policy/{policyId} (es.tid.fiware.iot.ac.pap.PoliciesEndpoint)
    POST    /pdp/v3/{tenant} (es.tid.fiware.iot.ac.pdp.PdpEndpoint)

time=15:12:55.464 | lvl=INFO | corr= | trans=n/a | op=ServerConnector | msg=Started application@7e00a490{HTTP/1.1}{0.0.0.0:7070}
time=15:12:55.464 | lvl=INFO | corr= | trans=n/a | op=ServerConnector | msg=Started admin@5c342a90{HTTP/1.1}{127.0.0.1:7071}
```

## Database connection errors

If the database connection is lost for whatever reason, the following error entry
will appear in the logs.

```
time=15:16:02.360 | lvl=ERROR | corr=5651d7a9-0cc8-475b-8d93-147df97ec62f | trans=n/a | op=SqlExceptionHelper | msg=Communications link failure
```

The application tries to reconnect automatically.

## Error provisioning a policy

In case the client user provision a invalid policy, the server returns a 400
HTTP response code.

But if for whatever reason you want to debug the details about this error, search
the logs for the following message:

```
time=15:15:02.453 | lvl=ERROR | corr=45a55bc3-3456-47ec-bf97-436f7ed0e71a | trans=n/a | op=SubjectEndpoint | msg=Cannot parse policy: java.lang.NullPointerException
```

## Monitoring API

You can inspect the application status through its admin interface, by default
configured to listen in <http://localhost:7071>.

The admin interface expose several metrics and health check status.
