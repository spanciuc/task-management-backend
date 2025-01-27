# Technologies

* Java 21
* Spring boot 3.4.1
* Maven
* PostgreSQL
* H2

# Application start

* Run command for dev profile:

``./mvnw spring-boot:run "-Dspring-boot.run.profiles=dev"``

* Run command for production profile:

``./mvnw spring-boot:run "-Dspring-boot.run.profiles=prod"``

# Swagger UI

``{HOST}:{PORT}/api/swagger-ui``

# Postman testing

* Import postman collection using url: 

``{HOST}:{PORT}/api/v3/api-docs``

* Authentication type: Bearer Token


