# Authenticket Backend (Spring Boot)
# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.1.2/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.1.2/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.1.2/reference/htmlsingle/index.html#web)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/3.1.2/reference/htmlsingle/index.html#data.sql.jpa-and-spring-data)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/3.1.2/reference/htmlsingle/index.html#using.devtools)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

### Adding the secret.properties file
This file contains the authentication keys needed. Download/copy it from wherever stored and make sure it is up to date. Move it to “main/resources” folder alongside application.properties.

### Reimporting dependencies
	1. Open your project in IntelliJ IDEA.
	2. Locate the `pom.xml` file in the root directory of your project.
	3. Right-click on the `pom.xml` file.
	4. From the context menu, hover over the option “Maven”
	5. Click on “Reload Object”

<strong>Take note: you will need to reload pom.xml file every time you make changes to your pom.xml to ensure that your project reflects the changes.</strong>

### API Documention
<strong>Swagger API URL: <http://localhost:8080/swagger-ui/index.html#/></strong>
