#!/bin/sh
mvn clean package && docker build -t pl.polsl.lab.szymonbotor/NoteManager .
docker rm -f NoteManager || true && docker run -d -p 9080:9080 -p 9443:9443 --name NoteManager pl.polsl.lab.szymonbotor/NoteManager