FROM eclipse-temurin:17-jre-alpine
ARG JAR_FILE=mcs_transaction-service/target/*.jar
COPY ${JAR_FILE} transaction-service.jar
ENTRYPOINT ["java","-jar","/transaction-service.jar"]