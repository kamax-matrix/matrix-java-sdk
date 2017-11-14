/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Arne Augenstein
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

import io.kamax.matrix.client.*;
import io.kamax.matrix.hs.MatrixHomeserver;

import org.junit.Test;

import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MatrixApplicationServiceClientTest extends MatrixHttpTest {
    private String createUserUrl = "/_matrix/client/r0/register" + tokenParameter;
    private String testUser = "testUser";

    @Test
    public void createUser() throws URISyntaxException {
        stubFor(post(urlEqualTo(createUserUrl)).willReturn(aResponse().withStatus(200)));
        createClientObject().createUser(testUser);
    }

    @Test
    public void createUserErrorRateLimited() throws URISyntaxException {
        stubFor(post(urlEqualTo(createUserUrl)).willReturn(aResponse().withStatus(429).withBody(error429Response)));

        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                () -> createClientObject().createUser(testUser));
        checkErrorInfo429(e);
    }

    private MatrixApplicationServiceClient createClientObject() throws URISyntaxException {
        String domain = "localhost";
        String baseUrl = "http://localhost:" + port;
        MatrixHomeserver hs = new MatrixHomeserver(domain, baseUrl);
        return new MatrixApplicationServiceClient(hs, testToken, "testuser");
    }

}
