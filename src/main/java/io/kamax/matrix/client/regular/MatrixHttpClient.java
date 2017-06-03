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
import io.kamax.matrix.client.*;
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

public class MatrixHttpClient extends AMatrixHttpClient implements _MatrixClient {

    private Logger log = LoggerFactory.getLogger(MatrixHttpClient.class);

    public MatrixHttpClient(MatrixClientContext context) {
        super(context);
    }

    protected _MatrixID getMatrixId(String localpart) {
        return new MatrixID(localpart, getHomeserver().getDomain());
    }

    @Override
    public void setDisplayName(String name) {
        try {
            URI path = getClientPath("/profile/{userId}/displayname");
            HttpPut req = new HttpPut(path);
            req.setEntity(getJsonEntity(new UserDisplaynameSetBody(name)));

            HttpResponse res = client.execute(log(req));

            if (res.getStatusLine().getStatusCode() == 200) {
                log.info("Successfully set user {} displayname to {}", getUser(), name);
            } else {
                if (res.getStatusLine().getStatusCode() == 429) {
                    // TODO handle rate limited
                    log.warn("Request was rate limited", new Exception());
                }

                Charset charset = ContentType.getOrDefault(res.getEntity()).getCharset();
                String body = IOUtils.toString(res.getEntity().getContent(), charset);
                MatrixErrorInfo info = gson.fromJson(body, MatrixErrorInfo.class);
                throw new MatrixClientRequestException(info, "Error changing display name for " + getUser());
            }
        } catch (IOException e) {
            throw new MatrixClientRequestException(e);
        }
    }

    @Override
    public _MatrixRoom getRoom(String roomId) {
        return new MatrixHttpRoom(getContext(), roomId);
    }

    @Override
    public _MatrixUser getUser(_MatrixID mxId) {
        return new MatrixHttpUser(getContext(), mxId);
    }

}
