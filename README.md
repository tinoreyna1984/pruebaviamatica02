# Aplicación

## Backend

### Dependencias empleadas
* Spring Web
* Spring Data JPA
* Spring Security
* Lombok
* Spring Boot Dev Tools
* PostgreSQL Driver
* JSON Schema:
```xml
		<!--JSON Schema-->
		<dependency>
			<groupId>com.networknt</groupId>
			<artifactId>json-schema-validator</artifactId>
			<version>1.0.72</version>
		</dependency>
```
* JWT:
```xml
		<!-- JWT -->
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-api</artifactId>
			<version>0.11.5</version>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-impl</artifactId>
			<version>0.11.5</version>
		</dependency>
		<dependency>
			<groupId>io.jsonwebtoken</groupId>
			<artifactId>jjwt-jackson</artifactId>
			<version>0.11.5</version>
		</dependency>
```
* Jackson:
```xml
		<!--Jackson-->
		<dependency>
			<groupId>com.fasterxml.jackson.core</groupId>
			<artifactId>jackson-core</artifactId>
			<version>2.15.2</version>
		</dependency>
		<dependency>
			<groupId>com.fasterxml.jackson.core</groupId>
			<artifactId>jackson-databind</artifactId>
			<version>2.15.2</version>
		</dependency>
```
* Swagger
```xml
		<!--Swagger-->
		<dependency>
			<groupId>org.springdoc</groupId>
			<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
			<version>2.0.4</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-validation</artifactId>
		</dependency> <!--requiere Validation-->
```
* OpenCSV
```xml
		<dependency>
			<groupId>com.opencsv</groupId>
			<artifactId>opencsv</artifactId>
			<version>5.6</version>
		</dependency>
```

### Ejecución
* Desde el programa principal AcademiaApplication.java (con IntelliJ IDEA u otro IDE).
* También se puede usar el ejecutable de Maven si aplica.
* NOTA: después de levantar el proyecto, ejecutar en PostgreSQL los archivos dentro de la carpeta postgresql del repositorio. Son funciones requeridas por el dashboard.

### Endpoints:
Usar Swagger: http://localhost:4009/swagger-ui/index.html


## Frontend

### Dependencias empleadas
* Angular
* Angular Material
* Bootstrap
* Sweet Alert 2
* JWT Decode

## Para probar
* Login como usuario:
```bash
U: Tinoreyna1984
C: u$uari0CRM
```
* Login como administrador:
```bash
U: Administrat0r
C: Tr20010878
```
Las cuentas provienen de la data de prueba (data.sql).

