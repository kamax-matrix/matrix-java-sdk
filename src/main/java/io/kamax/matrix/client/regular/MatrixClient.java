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

package io.kamax.matrix.client.regular;

import io.kamax.matrix.MatrixErrorInfo;
import io.kamax.matrix.MatrixID;
import io.kamax.matrix._MatrixID;
import io.kamax.matrix._MatrixUser;
import io.kamax.matrix.client.AMatrixHttpClient;
import io.kamax.matrix.client.MatrixClientRequestException;
import io.kamax.matrix.client.MatrixHttpRoom;
import io.kamax.matrix.client._MatrixClient;
import io.kamax.matrix.hs._MatrixHomeserver;
import io.kamax.matrix.hs._MatrixRoom;
import io.kamax.matrix.json.UserDisplaynameSetBody;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

public class MatrixClient extends AMatrixHttpClient implements _MatrixClient {

    private Logger log = LoggerFactory.getLogger(MatrixClient.class);

    public MatrixClient(_MatrixHomeserver hs, String token) {
        this(hs, token, null);
    }

    public MatrixClient(_MatrixHomeserver hs, String token, _MatrixID mxId) {
        super(hs, token, mxId);
    }

    protected _MatrixID getMatrixId(String localpart) {
        return new MatrixID(localpart, hs.getDomain());
    }

    @Override
    public void setDisplayName(String name) {
        try {
            URI path = getPath("/profile/{userId}/displayname");
            log.info("Doing PUT {}", path); // TODO redact access_token by encapsulating toString()
            HttpPut req = new HttpPut(path);
            req.setEntity(getJsonEntity(new UserDisplaynameSetBody(name)));

            HttpResponse res = client.execute(req);

            if (res.getStatusLine().getStatusCode() == 200) {
                log.info("Successfully set user {} displayname to {}", getUserId(), name);
            } else {
                if (res.getStatusLine().getStatusCode() == 429) {
                    // TODO handle rate limited
                    log.warn("Request was rate limited", new Exception());
                }

                Charset charset = ContentType.getOrDefault(res.getEntity()).getCharset();
                String body = IOUtils.toString(res.getEntity().getContent(), charset);
                MatrixErrorInfo info = gson.fromJson(body, MatrixErrorInfo.class);
                throw new IOException("Error changing display name for " + getUserId() + " - " + info.getErrcode() + ": " + info.getError());
            }
        } catch (IOException e) {
            throw new MatrixClientRequestException(e);
        }
    }

    @Override
    public _MatrixRoom getRoom(String roomId) {
        return new MatrixHttpRoom(hs, token, mxId, roomId);
    }

    @Override
    public _MatrixUser getUser(String localpart) {
        return null;
    }

}
