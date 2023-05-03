# nms-software

## Background

The network management system is developed and written as a part of a bachelor's thesis at the University of Science 
and Technology in Gjøvik, Norway.

## Description

NMS, short for network management system, is one of the two components of an offline license management system.

## Requirements

The application requires Java 11 to run, in addition to Maven.

In order to redeem and generate licenses, an intermediate certificate is necessary. The certificate is not included, 
and should be generated using the root certificate of the licensing company. 

## Launch
To launch the application, you need to use an IDE bundled with Maven or install Maven on you local machine.

### Install Maven

For Linux Debian, Ubuntu, etc.:

```bash
sudo apt-get install maven
```

For MacOS:
_Please note, Brew is required._

```bash
brew install maven
```

### Package the application

Package the application by running:

```bash
mvn clean compile package exec:java
```

### Run the application

Then run the created Jar file:

```bash
java -jar target/nms-software-1.0-SNAPSHOT.jar
```
