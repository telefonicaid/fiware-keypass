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
