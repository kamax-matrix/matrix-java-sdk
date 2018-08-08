/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Kamax Sarl
 *
 * https://www.kamax.io/
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

import com.google.gson.JsonObject;

import io.kamax.matrix.*;
import io.kamax.matrix.hs._MatrixRoom;
import io.kamax.matrix.json.GsonUtil;
import io.kamax.matrix.json.RoomMessageChunkResponseJson;
import io.kamax.matrix.json.RoomMessageFormattedTextPutBody;
import io.kamax.matrix.json.RoomMessageTextPutBody;
import io.kamax.matrix.json.event.MatrixJsonPersistentEvent;
import io.kamax.matrix.room.MatrixRoomMessageChunk;
import io.kamax.matrix.room._MatrixRoomMessageChunk;
import io.kamax.matrix.room._MatrixRoomMessageChunkOptions;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MatrixHttpRoom extends AMatrixHttpClient implements _MatrixRoom {

    private Logger log = LoggerFactory.getLogger(MatrixHttpRoom.class);

    private String roomId;

    public MatrixHttpRoom(MatrixClientContext context, String roomId) {
        super(context);
        this.roomId = roomId;
    }

    @Override
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
        return getState("m.room.name").flatMap(obj -> GsonUtil.findString(obj, "name"));
    }

    @Override
    public Optional<String> getTopic() {
        return getState("m.room.topic").flatMap(obj -> GsonUtil.findString(obj, "topic"));
    }

    @Override
    public Optional<String> getAvatarUrl() {
        return getState("m.room.avatar").flatMap(obj -> GsonUtil.findString(obj, "url"));
    }

    @Override
    public Optional<_MatrixContent> getAvatar() {
        return getAvatarUrl().flatMap(url -> {
            try {
                return Optional.of(new MatrixHttpContent(context, new URI(url)));
            } catch (URISyntaxException e) {
                log.debug("{} is not a valid URI for avatar, returning empty", url);
                return Optional.empty();
            }
        });
    }

    @Override
    public Optional<JsonObject> getState(String type) {
        URI path = getClientPathWithAccessToken("/rooms/{roomId}/state/" + type);

        MatrixHttpRequest request = new MatrixHttpRequest(new HttpGet(path));
        request.addIgnoredErrorCode(404);
        String body = execute(request);
        if (StringUtils.isBlank(body)) {
            return Optional.empty();
        }

        return Optional.of(GsonUtil.parseObj(body));
    }

    @Override
    public Optional<JsonObject> getState(String type, String key) {
        URI path = getClientPathWithAccessToken("/rooms/{roomId}/state/" + type + "/" + key);

        MatrixHttpRequest request = new MatrixHttpRequest(new HttpGet(path));
        request.addIgnoredErrorCode(404);
        String body = execute(request);
        if (StringUtils.isBlank(body)) {
            return Optional.empty();
        }

        return Optional.of(GsonUtil.parseObj(body));
    }

    @Override
    public void join() {
        URI path = getClientPathWithAccessToken("/rooms/{roomId}/join");
        execute(new HttpPost(path));
    }

    @Override
    public Optional<MatrixErrorInfo> tryJoin() {
        try {
            join();
            return Optional.empty();
        } catch (MatrixClientRequestException e) {
            return e.getError();
        }
    }

    @Override
    public void leave() {
        URI path = getClientPathWithAccessToken("/rooms/{roomId}/leave");
        MatrixHttpRequest request = new MatrixHttpRequest(new HttpPost(path));

        // TODO Find a better way to handle room objects for unknown rooms
        // Maybe throw exception?
        // TODO implement method to check room existence - isValid() ?
        // if (res.getStatusLine().getStatusCode() == 404) {
        // log.warn("Room {} is not joined, ignoring call", roomId);
        // return;
        // }
        request.addIgnoredErrorCode(404);
        execute(request);
    }

    @Override
    public Optional<MatrixErrorInfo> tryLeave() {
        try {
            leave();
            return Optional.empty();
        } catch (MatrixClientRequestException e) {
            return e.getError();
        }
    }

    @Override
    public String sendEvent(String type, JsonObject content) {
        // FIXME URL encoding
        URI path = getClientPathWithAccessToken("/rooms/{roomId}/send/" + type + "/" + System.currentTimeMillis());
        HttpPut httpPut = new HttpPut(path);
        httpPut.setEntity(getJsonEntity(content));
        String body = execute(httpPut);
        return GsonUtil.getStringOrThrow(GsonUtil.parseObj(body), "event_id");
    }

    private String sendMessage(RoomMessageTextPutBody content) {
        return sendEvent("m.room.message", GsonUtil.makeObj(content));
    }

    @Override
    public String sendText(String message) {
        return sendMessage(new RoomMessageTextPutBody(message));
    }

    @Override
    public String sendFormattedText(String formatted, String rawFallback) {
        // TODO sanitize input
        return sendMessage(new RoomMessageFormattedTextPutBody(rawFallback, formatted));
    }

    @Override
    public String sendNotice(String message) {
        return sendMessage(new RoomMessageTextPutBody("m.notice", message));
    }

    @Override
    public String sendNotice(String formatted, String plain) {
        // TODO sanitize input
        return sendMessage(new RoomMessageFormattedTextPutBody("m.notice", plain, formatted));
    }

    @Override
    public void sendReceipt(String type, String eventId) {
        URI path = getClientPathWithAccessToken("/rooms/{roomId}/receipt/" + type + "/" + eventId);
        HttpPost req = new HttpPost(path);
        execute(req);
    }

    @Override
    public void invite(_MatrixID mxId) {
        URI path = getClientPathWithAccessToken("/rooms/{roomId}/invite");
        HttpPost req = new HttpPost(path);
        req.setEntity(getJsonEntity(GsonUtil.makeObj("user_id", mxId.getId())));
        execute(req);
    }

    @Override
    public List<_MatrixUserProfile> getJoinedUsers() {
        URI path = getClientPathWithAccessToken("/rooms/{roomId}/joined_members");
        String body = execute(new HttpGet(path));

        List<_MatrixUserProfile> ids = new ArrayList<>();
        if (StringUtils.isNotEmpty(body)) {
            JsonObject joinedUsers = jsonParser.parse(body).getAsJsonObject().get("joined").getAsJsonObject();
            ids = joinedUsers.entrySet().stream().filter(e -> e.getValue().isJsonObject()).map(entry -> {
                JsonObject obj = entry.getValue().getAsJsonObject();
                return new MatrixHttpUser(getContext(), MatrixID.asAcceptable(entry.getKey())) {

                    @Override
                    public Optional<String> getName() {
                        return GsonUtil.findString(obj, "display_name");
                    }

                    @Override
                    public Optional<_MatrixContent> getAvatar() {
                        return GsonUtil.findString(obj, "avatar_url").flatMap(s -> {
                            try {
                                return Optional.of(new URI(s));
                            } catch (URISyntaxException e) {
                                return Optional.empty();
                            }
                        }).map(uri -> new MatrixHttpContent(getContext(), uri));
                    }

                };
            }).collect(Collectors.toList());
        }

        return ids;
    }

    @Override
    public _MatrixRoomMessageChunk getMessages(_MatrixRoomMessageChunkOptions options) {
        URIBuilder builder = getClientPathBuilder("/rooms/{roomId}/messages");
        builder.setParameter("from", options.getFromToken());
        builder.setParameter("dir", options.getDirection());
        options.getToToken().ifPresent(token -> builder.setParameter("to", token));
        options.getLimit().ifPresent(limit -> builder.setParameter("limit", limit.toString()));

        String bodyRaw = execute(new HttpGet(getWithAccessToken(builder)));
        RoomMessageChunkResponseJson body = GsonUtil.get().fromJson(bodyRaw, RoomMessageChunkResponseJson.class);
        return new MatrixRoomMessageChunk(body.getStart(), body.getEnd(),
                body.getChunk().stream().map(MatrixJsonPersistentEvent::new).collect(Collectors.toList()));
    }
}
