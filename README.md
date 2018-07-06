# Matrix Client SDK for Java
[![Build Status](https://travis-ci.org/kamax-io/matrix-java-sdk.svg?branch=master)](https://travis-ci.org/kamax-io/matrix-java-sdk)

## Purpose
Matrix SDK in Java 1.8 for:
- Client -> Identity Server
- Application Server -> Homeserver

## Use
### Gradle
```
repositories {
    maven {
        url 'https://kamax.io/maven/releases/'
    }
}

dependencies {
    compile 'io.kamax:matrix-java-sdk:USE_LATEST_TAG_WITHOUT_LEADING_V'
}
```

### Maven
```
<repositories>
  <repository>
    <id>kamax-io</id>
    <name>kamax-io</name>
    <url>https://kamax.io/maven/releases/</url>
  </repository>
</repositories>
<dependencies>
  <dependency>
    <groupId>io.kamax</groupId>
    <artifactId>matrix-java-sdk</artifactId>
    <version>USE_LATEST_TAG_WITHOUT_LEADING_V</version>
  </dependency>
</dependencies>
```
**WARNING:** This SDK was originally created to support [Kamax.io projects](https://github.com/kamax-io) and is therefore not necessarly complete. It will be built as the various projects evolve and grow. The SDK is therefore still in Alpha.

## Tests
### Unit tests
The unit tests of this project are located under `src/test`. In these tests the http calls against the homeserver are mocked with [Wiremock](http://wiremock.org/). The tests can be run by executing Gradle's test task: `./gradlew test`.

### Integration tests
The integration tests are located under `src/testInt` and are run against a homeserver. Therefore a server name
and user credentials have to be provided in the config file with the name `src/testInt/resources/HomeserverTest.conf` to run these tests. A template configuration file exists in the
same directory with the name `HomeserverTest.conf_template`. The configuration file is ignored by Git and will not be checked in when comitting to the repository.

To run the integration tests, please use the task testInt: `./gradlew testInt`.

**WARNING:** At the moment, most of the integration tests fail as the test cases are not yet adjusted to be run against a real homeserver.


## Contribute
Contributions and PRs are welcome to turn this into a fully fledged Matrix Java SDK.  
Your code will be licensed under AGPLv3.

## Contact
Matrix:
- Join [#matrix-java-sdk:kamax.io](https://matrix.to/#/#matrix-java-sdk:kamax.io)
- Read-only [view](https://view.matrix.org/room/!fQxAyfvcUDMivbUqFX:kamax.io/)

Email:
- On our website [Kamax.io](https://www.kamax.io)
