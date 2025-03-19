# How to use this Dockerfile

You can build a docker image based on this Dockerfile. This image will contain only an Keypass instance, exposing port `7070`. This requires that you have [docker](https://docs.docker.com/installation/) installed on your machine.

If you just want to have an Keypass running as fast as possible jump to section [The Fastest Way](#the_fastest_way).

If you want to know what and how we are doing things with the container step by step you can go ahead and read the [Build](#build_the_image) and [Run](#run_the_container) sections.

## The Fastest Way

A Docker Compose file is provided for convenience. You must install [Docker Compose](https://docs.docker.com/compose/install/) for this method to work.

Simply navigate to the docker directory of the orchestrator code (if you have downloaded it) and run

        sudo docker-compose up

If you haven't or you don't want to download the whole thing, you can download the file called `docker-compose.yaml` in a directory of your choice and run the aforementioned command. It will work just the same.

You can use [this](https://github.com/telefonicaid/fiware-keypass/blob/master/docker-compose.yml) or also you can create a docker-compose.yml file, were you should include a section like this:

```
keypass:
  image: telefonicaiot/fiware-keypass
  links:
    - mysql
  expose:
    - "7070"
    - "7071"
  ports:
    - "7070:7070"
    - "7071:7071"
  command: -dbhost mysql
```

As you can see there are several arguments to pass to keypass entry point in order to configure some relevant endpoints for keypass as mysql. Make sure all of them are present and in that order:
```
   -dbhost mysql
```

You can use other mysql port setting optionally the port as:
```
   -dbhost mysql[:port]
```

By default wait until mysql database is operative at maximum of default DBTIMEOUT 60 seconds. You can explicitly set this value to other:
```
  environment:
    - KEYPASS_DB_TIMEOUT=100
```



Additionally, the following environment variables are available for keypass docker

| Environment variable  | Configuration attribute | Default value  |
|:----------------------|:------------------------|:---------------|
| KEYPASS_DB_HOST_VALUE | database.url            | localhost:3306 |
| KEYPASS_DB_HOST_NAME  | database.url            | localhost      |
| KEYPASS_DB_HOST_PORT  | database.url            | 3306           |
| KEYPASS_DB_TYPE       | database connection type| mysql (or psql)|
| KEYPASS_DB_TIMEOUT    |                         | 60             |
| KEYPASS_DB_USER       |                         | keypass        |
| KEYPASS_DB_PASSWORD   |                         | keypass        |
| KEYPASS_DB_NAME       |                         | keypass        |
| KEYPASS_LOG_LEVEL     |                         | INFO           |



## Build the image

This is an alternative approach than the one presented in section [The Fastest Way](#the_fastest_way). You do not need to go through these steps if you have used docker-compose.

You only need to do this once in your system:

        sudo docker build -t fiware-keypass .

The parameter `-t fiware-keypass` gives the image a name. This name could be anything, or even include an organization like `-t org/fiware-keypass`. This name is later used to run the container based on the image.

If you want to know more about images and the building process you can find it in [Docker's documentation](https://docs.docker.com/userguide/dockerimages/).
    
## Run the container

The following line will run the container exposing port `7070`, give it a name -in this case `keypass1` and present a bash prompt.

          sudo docker run -d --name keypass1 -p 7070:7070 fiware-keypass

As a result of this command, there is a orchestrator listening on port 7070 on localhost. Try to see if it works now with

        curl localhost:7070/version

A few points to consider:

* The name `keypass1` can be anything and doesn't have to be related to the name given to the docker image in the previous section.
* In `-p 7070:7070` the first value represents the port to listen in on localhost. If you wanted to run a second keypass on your machine you should change this value to something else, for example `-p 7170:7070`.
* Anything after the name of the container image (in this case `keypass`) is interpreted as a parameter for the Keypass.

