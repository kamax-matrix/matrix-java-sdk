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

package io.kamax.matrix.client.regular;

import com.google.gson.JsonObject;

import io.kamax.matrix.MatrixID;
import io.kamax.matrix._MatrixID;
import io.kamax.matrix._MatrixUser;
import io.kamax.matrix.client.*;
import io.kamax.matrix.hs._MatrixRoom;
import io.kamax.matrix.json.*;
import io.kamax.matrix.room.RoomAlias;
import io.kamax.matrix.room.RoomAliasLookup;
import io.kamax.matrix.room._RoomAliasLookup;
import io.kamax.matrix.room._RoomCreationOptions;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.util.Optional;

public class MatrixHttpClient extends AMatrixHttpClient implements _MatrixClient {

    public MatrixHttpClient(String domain) {
        super(domain);
    }

    public MatrixHttpClient(MatrixClientContext context) {
        super(context);
    }

    protected _MatrixID getMatrixId(String localpart) {
        return new MatrixID(localpart, getHomeserver().getDomain());
    }

    @Override
    protected URIBuilder getClientPathBuilder(String action) {
        URIBuilder builder = super.getClientPathBuilder(action);
        context.getUser().ifPresent(user -> builder.setPath(builder.getPath().replace("{userId}", user.getId())));
        return builder;
    }

    @Override
    public void setDisplayName(String name) {
        URI path = getClientPathWithAccessToken("/profile/{userId}/displayname");
        HttpPut req = new HttpPut(path);
        req.setEntity(getJsonEntity(new UserDisplaynameSetBody(name)));
        execute(req);
    }

    @Override
    public _RoomAliasLookup lookup(RoomAlias alias) {
        URI path = getClientPath("/directory/room/" + alias.getId());
        HttpGet req = new HttpGet(path);
        String resBody = execute(req);
        RoomAliasLookupJson lookup = GsonUtil.get().fromJson(resBody, RoomAliasLookupJson.class);
        return new RoomAliasLookup(lookup.getRoomId(), alias.getId(), lookup.getServers());
    }

    @Override
    public _MatrixRoom createRoom(_RoomCreationOptions options) {
        URI path = getClientPathWithAccessToken("/createRoom");
        HttpPost req = new HttpPost(path);
        req.setEntity(getJsonEntity(new RoomCreationRequestJson(options)));

        String resBody = execute(req);
        String roomId = GsonUtil.get().fromJson(resBody, RoomCreationResponseJson.class).getRoomId();
        return getRoom(roomId);
    }

    @Override
    public _MatrixRoom getRoom(String roomId) {
        return new MatrixHttpRoom(getContext(), roomId);
    }

    @Override
    public _MatrixRoom joinRoom(String roomIdOrAlias) {
        URI path = getClientPathWithAccessToken("/join/ " + roomIdOrAlias);
        HttpPost req = new HttpPost(path);
        req.setEntity(getJsonEntity(new JsonObject()));

        String resBody = execute(req);
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

    @Override
    public void login(MatrixPasswordLoginCredentials credentials) {
        HttpPost request = new HttpPost(getClientPath("/login"));
        if (getDeviceId().isPresent()) {
            request.setEntity(getJsonEntity(
                    new LoginPostBody(credentials.getLocalPart(), credentials.getPassword(), getDeviceId().get())));
        } else {
            request.setEntity(getJsonEntity(new LoginPostBody(credentials.getLocalPart(), credentials.getPassword())));
        }

        String body = execute(request);
        LoginResponse response = gson.fromJson(body, LoginResponse.class);
        context.setToken(response.getAccessToken());
        context.setDeviceId(response.getDeviceId());
        context.setUser(new MatrixID(response.getUserId()));

        // FIXME spec returns hostname which we might not be the same as what has been used in baseUrl to login. Must
        // update internals accordingly
    }

    @Override
    public void logout() {
        URI path = getClientPathWithAccessToken("/logout");
        HttpPost req = new HttpPost(path);
        execute(req);
        context.setToken(null);
        context.setUser(null);
        context.setDeviceId(null);
    }

    @Override
    public _SyncData sync(_SyncOptions options) {
        URIBuilder path = getClientPathBuilder("/sync");

        path.addParameter("timeout", options.getTimeout().map(Long::intValue).orElse(30000).toString());
        options.getSince().ifPresent(since -> path.addParameter("since", since));
        options.getFilter().ifPresent(filter -> path.addParameter("filter", filter));
        options.withFullState().ifPresent(state -> path.addParameter("full_state", state ? "true" : "false"));
        options.getSetPresence().ifPresent(presence -> path.addParameter("presence", presence));

        String body = execute(new HttpGet(getWithAccessToken(path)));
        return new SyncDataJson(GsonUtil.parseObj(body));
    }

}
