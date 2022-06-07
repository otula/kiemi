FROM openjdk:17

WORKDIR /app
COPY ./spring-swagger-codegen-sites-0.0.1-SNAPSHOT.jar /app

COPY certificates /certificates/

EXPOSE 44443

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","spring-swagger-codegen-sites-0.0.1-SNAPSHOT.jar"]
