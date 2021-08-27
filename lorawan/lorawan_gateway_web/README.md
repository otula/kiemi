# OpenAPI / Spring

Simple Web Service (REST API) for retrieving data captured by the LoraWAN gateway.

## Guide

swagger-codegen-cli is available at https://github.com/swagger-api/swagger-codegen
or directly at Maven: https://mvnrepository.com/artifact/io.swagger.codegen.v3/swagger-codegen-cli

Spring Tool Suite can be found at https://spring.io/tools and the created application can be run as "Spring Boot App" or packaged with "mvn package" from terminal at run with java (e.g. java -jar FILENAME.jar).

## Codegen

To create code from the openapi.yaml, run (as described in the quide): java -jar swagger-codegen-cli-3.0.8.jar generate -i lorawan-web.yaml --api-package tuni.lorawan.sensors.api --model-package tuni.lorawan.sensors.model --group-id tuni.lorawan.sensors --artifact-id spring-swagger-codegen-sensors --artifact-version 0.0.1-SNAPSHOT -l spring -o spring-swagger-codegen-sensors

## Notes / Further Instructions

Check the "openapi" example at the repository for possible fixes for the files generated with swagger codegen.

# Spring / MySQL

Remember to add your database configuration to application.properties:
spring.datasource.url=jdbc:mysql://localhost:3306/db_example?serverTimezone=UTC
spring.datasource.username=user
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=none
You can also optionally change spring.jpa.hibernate.ddl-auto to =create or =update to force re-creating/updating all database tables on service startup

Login credentials to the service can be changed by editing application.properties:
tuni.lorawan.sensors.username=username
tuni.lorawan.sensors.passwowd=password

