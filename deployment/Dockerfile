# Prepare frontend files
FROM node:14 as nodeimage
COPY ./frontend /
WORKDIR /

RUN npm install
RUN yarn assemble

# Prepare jar using frontend and backend files
FROM gradle:jdk15 as gradleimage
COPY . /home/gradle/source
WORKDIR /home/gradle/source
COPY --from=nodeimage /backend/src/main/resources/static /home/gradle/source/backend/src/main/resources/static
COPY --from=nodeimage /backend/src/main/resources/templates /home/gradle/source/backend/src/main/resources/templates

WORKDIR /home/gradle/source/backend

RUN chmod +x ./gradlew
RUN ./gradlew bootJar

# Prepare environment and jar for the container
FROM adoptopenjdk/openjdk15:alpine-jre
COPY --from=gradleimage /home/gradle/source/backend/build/libs /

EXPOSE 80

CMD ["java", "-server", "-XX:+UnlockExperimentalVMOptions", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", \
    "-jar", "/pronombackend-0.0.1-SNAPSHOT.jar"]