# Network Management System (NMS)

## Background

The network management system is developed and written as a part of a bachelor's thesis at the University of Science 
and Technology in Gjøvik, Norway.

## Description

NMS, short for network management system, is one of the several components of an offline license management system.

## Requirements

The application requires Java 11 to run, in addition to Maven.

In order to redeem licenses, a root public key is necessary, and to generate and distribute licenses, an intermediate private key and certificate is necessary. The root public key is included in the source code, inside `no/ntnu/nms/CustomerConstants.java`, the intermediate private key and the intermediate certificate is included in the keystore.

A guide on how to generate these keys and certificates can be found [HERE](https://github.com/ntnu-2023-bcs-bidata-bprog-g3/license-file-signing/blob/main/initializationGuide.md).

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
