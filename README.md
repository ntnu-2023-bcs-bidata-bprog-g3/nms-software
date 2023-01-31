# nms-software

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

Launch the application by running:
```bash
mvn clean compile verify exec:java
```
