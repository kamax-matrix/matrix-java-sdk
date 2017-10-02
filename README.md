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
    compile 'io.kamax:matrix-java-sdk:USE_LATEST_TAG'
}
```

### Maven
```
<repositories>
    <repository>
        <name>kamax-io</name>
        <url>https://kamax.io/maven/releases/</url>
    </repository>
</repositories>
<dependencies>
    <dependency>
        <groupId>io.kamax</groupId>
        <artifactId>matrix-java-sdk</artifactId>
        <version>USE_LATEST_TAG</version>
    </dependency>
</dependencies>
```
**WARNING:** This SDK was originally created to support [Kamax.io projects](https://github.com/kamax-io) and is therefore not necessarly complete. It will be built as the various projects evolve and grow. The SDK is therefore still in Alpha.

## Contribute
Contributions and PRs are welcome to turn this into a fully fledged Matrix Java SDK.  
Your code will be licensed under AGPLv3

## Contact
Matrix:
- Join [#matrix-java-sdk:kamax.io](https://matrix.to/#/#matrix-java-sdk:kamax.io)
- Read-only [view](https://view.matrix.org/room/!fQxAyfvcUDMivbUqFX:kamax.io/)

Email:
- On our website [Kamax.io](https://www.kamax.io)
