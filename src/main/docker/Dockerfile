FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD LogExecutorService-1.0-SNAPSHOT.jar app.jar
ADD Simulation Simulation
RUN sh -c 'touch /app.jar'
ENTRYPOINT java -Djava.security.egd=file:/dev/./urandom -jar /app.jar
