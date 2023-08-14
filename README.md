# Spring 6 REST MCV Project

[![CircleCI](https://circleci.com/gh/Carla-de-Beer/spring-6-rest-mvc.svg?style=svg)](https://circleci.com/gh/Carla-de-Beer/pring-6-rest-mvc)

A Spring 6 demo project accessing a MySQL database to read and update beer-related data. An additional H2 in-memory
database is also
connected and is used to run the integration tests (under the "default" profile).

Steps required to run the application with MySQL:

* Spin up a MySQL Docker container with the following command:

    ```sh
    docker run -p 3306:3306 --name mysql-rest-mvc -e MYSQL_ROOT_PASSWORD=<password> -d mysql:8.0
    ```
* With a suitable database workbench, manually create the required database schema and operational user with the script
  provided in `src/scripts/mysql-init.sql`.
* Use the `localmysql` profile when excuting the main class in order to have the database managed by Flyway, and
  validated by Hiberate. Additional beer data entries are added to the database by means of a CSV upload.

The project is based on the following Spring Framework Guru Udemy tutorial:
https://www.udemy.com/course/spring-framework-6-beginner-to-guru/learn/lecture/33399792?start=15#overview

## Requirements

* Java 17
* Spring Boot 3.1.2
* JUnit 5
* MySQL 8.0
