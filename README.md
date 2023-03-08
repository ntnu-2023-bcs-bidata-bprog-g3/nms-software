# nms-software

[![Java CI with Maven](https://github.com/ntnu-2023-bcs-bidata-bprog-g2/nms-software/actions/workflows/maven.yml/badge.svg)](https://github.com/ntnu-2023-bcs-bidata-bprog-g2/nms-software/actions/workflows/maven.yml)

### Launch
To launch the application, you need to use an IDE bundled with Maven or install Maven on you local machine.

For Linux Debian, Ubuntu, etc.:
```bash
sudo apt-get install maven
```

For MacOS:
_Please note, Brew is required._
```bash
brew install maven
```

Package the application by running:
```bash
mvn clean compile package exec:java
```

Then run the created Jar file:
```bash
java -jar target/nms-software-1.0-SNAPSHOT.jar
```
