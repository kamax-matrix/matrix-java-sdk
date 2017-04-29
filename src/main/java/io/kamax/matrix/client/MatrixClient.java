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

import com.google.gson.Gson;
import io.kamax.matrix._MatrixID;
import io.kamax.matrix._MatrixUser;
import io.kamax.matrix.hs._MatrixHomeserver;
import io.kamax.matrix.hs._Room;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class MatrixClient implements _MatrixClient {

    private _MatrixHomeserver hs;
    private String token;
    private _MatrixID mxId;

    private Gson gson = new Gson();

    protected URI getPath(String action) {
        URIBuilder builder = hs.getClientEndpoint();
        builder.setPath(builder.getPath() + "/_matrix/client/r0" + action);
        builder.setParameter("access_token", token);
        try {
            return builder.build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected HttpEntity getJsonEntity(Object o) {
        return EntityBuilder.create()
                .setText(gson.toJson(o))
                .setContentType(ContentType.APPLICATION_JSON)
                .build();
    }

    @Override
    public void setHomeserver(_MatrixHomeserver hs) {
        this.hs = hs;
    }

    @Override
    public void setAccessToken(String token) {
        this.token = token;
    }

    @Override
    public void setUserId(_MatrixID mxId) {
        this.mxId = mxId;
    }

    @Override
    public Optional<_Room> findRoom(String roomId) {
        return null;
    }

    @Override
    public _MatrixUser getUser(_MatrixID mxId) {
        return null;
    }

}
