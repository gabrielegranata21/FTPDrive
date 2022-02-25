FROM openjdk:8
ARG JAR_FILE=target/prod/ftpdrive-1.0.jar
#COPY src/main/resources/ftpdrive.properties /
COPY ${JAR_FILE} ftpdrive.jar
RUN mkdir -p /mnt/pdftemp/Batches/todo && \
    mkdir -p /mnt/pdftemp/calderonepdf/ClassEditore/ITALIAOGGI/ && \
    mkdir -p /mnt/pdftemp/calderonepdf/ClassEditore/ITALIAOGGISETTE/ && \
    mkdir -p /mnt/pdftemp/calderonepdf/ClassEditore/MIFI/ && \
    mkdir -p /mnt/pdftemp/calderonepdf/ClassEditore/MILANOFINANZA/ && \
    ssh-keyscan -H -t rsa 172.16.10.10 >> known_hosts
ENTRYPOINT ["java","-jar","/ftpdrive.jar"]