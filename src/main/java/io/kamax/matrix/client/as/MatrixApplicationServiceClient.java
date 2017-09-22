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

package io.kamax.matrix.client.as;

import io.kamax.matrix.MatrixErrorInfo;
import io.kamax.matrix.MatrixID;
import io.kamax.matrix.client.MatrixClientContext;
import io.kamax.matrix.client.MatrixClientRequestException;
import io.kamax.matrix.client._MatrixClient;
import io.kamax.matrix.client.regular.MatrixHttpClient;
import io.kamax.matrix.hs._MatrixHomeserver;
import io.kamax.matrix.json.VirtualUserRegistrationBody;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

public class MatrixApplicationServiceClient extends MatrixHttpClient
        implements _MatrixApplicationServiceClient {

    private Logger log = LoggerFactory.getLogger(MatrixApplicationServiceClient.class);

    public MatrixApplicationServiceClient(_MatrixHomeserver hs, String token, String localpart) {
        super(new MatrixClientContext(hs, new MatrixID(localpart, hs.getDomain()), token));
    }

    private MatrixHttpClient createClient(String localpart) {
        return new MatrixHttpClient(new MatrixClientContext(getHomeserver(), getMatrixId(localpart),
                getAccessToken(), true));
    }

    @Override
    public _MatrixClient createUser(String localpart) {
        log.info("Creating new user {}", localpart);
        try {
            URI path = getClientPath("/register");
            HttpPost req = new HttpPost(path);
            req.setEntity(getJsonEntity(new VirtualUserRegistrationBody(localpart)));

            try (CloseableHttpResponse res = client.execute(log(req))) {
                if (res.getStatusLine().getStatusCode() == 200) {
                    log.info("Successfully created user {}", localpart);
                } else {
                    Charset charset = ContentType.getOrDefault(res.getEntity()).getCharset();
                    String body = IOUtils.toString(res.getEntity().getContent(), charset);
                    MatrixErrorInfo info = gson.fromJson(body, MatrixErrorInfo.class);
                    if ("M_USER_IN_USE".contentEquals(info.getErrcode())) {
                        log.warn("User {} already exists, ignoring", localpart);
                    } else {
                        // TODO turn into dedicated exceptions, following the Spec distinct errors
                        throw new MatrixClientRequestException(info,
                                "Error creating the new user " + localpart);
                    }
                }
            }
        } catch (IOException e) {
            throw new MatrixClientRequestException(e);
        }

        return createClient(localpart);
    }

    @Override
    public _MatrixClient getUser(String localpart) {
        return createClient(localpart);
    }

}
