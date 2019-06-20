# Matrix Client SDK for Java
[![Build Status](https://travis-ci.org/kamax-matrix/matrix-java-sdk.svg?branch=master)](https://travis-ci.org/kamax-matrix/matrix-java-sdk)

---

**This project is no longer maintained.**

---

## Purpose

Matrix SDK in Java 1.8 for:
- Client -> Homeserver
- Client -> Identity Server
- Application Server -> Homeserver

## Use
### Add to your project
#### Gradle
```
repositories {
    maven {
        url 'https://kamax.io/maven/releases/'
    }
}

dependencies {
    compile 'io.kamax:matrix-java-sdk:<USE_LATEST_TAG_WITHOUT_LEADING_V>'
}
```

#### Maven
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
**WARNING:** This SDK was originally created to support [Kamax.io projects](https://github.com/kamax-matrix) and is
therefore not necessarily complete. It will be built as the various projects evolve and grow. The SDK is therefore still
in Alpha.

### Getting started
#### Getting the client object
With .well-known auto-discovery:
```java
_MatrixClient client = new MatrixHttpClient("example.org");
client.discoverSettings();
```

With C2S API Base URL:
```java
URL baseUrl = new URL("https://example.org");
_MatrixClient client = new MatrixHttpClient(baseUrl);
```

#### Providing credentials
Access token:
```java
client.setAccessToken(accessToken);
```

Log in:
```java
client.login(new MatrixPasswordCredentials(username, password));
```

#### Sync
```java
// We will update this after each sync call
String syncToken = null;

// We sync until the process is interrupted via Ctrl+C or a signal
while (!Thread.currentThread().isInterrupted()) {
    
    // We provide the next batch token, or null if we don't have one yet
    _SyncData data = client.sync(SyncOptions.build().setSince(syncToken).get());
    
    // We check the joined rooms
    for (JoinedRoom joinedRoom : data.getRooms().getJoined()) {
        // We get the relevant room object to act on it while we process
        _Room room = client.getRoom(joinedRoom.getId());
        
        for (_MatrixEvent rawEv : joinedRoom.getTimeline()) {
            // We only want to act on room messages
            if ("m.room.message".contentEquals(rawEv.getType())) {
                MatrixJsonRoomMessageEvent msg = new MatrixJsonRoomMessageEvent(rawEv.getJson());
                
                // Ping?
                if (StringUtils.equals("ping", msgg.getBody())) {
                    // Pong!
                    room.sendText("pong");
                }
            }
        }
    }
    
    // We check the invited rooms
    for (InvitedRoom invitedRoom : data.getRooms().getInvited()) {
        // We auto-join rooms we are invited to
        client.getRoom(invitedRoom.getId()).join());
    }
    
    // Done processing sync data. We save the next batch token for the next loop execution
    syncToken = data.nextBatchToken();
}
```


#### As an Application Service
Use `MatrixApplicationServiceClient` instead of `MatrixHttpClient` when creating the main client object.

To talk to the API as a virtual user, use the method `createClient(localpart)` on MatrixApplicationServiceClient, then
processed normally.

### Real-world usage
#### As a regular client
You can check the [Send'n'Leave bot](https://github.com/kamax-matrix/matrix-send-n-leave-bot) which make uses of this SDK in a more realistic fashion.  
Direct link to the relevant code: [here](https://github.com/kamax-matrix/matrix-send-n-leave-bot/blob/master/src/main/java/io/kamax/matrix/bots/send_n_leave/SendNLeaveBot.java#L68)

#### As an Application Service
- mxasd-voip
  - [Project](https://github.com/kamax-matrix/matrix-appservice-voip)
  - [Relevant code](https://github.com/kamax-matrix/matrix-appservice-voip/blob/master/src/main/java/io/kamax/matrix/bridge/voip/matrix/MatrixManager.java)

- mxasd-email - **WARNING:** This project use a very old version of the SDK but is still relevant 
  - [Project](https://github.com/kamax-matrix/matrix-appservice-email)
  - [Relevant code](https://github.com/kamax-matrix/matrix-appservice-email/blob/master/src/main/java/io/kamax/matrix/bridge/email/model/matrix/MatrixApplicationService.java)

## Contribute
Contributions and PRs are welcome to turn this into a fully fledged Matrix Java SDK.  
Your code will be licensed under AGPLv3.

To ensure code formatting consistency, we use [Spotless](https://github.com/diffplug/spotless).  
Before opening any PR, make sure you format the code:
```bash
./gradlew spotlessApply
```

Your code must pass all existing tests with and must provide tests for any new method/class/feature.  
Make sure you run:
```bash
./gradlew test
```
