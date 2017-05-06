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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.matrix.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.kamax.matrix.MatrixErrorInfo;
import io.kamax.matrix.MatrixID;
import io.kamax.matrix._MatrixID;
import io.kamax.matrix.hs._MatrixHomeserver;
import io.kamax.matrix.hs._MatrixRoom;
import io.kamax.matrix.json.RoomMessageFormattedTextPutBody;
import io.kamax.matrix.json.RoomMessageTextPutBody;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
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

public class MatrixHttpRoom extends AMatrixHttpClient implements _MatrixRoom {

    private Logger log = LoggerFactory.getLogger(MatrixHttpRoom.class);

    private String roomId;

    public MatrixHttpRoom(_MatrixHomeserver hs, String token, _MatrixID mxId, String roomId) {
        super(hs, token, mxId);

        this.roomId = roomId;
    }

    protected URIBuilder getPathBuilder(String action) {
        URIBuilder builder = super.getPathBuilder(action);
        builder.setPath(builder.getPath().replace("{roomId}", roomId));

        return builder;
    }

    @Override
    public void join() {
        try {
            URI path = getPath("/rooms/{roomId}/join");
            log.info("Doing POST {}", path); // TODO redact access_token by encapsulating toString()
            HttpResponse res = client.execute(new HttpPost(path));

            if (res.getStatusLine().getStatusCode() == 200) {
                log.info("Successfully joined room {} as {}", roomId, getUserId());
            } else {
                if (res.getStatusLine().getStatusCode() == 429) {
                    // TODO handle rate limited
                    log.warn("Request was rate limited", new Exception());
                }

                Charset charset = ContentType.getOrDefault(res.getEntity()).getCharset();
                String body = IOUtils.toString(res.getEntity().getContent(), charset);
                MatrixErrorInfo info = gson.fromJson(body, MatrixErrorInfo.class);

                if (res.getStatusLine().getStatusCode() == 403) {
                    log.error("Failed to join room, we are not allowed: {}", info.getError());
                } else {
                    throw new IOException("Error changing display name for " + getUserId() + " - " + info.getErrcode() + ": " + info.getError());
                }
            }
        } catch (IOException e) {
            throw new MatrixClientRequestException(e);
        }
    }

    @Override
    public void leave() {
        try {
            URI path = getPath("/rooms/{roomId}/leave");
            log.info("Doing POST {}", path); // TODO redact access_token by encapsulating toString()
            HttpResponse res = client.execute(new HttpPost(path));

            if (res.getStatusLine().getStatusCode() == 200) {
                log.info("Successfully left room {} as {}", roomId, getUserId());
            } else {
                if (res.getStatusLine().getStatusCode() == 429) {
                    // TODO handle rate limited
                    log.warn("Request was rate limited", new Exception());
                }

                Charset charset = ContentType.getOrDefault(res.getEntity()).getCharset();
                String body = IOUtils.toString(res.getEntity().getContent(), charset);
                MatrixErrorInfo info = gson.fromJson(body, MatrixErrorInfo.class);

                if (res.getStatusLine().getStatusCode() == 403) {
                    log.error("Failed to leave room, we are not allowed: {}", info.getError());
                } else {
                    throw new IOException("Error when leaving room " + roomId + " as " + getUserId() + " - " + info.getErrcode() + ": " + info.getError());
                }
            }
        } catch (IOException e) {
            throw new MatrixClientRequestException(e);
        }
    }

    private void sendMessage(RoomMessageTextPutBody content) {
        try {
            URI path = getPath("/rooms/{roomId}/send/m.room.message/" + System.currentTimeMillis());
            log.info("Doing PUT {}", path); // TODO redact access_token by encapsulating toString()
            HttpPut req = new HttpPut(path);
            req.setEntity(getJsonEntity(content));
            HttpResponse res = client.execute(req);

            if (res.getStatusLine().getStatusCode() == 200) {
                log.info("Successfully sent message in room {} as {}", roomId, getUserId());
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
                    throw new IOException("Error sending message for " + getUserId() + " - " + info.getErrcode() + ": " + info.getError());
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
    public void invite(_MatrixID mxId) {
        // TODO populate
        log.error("Invite is not yet supported");
    }

    @Override
    public List<_MatrixID> getJoinedUsers() {
        try {
            URI path = getPath("/rooms/{roomId}/joined_members");
            log.info("Doing GET {}", path); // TODO redact access_token by encapsulating toString()
            HttpResponse res = client.execute(new HttpGet(path));
            Charset charset = ContentType.getOrDefault(res.getEntity()).getCharset();
            String body = IOUtils.toString(res.getEntity().getContent(), charset);

            if (res.getStatusLine().getStatusCode() != 200) {
                // TODO handle rate limited
                if (res.getStatusLine().getStatusCode() == 429) {
                    log.warn("Request was rate limited", new Exception());
                }
                MatrixErrorInfo info = gson.fromJson(body, MatrixErrorInfo.class);
                throw new IOException("Couldn't list joined users in " + roomId + " - " + info.getErrcode() + ": " + info.getError());
            }

            JsonObject joinedUsers = jsonParser.parse(body).getAsJsonObject().get("joined").getAsJsonObject();
            List<_MatrixID> ids = new ArrayList<>();
            for (Map.Entry<String, JsonElement> entry : joinedUsers.entrySet()) {
                ids.add(new MatrixID(entry.getKey()));
            }

            return ids;
        } catch (IOException e) {
            throw new MatrixClientRequestException(e);
        }
    }

}
