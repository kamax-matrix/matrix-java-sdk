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

import java.net.URI;
import java.net.URISyntaxException;

public abstract class AMatrixHttpClient implements _MatrixClientRaw {

    protected MatrixClientContext context;

    protected HttpClient client = HttpClients.createDefault();
    protected Gson gson = new Gson();
    protected JsonParser jsonParser = new JsonParser();

    public AMatrixHttpClient(MatrixClientContext context) {
        this.context = context;
    }

    @Override
    public MatrixClientContext getContext() {
        return context;
    }

    @Override
    public _MatrixHomeserver getHomeserver() {
        return context.getHs();
    }

    @Override
    public String getAccessToken() {
        return context.getToken();
    }

    @Override
    public _MatrixID getUser() {
        return context.getUser();
    }

    protected URIBuilder getPathBuilder(String action) {
        URIBuilder builder = context.getHs().getClientEndpoint();
        builder.setPath(builder.getPath() + "/_matrix/client/r0" + action);
        builder.setParameter("access_token", context.getToken());
        builder.setPath(builder.getPath().replace("{userId}", context.getUser().getId()));
        if (context.isVirtualUser()) {
            builder.setParameter("user_id", context.getUser().getId());
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
