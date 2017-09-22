/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Maxime Dor
 *
 * https://max.kamax.io/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.matrix.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.kamax.matrix.MatrixErrorInfo;
import io.kamax.matrix.MatrixID;
import io.kamax.matrix._MatrixID;
import io.kamax.matrix.hs._MatrixRoom;
import io.kamax.matrix.json.RoomMessageFormattedTextPutBody;
import io.kamax.matrix.json.RoomMessageTextPutBody;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MatrixHttpRoom extends AMatrixHttpClient implements _MatrixRoom {

    private Logger log = LoggerFactory.getLogger(MatrixHttpRoom.class);

    private String roomId;

    public MatrixHttpRoom(MatrixClientContext context, String roomId) {
        super(context);

        this.roomId = roomId;
    }

    protected URIBuilder getClientPathBuilder(String action) {
        URIBuilder builder = super.getClientPathBuilder(action);
        builder.setPath(builder.getPath().replace("{roomId}", roomId));

        return builder;
    }

    @Override
    public String getAddress() {
        return roomId;
    }

    @Override
    public Optional<String> getName() {
        try {
            URI path = getClientPath("/rooms/{roomId}/state/m.room.name");

            try (CloseableHttpResponse res = client.execute(log(new HttpGet(path)))) {
                Charset charset = ContentType.getOrDefault(res.getEntity()).getCharset();
                String body = IOUtils.toString(res.getEntity().getContent(), charset);

                if (res.getStatusLine().getStatusCode() != 200) {
                    if (res.getStatusLine().getStatusCode() == 404) {
                        // No name has been set
                        return Optional.empty();
                    }

                    // TODO handle rate limited
                    if (res.getStatusLine().getStatusCode() == 429) {
                        log.warn("Request was rate limited", new Exception());
                    }
                    MatrixErrorInfo info = gson.fromJson(body, MatrixErrorInfo.class);
                    throw new IOException("Couldn't get name for room " + roomId + " - "
                            + info.getErrcode() + ": " + info.getError());
                }

                return Optional
                        .of(jsonParser.parse(body).getAsJsonObject().get("name").getAsString());
            }
        } catch (IOException e) {
            throw new MatrixClientRequestException(e);
        }
    }

    @Override
    public Optional<String> getTopic() {
        try {
            URI path = getClientPath("/rooms/{roomId}/state/m.room.topic");

            try (CloseableHttpResponse res = client.execute(log(new HttpGet(path)))) {
                Charset charset = ContentType.getOrDefault(res.getEntity()).getCharset();
                String body = IOUtils.toString(res.getEntity().getContent(), charset);

                if (res.getStatusLine().getStatusCode() != 200) {
                    if (res.getStatusLine().getStatusCode() == 404) {
                        // No topic has been set
                        return Optional.empty();
                    }

                    // TODO handle rate limited
                    if (res.getStatusLine().getStatusCode() == 429) {
                        log.warn("Request was rate limited", new Exception());
                    }
                    MatrixErrorInfo info = gson.fromJson(body, MatrixErrorInfo.class);
                    throw new MatrixClientRequestException(info,
                            "Couldn't get topic for room " + roomId);
                }

                return Optional
                        .of(jsonParser.parse(body).getAsJsonObject().get("topic").getAsString());
            }
        } catch (IOException e) {
            throw new MatrixClientRequestException(e);
        }
    }

    @Override
    public void join() {
        try {
            URI path = getClientPath("/rooms/{roomId}/join");

            try (CloseableHttpResponse res = client.execute(log(new HttpPost(path)))) {
                if (res.getStatusLine().getStatusCode() == 200) {
                    log.info("Successfully joined room {} as {}", roomId, getUser());
                    return;
                }

                if (res.getStatusLine().getStatusCode() == 429) {
                    // TODO handle rate limited
                    log.warn("Request was rate limited", new Exception());
                }

                Charset charset = ContentType.getOrDefault(res.getEntity()).getCharset();
                String body = IOUtils.toString(res.getEntity().getContent(), charset);
                MatrixErrorInfo info = gson.fromJson(body, MatrixErrorInfo.class);

                if (res.getStatusLine().getStatusCode() == 403) {
                    log.error("Failed to join room, we are not allowed: {} - {}", info.getErrcode(),
                            info.getError());
                } else {
                    throw new MatrixClientRequestException(info, "Error joining for " + getUser());
                }
            }
        } catch (IOException e) {
            throw new MatrixClientRequestException(e);
        }
    }

    @Override
    public void leave() {
        try {
            URI path = getClientPath("/rooms/{roomId}/leave");

            try (CloseableHttpResponse res = client.execute(log(new HttpPost(path)))) {
                if (res.getStatusLine().getStatusCode() == 200) {
                    log.info("Successfully left room {} as {}", roomId, getUser());
                } else {
                    if (res.getStatusLine().getStatusCode() == 429) {
                        // TODO handle rate limited
                        log.warn("Request was rate limited", new Exception());
                    }

                    // TODO Find a better way to handle room objects for unknown rooms
                    // Maybe throw exception?
                    // TODO implement method to check room existence - isValid() ?
                    if (res.getStatusLine().getStatusCode() == 404) {
                        log.warn("Room {} is not joined, ignoring call", roomId);
                        return;
                    }

                    Charset charset = ContentType.getOrDefault(res.getEntity()).getCharset();
                    String body = IOUtils.toString(res.getEntity().getContent(), charset);
                    MatrixErrorInfo info = gson.fromJson(body, MatrixErrorInfo.class);

                    if (res.getStatusLine().getStatusCode() == 403) {
                        log.debug(
                                "Failed to leave room, we are not allowed, most likely already left: {} - {}",
                                info.getErrcode(), info.getError());
                    } else {
                        throw new MatrixClientRequestException(info,
                                "Error when leaving room " + roomId + " as " + getUser());
                    }
                }
            }
        } catch (IOException e) {
            throw new MatrixClientRequestException(e);
        }
    }

    private void sendMessage(RoomMessageTextPutBody content) {
        try {
            URI path = getClientPath(
                    "/rooms/{roomId}/send/m.room.message/" + System.currentTimeMillis());
            HttpPut req = new HttpPut(path);
            req.setEntity(getJsonEntity(content));

            try (CloseableHttpResponse res = client.execute(log(req))) {
                if (res.getStatusLine().getStatusCode() == 200) {
                    log.info("Successfully sent message in room {} as {}", roomId, getUser());
                } else {
                    if (res.getStatusLine().getStatusCode() == 429) {
                        // TODO handle rate limited
                        log.warn("Request was rate limited", new Exception());
                    }

                    Charset charset = ContentType.getOrDefault(res.getEntity()).getCharset();
                    String body = IOUtils.toString(res.getEntity().getContent(), charset);
                    MatrixErrorInfo info = gson.fromJson(body, MatrixErrorInfo.class);

                    if (res.getStatusLine().getStatusCode() == 403) {
                        log.error("Failed send message, we are not allowed: {}", info.getError());
                    } else {
                        throw new IOException("Error sending message for " + getUser() + " - "
                                + info.getErrcode() + ": " + info.getError());
                    }
                }
            }
        } catch (IOException e) {
            throw new MatrixClientRequestException(e);
        }
    }

    @Override
    public void sendText(String message) {
        sendMessage(new RoomMessageTextPutBody(message));
    }

    @Override
    public void sendFormattedText(String formatted, String rawFallback) {
        // TODO sanitize input
        sendMessage(new RoomMessageFormattedTextPutBody(rawFallback, formatted));
    }

    @Override
    public void sendNotice(String message) {
        sendMessage(new RoomMessageTextPutBody("m.notice", message));
    }

    @Override
    public void sendNotice(String formatted, String plain) {
        // TODO sanitize input
        sendMessage(new RoomMessageFormattedTextPutBody("m.notice", plain, formatted));
    }

    @Override
    public void invite(_MatrixID mxId) {
        // TODO populate
        log.error("Invite is not yet supported");
    }

    @Override
    public List<_MatrixID> getJoinedUsers() {
        try {
            URI path = getClientPath("/rooms/{roomId}/joined_members");

            try (CloseableHttpResponse res = client.execute(log(new HttpGet(path)))) {
                Charset charset = ContentType.getOrDefault(res.getEntity()).getCharset();
                String body = IOUtils.toString(res.getEntity().getContent(), charset);

                if (res.getStatusLine().getStatusCode() != 200) {
                    // TODO handle rate limited
                    if (res.getStatusLine().getStatusCode() == 429) {
                        log.warn("Request was rate limited", new Exception());
                    }
                    MatrixErrorInfo info = gson.fromJson(body, MatrixErrorInfo.class);
                    throw new IOException("Couldn't list joined users in " + roomId + " - "
                            + info.getErrcode() + ": " + info.getError());
                }

                JsonObject joinedUsers = jsonParser.parse(body).getAsJsonObject().get("joined")
                        .getAsJsonObject();
                List<_MatrixID> ids = new ArrayList<>();
                for (Map.Entry<String, JsonElement> entry : joinedUsers.entrySet()) {
                    ids.add(new MatrixID(entry.getKey()));
                }

                return ids;
            }
        } catch (IOException e) {
            throw new MatrixClientRequestException(e);
        }
    }

}
