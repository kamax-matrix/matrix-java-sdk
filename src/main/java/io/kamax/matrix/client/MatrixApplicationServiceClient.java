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
import io.kamax.matrix.MatrixErrorInfo;
import io.kamax.matrix._MatrixUser;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

public class MatrixApplicationServiceClient extends MatrixClient implements _MatrixApplicationServiceClient {

    private Logger log = LoggerFactory.getLogger(MatrixApplicationServiceClient.class);

    private HttpClient client = HttpClients.createDefault();
    private Gson gson = new Gson();

    @Override
    public void createUser(_MatrixUser user) {
        String mxId = user.getId().getId();

        log.info("Creating new user {}", mxId);
        try {
            URI path = getPath("/register");
            log.info("Doing POST {}", path); // TODO redact access_token by encapsulating toString()
            HttpPost req = new HttpPost(path);
            req.setEntity(getJsonEntity(new VirtualUserRegistrationBody(user.getId().getLocalPart())));

            HttpResponse res = client.execute(req);

            if (res.getStatusLine().getStatusCode() == 200) {
                log.info("Successfully created user {}", mxId);
            } else {
                Charset charset = ContentType.getOrDefault(res.getEntity()).getCharset();
                String body = IOUtils.toString(res.getEntity().getContent(), charset);
                MatrixErrorInfo info = gson.fromJson(body, MatrixErrorInfo.class);
                if ("M_USER_IN_USE".contentEquals(info.getErrcode())) {
                    log.warn("User {} already exists, ignoring", user.getId().getId());
                } else {
                    throw new IOException("Error creating the new user " + mxId + " - " + info.getErrcode() + ": " + info.getError());
                }
            }
        } catch (IOException e) {
            throw new MatrixClientRequestException(e);
        }
    }

    private class VirtualUserRegistrationBody {
        private String type = "m.login.application_server";
        private String username;

        public VirtualUserRegistrationBody(String username) {
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        public String getType() {
            return type;
        }

    }

}
