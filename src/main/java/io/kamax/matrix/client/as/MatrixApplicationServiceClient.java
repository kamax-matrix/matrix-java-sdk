/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Kamax Sarl
 *
 * https://www.kamax.io/
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

import io.kamax.matrix.client.MatrixClientContext;
import io.kamax.matrix.client.MatrixClientDefaults;
import io.kamax.matrix.client._MatrixClient;
import io.kamax.matrix.client.regular.MatrixHttpClient;
import io.kamax.matrix.json.VirtualUserRegistrationBody;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;


import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MatrixApplicationServiceClient extends MatrixHttpClient implements _MatrixApplicationServiceClient {

    private Logger log = LoggerFactory.getLogger(MatrixApplicationServiceClient.class);

    public MatrixApplicationServiceClient(String domain) {
        super(domain);
    }

    public MatrixApplicationServiceClient(URL hsBaseUrl) {
        super(hsBaseUrl);
    }

    public MatrixApplicationServiceClient(MatrixClientContext context) {
        super(context);
    }

    public MatrixApplicationServiceClient(MatrixClientContext context, OkHttpClient.Builder client) {
        super(context, client);
    }

    public MatrixApplicationServiceClient(MatrixClientContext context, OkHttpClient.Builder client,
            MatrixClientDefaults defaults) {
        super(context, client, defaults);
    }

    public MatrixApplicationServiceClient(MatrixClientContext context, OkHttpClient client) {
        super(context, client);
    }

    private MatrixHttpClient createClient(String localpart) {
        MatrixClientContext context = new MatrixClientContext(getContext()).setUserWithLocalpart(localpart)
                .setVirtual(true);
        return new MatrixHttpClient(context);
    }

    @Override
    public _MatrixClient createUser(String localpart) {
        log.debug("Creating new user {}", localpart);
        URL path = getClientPath("register");
        executeAuthenticated(
                new Request.Builder().post(getJsonBody(new VirtualUserRegistrationBody(localpart))).url(path));
        return createClient(localpart);
    }

    @Override
    public _MatrixClient getUser(String localpart) {
        return createClient(localpart);
    }

}
