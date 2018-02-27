/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Arne Augenstein
 *
 * https://www.kamax.io/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.matrix.client.as;

import io.kamax.matrix.client.MatrixClientContext;
import io.kamax.matrix.client.MatrixClientRequestException;
import io.kamax.matrix.client.MatrixHttpTest;

import java.net.MalformedURLException;
import java.net.URL;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MatrixApplicationServiceClientTest extends MatrixHttpTest {
    private String createUserUrl = "/_matrix/client/r0/register" + tokenParameter;
    private String testUser = "testUser";

    @Override
    public void login() {
    }

    @Override
    public void logout() {
    }

    // @Test
    // FIXME re-enable
    public void createUser() throws MalformedURLException {
        stubFor(post(urlEqualTo(createUserUrl)).willReturn(aResponse().withStatus(200)));
        createClientObject().createUser(testUser);
    }

    // @Test
    // FIXME re-enable
    public void createUserErrorRateLimited() {
        stubFor(post(urlEqualTo(createUserUrl))
                .willReturn(aResponse().withStatus(429).withBody(errorRateLimitedResponse)));

        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                () -> createClientObject().createUser(testUser));
        checkErrorInfoRateLimited(e);
    }

    private MatrixApplicationServiceClient createClientObject() throws MalformedURLException {
        String domain = "localhost";
        String baseUrl = "http://localhost:" + port;

        MatrixClientContext context = new MatrixClientContext();
        context.setDomain("localhost");
        context.setHsBaseUrl(new URL(baseUrl));
        context.setUserWithLocalpart("testuser");

        return new MatrixApplicationServiceClient(context);
    }

}
