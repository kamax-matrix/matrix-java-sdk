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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import okhttp3.HttpUrl;
import okhttp3.Request;

public class MatrixHttpRoom extends AMatrixHttpClient implements _MatrixRoom {

    private Logger log = LoggerFactory.getLogger(MatrixHttpRoom.class);

    private String roomId;

    public MatrixHttpRoom(MatrixClientContext context, String roomId) {
        super(context);
        this.roomId = roomId;
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
        URL path = getClientPath("rooms", getAddress(), "state", type);

        MatrixHttpRequest request = new MatrixHttpRequest(new Request.Builder().get().url(path));
        request.addIgnoredErrorCode(404);
        String body = executeAuthenticated(request);
        if (StringUtils.isBlank(body)) {
            return Optional.empty();
        }

        return Optional.of(GsonUtil.parseObj(body));
    }

    @Override
    public Optional<JsonObject> getState(String type, String key) {
        URL path = getClientPath("rooms", roomId, "state", type, key);

        MatrixHttpRequest request = new MatrixHttpRequest(new Request.Builder().get().url(path));
        request.addIgnoredErrorCode(404);
        String body = executeAuthenticated(request);
        if (StringUtils.isBlank(body)) {
            return Optional.empty();
        }

        return Optional.of(GsonUtil.parseObj(body));
    }

    @Override
    public void join() {
        URL path = getClientPath("rooms", roomId, "join");
        executeAuthenticated(new Request.Builder().post(getJsonBody(new JsonObject())).url(path));
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
        URL path = getClientPath("rooms", roomId, "leave");
        MatrixHttpRequest request = new MatrixHttpRequest(
                new Request.Builder().post(getJsonBody(new JsonObject())).url(path));

        // TODO Find a better way to handle room objects for unknown rooms
        // Maybe throw exception?
        // TODO implement method to check room existence - isValid() ?
        // if (res.getStatusLine().getStatusCode() == 404) {
        // log.warn("Room {} is not joined, ignoring call", roomId);
        // return;
        // }
        request.addIgnoredErrorCode(404);
        executeAuthenticated(request);
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
        URL path = getClientPath("rooms", roomId, "send", type, Long.toString(System.currentTimeMillis()));
        String body = executeAuthenticated(new Request.Builder().put(getJsonBody(content)).url(path));
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
        URL path = getClientPath("rooms", roomId, "receipt", type, eventId);
        executeAuthenticated(new Request.Builder().post(null).url(path));
    }

    @Override
    public void invite(_MatrixID mxId) {
        URL path = getClientPath("rooms", roomId, "invite");
        executeAuthenticated(
                new Request.Builder().post(getJsonBody(GsonUtil.makeObj("user_id", mxId.getId()))).url(path));
    }

    @Override
    public List<_MatrixUserProfile> getJoinedUsers() {
        URL path = getClientPath("rooms", roomId, "joined_members");
        String body = executeAuthenticated(new Request.Builder().get().url(path));

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
        HttpUrl.Builder builder = getClientPathBuilder("rooms", roomId, "messages");
        builder.addQueryParameter("from", options.getFromToken());
        builder.addQueryParameter("dir", options.getDirection());
        options.getToToken().ifPresent(token -> builder.addQueryParameter("to", token));
        options.getLimit().ifPresent(limit -> builder.addQueryParameter("limit", limit.toString()));

        String bodyRaw = executeAuthenticated(new Request.Builder().get().url(builder.build().url()));
        RoomMessageChunkResponseJson body = GsonUtil.get().fromJson(bodyRaw, RoomMessageChunkResponseJson.class);
        return new MatrixRoomMessageChunk(body.getStart(), body.getEnd(),
                body.getChunk().stream().map(MatrixJsonPersistentEvent::new).collect(Collectors.toList()));
    }

}
