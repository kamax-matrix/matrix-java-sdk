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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.kamax.matrix.MatrixErrorInfo;
import io.kamax.matrix.MatrixID;
import io.kamax.matrix._MatrixContent;
import io.kamax.matrix._MatrixID;
import io.kamax.matrix._MatrixUserProfile;
import io.kamax.matrix.hs._MatrixRoom;
import io.kamax.matrix.json.GsonUtil;
import io.kamax.matrix.json.RoomMessageChunkResponseJson;
import io.kamax.matrix.json.RoomMessageFormattedTextPutBody;
import io.kamax.matrix.json.RoomMessageTextPutBody;
import io.kamax.matrix.json.RoomTagSetBody;
import io.kamax.matrix.json.event.MatrixJsonPersistentEvent;
import io.kamax.matrix.room.*;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java8.util.Optional;
import java8.util.stream.StreamSupport;


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
    public String getId() {
        return roomId;
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
        executeAuthenticated(new Request.Builder().post(getJsonBody(new JsonObject())).url(path));
    }

    @Override
    public void sendReceipt(ReceiptType type, String eventId) {
        sendReceipt(type.getId(), eventId);
    }

    @Override
    public void sendReadReceipt(String eventId) {
        sendReceipt(ReceiptType.Read, eventId);
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

    @Override
    public List<RoomTag> getUserTags() {
        return getAllTags().stream().filter(tag -> "u".equals(tag.getNamespace())).collect(Collectors.toList());
    }

    private List<RoomTag> getAllTags() {

        URL path = getClientPath("user", getUserId(), "rooms", getAddress(), "tags");

        String body = executeAuthenticated(new Request.Builder().get().url(path));

        JsonObject jsonTags = GsonUtil.parseObj(body).getAsJsonObject("tags").getAsJsonObject();
        List<RoomTag> tags = jsonTags.entrySet().stream().map(entry -> {
            String completeName = entry.getKey();
            String name = "";
            String namespace = "";
            if (completeName.startsWith("m.")) {
                name = completeName.substring(2);
                namespace = "m";
            } else if (completeName.startsWith("u.")) {
                name = completeName.substring(2);
                namespace = "u";
            } else {
                name = completeName;
            }
            JsonElement jsonOrder = entry.getValue().getAsJsonObject().get("order");
            Double order = null;
            if (jsonOrder != null) {
                order = jsonOrder.getAsDouble();
            }

            return new RoomTag(namespace, name, order);
        }).collect(Collectors.toList());

        return tags;
    }

    @Override
    public void addUserTag(String tag) {
        addTag("u." + tag, null);
    }

    @Override
    public void addUserTag(String tag, double order) {
        addTag("u." + tag, order);
    }

    @Override
    public void deleteUserTag(String tag) {
        deleteTag("u." + tag);
    }

    @Override
    public void addFavouriteTag() {
        addTag("m.favourite", null);
    }

    @Override
    public void addFavouriteTag(double order) {
        addTag("m.favourite", order);
    }

    @Override
    public Optional<RoomTag> getFavouriteTag() {
        return StreamSupport.stream(getAllTags())
                .filter(tag -> "m".equals(tag.getNamespace()) && "favourite".equals(tag.getName())).findFirst();
    }

    @Override
    public void deleteFavouriteTag() {
        deleteTag("m.favourite");
    }

    @Override
    public void addLowpriorityTag() {
        addTag("m.lowpriority", null);
    }

    @Override
    public void addLowpriorityTag(double order) {
        addTag("m.lowpriority", order);
    }

    @Override
    public Optional<RoomTag> getLowpriorityTag() {
        return StreamSupport.stream(getAllTags())
                .filter(tag -> "m".equals(tag.getNamespace()) && "lowpriority".equals(tag.getName())).findFirst();
    }

    @Override
    public void deleteLowpriorityTag() {
        deleteTag("m.lowpriority");
    }

    private void addTag(String tag, Double order) {
        // TODO check name size

        if (order != null && (order < 0 || order > 1)) {
            throw new IllegalArgumentException("Order out of range!");
        }

        URL path = getClientPath("user", getUserId(), "rooms", getAddress(), "tags", tag);
        Request.Builder request = new Request.Builder().url(path);
        if (order != null) {
            request.put(getJsonBody(new RoomTagSetBody(order)));
        } else {
            request.put(getJsonBody(new JsonObject()));
        }
        executeAuthenticated(request);
    }

    private void deleteTag(String tag) {
        URL path = getClientPath("user", getUserId(), "rooms", getAddress(), "tags", tag);
        executeAuthenticated(new Request.Builder().url(path).delete());
    }
}
