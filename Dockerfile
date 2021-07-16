FROM openjdk:8-alpine

COPY target/uberjar/test-project.jar /test-project/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/test-project/app.jar"]
