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

package io.kamax.matrix.client.regular;

import com.google.gson.JsonObject;

import io.kamax.matrix.MatrixID;
import io.kamax.matrix._MatrixContent;
import io.kamax.matrix._MatrixID;
import io.kamax.matrix._MatrixUser;
import io.kamax.matrix.client.*;
import io.kamax.matrix.hs._MatrixRoom;
import io.kamax.matrix.json.*;
import io.kamax.matrix.room.RoomAlias;
import io.kamax.matrix.room.RoomAliasLookup;
import io.kamax.matrix.room._RoomAliasLookup;
import io.kamax.matrix.room._RoomCreationOptions;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import okhttp3.*;

public class MatrixHttpClient extends AMatrixHttpClient implements _MatrixClient {

    public MatrixHttpClient(String domain) {
        super(domain);
    }

    public MatrixHttpClient(MatrixClientContext context) {
        super(context);
    }

    public MatrixHttpClient(MatrixClientContext context, MatrixClientDefaults defaults) {
        super(context, defaults);
    }

    public MatrixHttpClient(MatrixClientContext context, OkHttpClient client) {
        super(context, client);
    }

    protected _MatrixID getMatrixId(String localpart) {
        return new MatrixID(localpart, getHomeserver().getDomain());
    }

    @Override
    public _MatrixID getWhoAmI() {
        URL path = getClientPath("account", "whoami");
        String body = executeAuthenticated(new Request.Builder().get().url(path));
        return MatrixID.from(GsonUtil.getStringOrThrow(GsonUtil.parseObj(body), "user_id")).acceptable();
    }

    @Override
    public void setDisplayName(String name) {
        URL path = getClientPath("profile", getUserId(), "displayname");
        execute(new Request.Builder().put(getJsonBody(new UserDisplaynameSetBody(name))).url(path));
    }

    @Override
    public _RoomAliasLookup lookup(RoomAlias alias) {
        URL path = getClientPath("directory", "room", alias.getId());
        String resBody = execute(new Request.Builder().get().url(path));
        RoomAliasLookupJson lookup = GsonUtil.get().fromJson(resBody, RoomAliasLookupJson.class);
        return new RoomAliasLookup(lookup.getRoomId(), alias.getId(), lookup.getServers());
    }

    @Override
    public _MatrixRoom createRoom(_RoomCreationOptions options) {
        URL path = getClientPath("createRoom");
        String resBody = executeAuthenticated(
                new Request.Builder().post(getJsonBody(new RoomCreationRequestJson(options))).url(path));
        String roomId = GsonUtil.get().fromJson(resBody, RoomCreationResponseJson.class).getRoomId();
        return getRoom(roomId);
    }

    @Override
    public _MatrixRoom getRoom(String roomId) {
        return new MatrixHttpRoom(getContext(), roomId);
    }

    @Override
    public List<_MatrixRoom> getJoinedRooms() {
        URL path = getClientPath("joined_rooms");
        JsonObject resBody = GsonUtil.parseObj(executeAuthenticated(new Request.Builder().get().url(path)));
        return GsonUtil.asList(resBody, "joined_rooms", String.class).stream().map(this::getRoom)
                .collect(Collectors.toList());
    }

    @Override
    public _MatrixRoom joinRoom(String roomIdOrAlias) {
        URL path = getClientPath("join", roomIdOrAlias);
        String resBody = executeAuthenticated(new Request.Builder().post(getJsonBody(new JsonObject())).url(path));
        String roomId = GsonUtil.get().fromJson(resBody, RoomCreationResponseJson.class).getRoomId();
        return getRoom(roomId);
    }

    @Override
    public _MatrixUser getUser(_MatrixID mxId) {
        return new MatrixHttpUser(getContext(), mxId);
    }

    @Override
    public Optional<String> getDeviceId() {
        return Optional.ofNullable(context.getDeviceId());
    }

    protected void updateContext(String resBody) {
        LoginResponse response = gson.fromJson(resBody, LoginResponse.class);
        context.setToken(response.getAccessToken());
        context.setDeviceId(response.getDeviceId());
        context.setUser(MatrixID.asAcceptable(response.getUserId()));
    }

    @Override
    public void register(MatrixPasswordCredentials credentials, String sharedSecret, boolean admin) {
        // As per synapse registration script:
        // https://github.com/matrix-org/synapse/blob/master/scripts/register_new_matrix_user#L28

        String value = credentials.getLocalPart() + "\0" + credentials.getPassword() + "\0"
                + (admin ? "admin" : "notadmin");
        String mac = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, sharedSecret).hmacHex(value);
        JsonObject body = new JsonObject();
        body.addProperty("user", credentials.getLocalPart());
        body.addProperty("password", credentials.getPassword());
        body.addProperty("mac", mac);
        body.addProperty("type", "org.matrix.login.shared_secret");
        body.addProperty("admin", false);
        URL url = getPath("client", "api", "v1", "register");
        updateContext(execute(new Request.Builder().post(getJsonBody(body)).url(url)));
    }

    @Override
    public void login(MatrixPasswordCredentials credentials) {
        URL url = getClientPath("login");

        LoginPostBody data = new LoginPostBody(credentials.getLocalPart(), credentials.getPassword());
        getDeviceId().ifPresent(data::setDeviceId);
        Optional.ofNullable(context.getInitialDeviceName()).ifPresent(data::setInitialDeviceDisplayName);

        updateContext(execute(new Request.Builder().post(getJsonBody(data)).url(url)));
    }

    @Override
    public void logout() {
        URL path = getClientPath("logout");
        executeAuthenticated(new Request.Builder().post(getJsonBody(new JsonObject())).url(path));
        context.setToken(null);
        context.setUser(null);
        context.setDeviceId(null);
    }

    @Override
    public _SyncData sync(_SyncOptions options) {
        HttpUrl.Builder path = getClientPathBuilder("sync");
        path.addQueryParameter("timeout", options.getTimeout().map(Long::intValue).orElse(30000).toString());
        options.getSince().ifPresent(since -> path.addQueryParameter("since", since));
        options.getFilter().ifPresent(filter -> path.addQueryParameter("filter", filter));
        options.withFullState().ifPresent(state -> path.addQueryParameter("full_state", state ? "true" : "false"));
        options.getSetPresence().ifPresent(presence -> path.addQueryParameter("presence", presence));

        String body = executeAuthenticated(new Request.Builder().get().url(path.build().url()));
        return new SyncDataJson(GsonUtil.parseObj(body));
    }

    @Override
    public _MatrixContent getMedia(String mxUri) throws IllegalArgumentException {
        return getMedia(URI.create(mxUri));
    }

    @Override
    public _MatrixContent getMedia(URI mxUri) throws IllegalArgumentException {
        return new MatrixHttpContent(context, mxUri);
    }

    private String putMedia(Request.Builder builder) {
        String body = execute(builder.url(getMediaPath("upload")));
        return GsonUtil.getStringOrThrow(GsonUtil.parseObj(body), "content_uri");
    }

    @Override
    public String putMedia(byte[] data, String type) {
        return putMedia(new Request.Builder().post(RequestBody.create(MediaType.parse(type), data)));
    }

    @Override
    public String putMedia(File data, String type) {
        return putMedia(new Request.Builder().post(RequestBody.create(MediaType.parse(type), data)));
    }

}
