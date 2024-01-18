FROM eclipse-temurin:17-jdk
VOLUME /tmp
COPY target/pvoptimizer-0.2.0.jar pvoptimizer.jar
COPY src/main/resources/application-compose.yml application-compose.yml
COPY src/main/resources/logback.xml logback.xml
#requires liblpsolve55.so and liblpsolve55j.so
COPY lib/* lib/
ENTRYPOINT java -Dspring.profiles.active=compose -Dlogback.configurationFile=logback.xml -jar pvoptimizer.jar