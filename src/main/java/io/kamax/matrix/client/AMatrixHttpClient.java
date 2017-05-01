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
import com.google.gson.JsonParser;
import io.kamax.matrix._MatrixID;
import io.kamax.matrix.hs._MatrixHomeserver;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public abstract class AMatrixHttpClient implements _MatrixClientRaw {

    private Logger log = LoggerFactory.getLogger(AMatrixHttpClient.class);

    protected _MatrixHomeserver hs;
    protected String token;
    protected _MatrixID mxId;

    protected HttpClient client = HttpClients.createDefault();
    protected Gson gson = new Gson();
    protected JsonParser jsonParser = new JsonParser();

    public AMatrixHttpClient(_MatrixHomeserver hs, String token, _MatrixID mxId) {
        setHomeserver(hs);
        setAccessToken(token);
        setUserId(mxId);
    }

    @Override
    public _MatrixHomeserver getHomeserver() {
        return hs;
    }

    @Override
    public String getAccessToken() {
        return token;
    }

    @Override
    public _MatrixID getUserId() {
        return mxId;
    }

    private void setHomeserver(_MatrixHomeserver hs) {
        this.hs = hs;
    }

    private void setAccessToken(String token) {
        this.token = token;
    }

    private void setUserId(_MatrixID mxId) {
        this.mxId = mxId;
    }

    protected URIBuilder getPathBuilder(String action) {
        URIBuilder builder = hs.getClientEndpoint();
        builder.setPath(builder.getPath() + "/_matrix/client/r0" + action);
        builder.setParameter("access_token", token);
        if (mxId != null) {
            builder.setPath(builder.getPath().replace("{userId}", mxId.getId()));
            builder.setParameter("user_id", mxId.getId());
        }

        return builder;
    }

    protected URI getPath(String action) {
        try {
            return getPathBuilder(action).build();
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

}
