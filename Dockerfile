FROM openjdk:11.0.8-jre-slim

ENV APPLICATION_USER ktor

RUN adduser --disabled-password --gecos '' $APPLICATION_USER

RUN mkdir /app
RUN chown -R $APPLICATION_USER /app

USER $APPLICATION_USER

COPY ./build/libs/charisma-api-fat.jar /app/charisma-api-fat.jar
WORKDIR /app

CMD ["sh", "-c", "java -server -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport -XX:InitialRAMFraction=2 -XX:MinRAMFraction=2 -XX:MaxRAMFraction=2 -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -XX:+UseStringDeduplication -jar charisma-api-fat.jar"]
