# DataSites Data Service

Spring Boot App (service) for visualizing sensor data. This service contains the "Data API" back-end.


## Configuration
The configuration file application.properties is located in the spring-swagger-codegen-sites/src/main/resources directory.

To change the default port for the application, modify: server.port=PORTNUMBER

The Data Service does not store any sensor data or metadata. It uses the Sites APIs for retrieving sensor metadata, and retrieves data directly from external services. The uri for the Sites instance and access credentials can be modified by editing the following settings:
* sites.uri=http://example.org:1234
* sites.username = USERNAME
* sites.password = PASSWORD

If you want to use the simple file upload service included in the Data Service (e.g. for storing blueprints), you can set the file storage directory by editing:
* files.directory = /PATH/TO

### Adapter configuration

If you want to retrieve data from an InfluxDB instance, you need to modify the adapter-ruuvi.properties located in spring-swagger-codegen-sites/src/main/resources. The directory also contains adapter-aranet.properties, which also uses InfluxDB. Both can work with any InfluxDB data source as long as the data fields match the ones defined in Sites's configuration files even though the former is designed to be used with RuuviTag sensors (i.e. data collected by RuuviCollector application), and the latter is degined for Aranet sensors (i.e. aranet-agent application).

The correct Sites' configuration file is located in Sites source code directory spring-swagger-codegen-sites/src/main/resources/static/js/ and is called config.js (search the file for "ruuvi"). This is also the file you should edit, if you modify the Data service adapters (e.g. add more fields to objects). The default field names for "ruuvi" type are: mac, temperature, pressure, humidity, and time.

In the adapter-ruuvi.properties file you can find the required configuration settings:
* ruuvi.influxdb.uri=https://example.org:1234
* ruuvi.influxdb.username=USERNAME
* ruuvi.influxdb.password=PASSWORD
* ruuvi.influxdb.database=DATABASENAME
* ruuvi.influxdb.measurement=MEASUREMENTNAME
* ruuvi.influxdb.tag=TAGNAME
* ruuvi.influxdb.retentionpolicy=DEFAULTRETENTIONPOLICY (e.g. autogen)

The InfluxDB adapters have been tested on InfluxDB 1.8.X, other versions may or may not work.

### Database configuration

The application uses Hibernate's automatic schema generation, which can be configured by modifying: spring.jpa.hibernate.ddl-auto=MODE (e.g. MODE = "create" or "update" (without quotes) to create the initial database table schema).

Only external requirement for the service is a MySQL/MariaDB database instance. The database configuration for database address, username and password can be changed by modifying the following settings:
* spring.datasource.url=jdbc:mysql://localhost:3306/sites_service?serverTimezone=UTC
* spring.datasource.username=MYSQLUSERNAME
* spring.datasource.password=MYSQLPASSWORD

The default database name is sites_service (configured in the url above). The database should be pre-created before running the application with the MYSQLUSERNAME and MYSQLPASSWORD credentials given read and write access to the database. Check the docker-scripts directory for examples on how to create databases for both Sites and Data Services (docker-scripts/sites-docker/databases-init.sql).

### SSL configuration

By-default the application is run without SSL. If you want to enable HTTPS directly on the Spring Boot App, you'll need a pkcs12 keystore file. The SSL configuration can be set up by un-commenting and modifying the properties:
* server.ssl.key-store: /PATH/TO/keystore.p12
* server.ssl.key-store-password: KEYSTOREPASSWORD
* server.ssl.key-store-type: PKCS12
* server.ssl.key-alias: SSLKEYALIASTOUSE
* server.ssl.key-password: SSLKEYPASSWORD

## Compilation

Minimum requirements to compile the application are Java SDK 17+ and Maven.

To compile the application, run "mvn package" in the root directory (where the pom.xml file is located). This will produce various class and status files in the "target" directory, and more importantly, the spring-swagger-codegen-data-0.0.1-SNAPSHOT.jar file, which can be used to run the application.

## Running the application

To run the service application a Java JRE 17+ is required, and the application can be run using the java-command, e.g. java -jar /PATH/TO/spring-swagger-codegen-data-0.0.1-SNAPSHOT.jar

Alternatively, you can check out the docker-scripts directory, which contains instructions on how to run both Sites and Data in a Docker container (docker-scripts/sites-docker).

### Initial user

By-default the application is initialized with an empty database. The initial user must be inserted manually in to the "user" and "user_authorities" tables _after_ the application has been started at least once (as the startup will generate the required tables).

The docker-scripts directory (docker-scripts/sites-docker) has an example scripts for an initial user. You can also check out the "Startup" section of the directory's readme - the same instructions apply here with the exception of using the provided create-user.sh (you need to use the .sql file, the .sh won't work without a running Docker container).

### API Documentation

The API documentation is automatically generated by Swagger and is available in the root of the service (e.g. http://example.org:1234).

### Users

In this Proof-of-Concept version the user management is not implemented. HTTP basic based authentication is available in both Sites and Data (and thus, the use of HTTPS is recommended). Both services have their own user and user_authorities tables for user credential, but in for easy of use, it is recommended to either use the same credentials for both services or better yet, to implement a better user management!

## Web User Interfaces

The Data service does not have a web user interface, but a simple file uploader html is included for storing files (e.g. floor blueprints for sites). This can be reached from: /uploader/index.html, e.g. http://example.org:1234/uploader/index.html. Note that this page is only for debug purposes, and does not contain a login page. You need to access another url first to trigger the browser's basic auth login prompt. The url does not need to be a valid one, e.g. http://example.org:1234/data will do just fine even though it returns 404.

## Other Important Things to Note!

The OpenAPI 3.0 Yaml specification can be found in the api directory.

The Data Service contains three adapters that are not included in the public repository. These are for accessing Pori Energia's, Fourdeg's and Ouman's APIs. The adapter classes are in the source code directory without implementations.
