FROM openjdk:17

COPY target/transactions.jar transactions.jar

ENTRYPOINT["java","-jar", "/transactions.jar"]

