FROM adoptopenjdk/openjdk15:alpine-jre

WORKDIR /

COPY ./build/libs/pronombackend-0.0.1-SNAPSHOT.jar /pronombackend.jar

EXPOSE 80

CMD ["java", "-server", "-XX:+UnlockExperimentalVMOptions", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", \
    "-jar", "/pronombackend.jar"]