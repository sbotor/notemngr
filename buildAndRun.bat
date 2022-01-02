@echo off
call mvn clean package
call docker build -t pl.polsl.lab.szymonbotor/NoteManager .
call docker rm -f NoteManager
call docker run -d -p 9080:9080 -p 9443:9443 --name NoteManager pl.polsl.lab.szymonbotor/NoteManager