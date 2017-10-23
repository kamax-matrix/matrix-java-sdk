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

package io.kamax.matrix.client.regular;

import io.kamax.matrix.MatrixID;
import io.kamax.matrix._MatrixID;
import io.kamax.matrix._MatrixUser;
import io.kamax.matrix.client.*;
import io.kamax.matrix.hs._MatrixRoom;
import io.kamax.matrix.json.LoginPostBody;
import io.kamax.matrix.json.LoginResponse;
import io.kamax.matrix.json.UserDisplaynameSetBody;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import java.net.URI;
import java.util.Optional;

public class MatrixHttpClient extends AMatrixHttpClient implements _MatrixClient {

    public MatrixHttpClient(MatrixClientContext context) {
        super(context);
    }

    protected _MatrixID getMatrixId(String localpart) {
        return new MatrixID(localpart, getHomeserver().getDomain());
    }

    @Override
    public void setDisplayName(String name) {
        URI path = getClientPathWithAccessToken("/profile/" + context.getUser().getId() + "/displayname");
        HttpPut req = new HttpPut(path);
        req.setEntity(getJsonEntity(new UserDisplaynameSetBody(name)));
        execute(req);
    }

    @Override
    public _MatrixRoom getRoom(String roomId) {
        return new MatrixHttpRoom(getContext(), roomId);
    }

    @Override
    public _MatrixUser getUser(_MatrixID mxId) {
        return new MatrixHttpUser(getContext(), mxId);
    }

    @Override
    public Optional<String> getDeviceId() {
        return context.getDeviceId();
    }

    @Override
    public void login() {
        HttpPost request = new HttpPost(getClientPath("/login"));
        _MatrixID user = context.getUser();
        String password = context.getPassword()
                .orElseThrow(() -> new IllegalStateException("You have to provide a password to be able to login."));
        if (context.getDeviceId().isPresent()) {
            request.setEntity(
                    getJsonEntity(new LoginPostBody(user.getLocalPart(), password, context.getDeviceId().get())));
        } else {
            request.setEntity(getJsonEntity(new LoginPostBody(user.getLocalPart(), password)));
        }

        String body = execute(request);
        LoginResponse response = gson.fromJson(body, LoginResponse.class);
        context.setToken(response.getAccessToken());
        context.setDeviceId(response.getDeviceId());
    }

    @Override
    public void logout() {
        URI path = getClientPathWithAccessToken("/logout");
        HttpPost req = new HttpPost(path);
        execute(req);
    }

}
