FROM maven:3.8.3-openjdk-17

WORKDIR /${JAR_FILE}
COPY . .
RUN mvn clean install -DskipTests

CMD mvn spring-boot:run