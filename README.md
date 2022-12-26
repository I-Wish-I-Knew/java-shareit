# java-shareit
Template repository for Shareit project.

[Repository](https://github.com/I-Wish-I-Knew/java-shareit)

ShareIt is a service for sharing items for daily use.

## Project structure

- **gateway** processes and validates income requests and redirects them to the main server

- **server** - main server handles requests from gateway

## Features

- Add and delete items;
- Item search by name or description;
- Leave a request for an item that not present in the list;
- Book items;
- Add comments to used items;
- Get all bookings by state(ALL, CURRENT, FUTURE, PAST, WAITING, REJECTED)

## Technologies used

- Java 11
- Spring Boot
- Hibernate, JPA
- Lombok
- PostgreSQL, H2
- Junit, Mockito
- Docker, docker-compose
- Maven (multi-module project)
- Postman

## Requirements
The application can be run locally or in a docker container, the requirements for each setup are listed below.

## Local
- [Java 11 SDK](https://www.oracle.com/de/java/technologies/javase/jdk11-archive-downloads.html)
- [Maven](https://maven.apache.org/download.cgi)

## Docker
- [Docker](https://www.docker.com/products/docker-desktop/)

## Run Local
````
$ mvn spring-boot:run -pl gateway
````
gateway will run by default on port 8080
````
$ mvn spring-boot:run -pl server
````
server will run by default on port 9090

Configure the port by changing server.port in application.properties

## Run Docker
First build the image:
````
$ docker-compose build
````
When ready, run it:
````
$ docker-compose up
````
Application will run by default on port 8080

Configure the port by changing gateway.port in docker-compose.yml.
