# Spring 6 REST MVC Project

![example event parameter](https://github.com/Carla-de-Beer/spring-6-rest-mvc/actions/workflows/build.yml/badge.svg?event=push)

A Spring 6 demo project accessing a MySQL database to read and update beer-related data. An additional H2 in-memory
database is also connected and is used to run the integration tests (under the "default" profile).

The Java section of the project is partly based on the following Spring Framework Guru Udemy tutorial:
https://www.udemy.com/course/spring-framework-6-beginner-to-guru/learn/lecture/33399792?start=15#overview.

## Local development

Steps required to run the application with MySQL:

* Spin up a containerised MySQL server with the provide docker-compose file to create the MySql database container,
  initialised with the required schema and operational user:

    ```sh
    cd docker/docker-compose; docker compose up
    ```

* Start the Spring Boot application with the `localmysql` profile when excuting the main class in order to have the
  MySQL
  database managed by Flyway, and
  validated by Hiberate. Additional beer data entries are added to the database by means of a CSV upload.

### Requirements for local execution

* Java 17
* Spring Boot 3.1.2
* JUnit 5
* MySQL 8.0

## Dockerised application

The application is also available as a Docker image generated via the automated build pipeline.
To run the application with the H2 in-memory database use `default` for the `PROFILE` variable, or `localmysql` to use a
MySQL database:

```
docker container run -e PROFILE=<PROFILE> cadebe/spring-6-rest-mvc
```

For ease of use, the containerised application, together with a MySQL database, can be
executed with the following docker-compose command:

```sh
cd docker/docker-compose; docker compose -f docker-compose.application.yml up
```

## Example Requests

Get all beer entries

```sh
curl -X GET localhost:8080/api/v1/beer | jq
```

Get a beer by name

```sh
curl -X GET "localhost:8080/api/v1/beer?beerName=Porter" | jq
```

Get a beer by style

```sh
curl -X GET "localhost:8080/api/v1/beer?beerStyle=SAISON" | jq
```

Create a new beer entry:

```sh
curl -i -X POST -H 'Content-Type: application/json' -d '{"beerName":"Alaskan Icy Bay","beerStyle":"IPA","upc":"100","price":12.00,"quantityOnHand":10}' localhost:8080/api/v1/beer 
```
