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
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AMatrixHttpClient implements _MatrixClientRaw {

    private Logger log = LoggerFactory.getLogger(AMatrixHttpClient.class);

    protected MatrixClientContext context;

    protected CloseableHttpClient client = HttpClients.createDefault();
    protected Gson gson = new Gson();
    protected JsonParser jsonParser = new JsonParser();
    private Pattern accessTokenUrlPattern = Pattern.compile("\\?access_token=(?<token>[^&]*)");

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

    protected HttpRequestBase log(HttpRequestBase req) {
        String reqUrl = req.getURI().toASCIIString();
        Matcher m = accessTokenUrlPattern.matcher(reqUrl);
        if (m.find()) {
            StringBuilder b = new StringBuilder();
            b.append(reqUrl.substring(0, m.start("token")));
            b.append("<redacted>");
            b.append(reqUrl.substring(m.end("token"), reqUrl.length()));
            reqUrl = b.toString();
        }

        log.info("Doing {} {}", req.getMethod(), reqUrl);

        return req;
    }

    protected URIBuilder getPathBuilder(String module, String version, String action) {
        URIBuilder builder = context.getHs().getClientEndpoint();
        builder.setPath(builder.getPath() + "/_matrix/" + module + "/" + version + action);
        builder.setParameter("access_token", context.getToken());
        builder.setPath(builder.getPath().replace("{userId}", context.getUser().getId()));
        if (context.isVirtualUser()) {
            builder.setParameter("user_id", context.getUser().getId());
        }

        return builder;
    }

    protected URIBuilder getClientPathBuilder(String action) {
        return getPathBuilder("client", "r0", action);
    }

    protected URIBuilder getMediaPathBuilder(String action) {
        return getPathBuilder("media", "v1", action);
    }

    protected URI getClientPath(String action) {
        try {
            return getClientPathBuilder(action).build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected URI getMediaPath(String action) {
        try {
            return getMediaPathBuilder(action).build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected HttpEntity getJsonEntity(Object o) {
        return EntityBuilder.create().setText(gson.toJson(o))
                .setContentType(ContentType.APPLICATION_JSON).build();
    }

}
