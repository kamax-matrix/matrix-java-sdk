/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Maxime Dor
 *
 * https://www.kamax.io/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.matrix.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.kamax.matrix.MatrixID;
import io.kamax.matrix._MatrixID;
import io.kamax.matrix.hs._MatrixRoom;
import io.kamax.matrix.json.GsonUtil;
import io.kamax.matrix.json.RoomMessageChunkResponseJson;
import io.kamax.matrix.json.RoomMessageFormattedTextPutBody;
import io.kamax.matrix.json.RoomMessageTextPutBody;
import io.kamax.matrix.json.event.MatrixJsonEvent;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        URI path = getClientPathWithAccessToken("/rooms/{roomId}/state/m.room.name");

        MatrixHttpRequest request = new MatrixHttpRequest(new HttpGet(path));
        request.addIgnoredErrorCode(404);
        String body = execute(request);
        return extractAsStringFromBody(body, "name");
    }

    @Override
    public Optional<String> getTopic() {
        URI path = getClientPathWithAccessToken("/rooms/{roomId}/state/m.room.topic");
        MatrixHttpRequest matrixRequest = new MatrixHttpRequest(new HttpGet(path));
        matrixRequest.addIgnoredErrorCode(404);
        String body = execute(matrixRequest);
        return extractAsStringFromBody(body, "topic");
    }

    @Override
    public void join() {
        URI path = getClientPathWithAccessToken("/rooms/{roomId}/join");
        execute(new HttpPost(path));
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

    private void sendMessage(RoomMessageTextPutBody content) {
        URI path = getClientPathWithAccessToken("/rooms/{roomId}/send/m.room.message/" + System.currentTimeMillis());
        HttpPut httpPut = new HttpPut(path);
        httpPut.setEntity(getJsonEntity(content));
        execute(httpPut);
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
        URI path = getClientPathWithAccessToken("/rooms/{roomId}/invite");
        HttpPost req = new HttpPost(path);
        req.setEntity(getJsonEntity(GsonUtil.makeObj("user_id", mxId.getId())));
        execute(req);
    }

    @Override
    public List<_MatrixID> getJoinedUsers() {
        URI path = getClientPathWithAccessToken("/rooms/{roomId}/joined_members");
        String body = execute(new HttpGet(path));

        List<_MatrixID> ids = new ArrayList<>();
        if (StringUtils.isNotEmpty(body)) {
            JsonObject joinedUsers = jsonParser.parse(body).getAsJsonObject().get("joined").getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : joinedUsers.entrySet()) {
                ids.add(new MatrixID(entry.getKey()));
            }
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
                body.getChunk().stream().map(MatrixJsonEvent::new).collect(Collectors.toList()));
    }
}
