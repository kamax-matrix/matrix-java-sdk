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

package io.kamax.matrix.client.regular;

import io.kamax.matrix.client.MatrixClientContext;
import io.kamax.matrix.client.MatrixClientRequestException;
import io.kamax.matrix.client.MatrixHttpTest;
import io.kamax.matrix.client.MatrixPasswordLoginCredentials;
import io.kamax.matrix.hs.MatrixHomeserver;

import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;

import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MatrixHttpClientTest extends MatrixHttpTest {
    private String loginUrl = "/_matrix/client/r0/login";
    private String password = "MostSecretPasswordEver";
    private String deviceId = "testDeviceId_892377";

    private String logoutUrl = "/_matrix/client/r0/logout" + tokenParameter;

    private String setDisplaynameUrl = String.format("/_matrix/client/r0/profile/%s/displayname",
            createClientContext().getUser().get().getId()) + tokenParameter;
    private String displayName = "display name";

    public MatrixHttpClientTest() throws URISyntaxException {
    }

    @Test
    public void loginWithDeviceId() throws URISyntaxException {
        stubFor(post(urlEqualTo(loginUrl))
                .withRequestBody(equalToJson("{\"type\": \"m.login.password\"," + //
                        "\"user\": \"" + user.getLocalPart() + "\"," + //
                        "\"password\": \"" + password + "\"," + //
                        "\"device_id\": \"" + deviceId + "\"}"))
                .willReturn(aResponse().withStatus(200)
                        .withBody("{\"user_id\": \"" + user.getId() + "\"," + //
                                "\"access_token\": \"" + testToken + "\"," + //
                                "\"home_server\": \"" + hostname + "\"," + //
                                "\"device_id\": \"" + deviceId + "\"}")));

        MatrixHomeserver hs = new MatrixHomeserver(domain, baseUrl);
        MatrixClientContext context = new MatrixClientContext(hs, deviceId);
        MatrixHttpClient client = new MatrixHttpClient(context);

        MatrixPasswordLoginCredentials credentials = new MatrixPasswordLoginCredentials(user.getLocalPart(), password);
        client.login(credentials);

        assertTrue(StringUtils.isNotBlank(client.getAccessToken().get()));
        assertTrue(StringUtils.isNotBlank(client.getDeviceId().get()));
        assertTrue(StringUtils.isNotBlank(client.getUser().get().getId()));

        /*
         * TODO The spec is not clear if the returned device id is always the same as the one you pass to the server.
         * If it can be different this assertion has to be removed.
         */
        assertEquals(deviceId, client.getDeviceId().get());
    }

    @Test
    public void loginWithDeviceIdAndLogout() throws URISyntaxException {
        stubFor(post(urlEqualTo(loginUrl))
                .withRequestBody(equalToJson("{\"type\": \"m.login.password\"," + //
                        "\"user\": \"" + user.getLocalPart() + "\"," + //
                        "\"password\": \"" + password + "\"," + //
                        "\"device_id\": \"" + deviceId + "\"}"))
                .willReturn(aResponse().withStatus(200)
                        .withBody("{\"user_id\": \"" + user.getId() + "\"," + //
                                "\"access_token\": \"" + testToken + "\"," + //
                                "\"home_server\": \"" + hostname + "\"," + //
                                "\"device_id\": \"" + deviceId + "\"}")));

        MatrixHomeserver hs = new MatrixHomeserver(domain, baseUrl);
        MatrixClientContext context = new MatrixClientContext(hs, deviceId);
        MatrixHttpClient client = new MatrixHttpClient(context);

        MatrixPasswordLoginCredentials credentials = new MatrixPasswordLoginCredentials(user.getLocalPart(), password);
        client.login(credentials);

        assertTrue(StringUtils.isNotBlank(client.getAccessToken().get()));
        assertTrue(StringUtils.isNotBlank(client.getDeviceId().get()));
        assertTrue(StringUtils.isNotBlank(client.getUser().get().getId()));

        /*
         * TODO The spec is not clear if the returned device id is always the same as the one you pass to the server.
         * If it can be different this assertion has to be removed.
         */
        assertEquals(deviceId, client.getDeviceId().get());

        stubFor(post(urlEqualTo(logoutUrl)));
        client.logout();
    }

    @Test
    public void login() throws URISyntaxException {
        stubFor(post(urlEqualTo(loginUrl))
                .withRequestBody(equalToJson("{\"type\": \"m.login.password\"," + //
                        "\"user\": \"" + user.getLocalPart() + "\"," + //
                        "\"password\": \"" + password + "\"}"))
                .willReturn(aResponse().withStatus(200)
                        .withBody("{\"user_id\": \"" + user.getId() + "\"," + //
                                "\"access_token\": \"" + testToken + "\"," + //
                                "\"home_server\": \"" + hostname + "\"," + //
                                "\"device_id\": \"" + deviceId + "\"}")));

        MatrixHomeserver hs = new MatrixHomeserver(domain, baseUrl);
        MatrixClientContext context = new MatrixClientContext(hs);
        MatrixHttpClient client = new MatrixHttpClient(context);

        MatrixPasswordLoginCredentials credentials = new MatrixPasswordLoginCredentials(user.getLocalPart(), password);
        client.login(credentials);

        assertTrue(StringUtils.isNotBlank(client.getAccessToken().get()));
        assertTrue(StringUtils.isNotBlank(client.getDeviceId().get()));
        assertTrue(StringUtils.isNotBlank(client.getUser().get().getId()));

        /*
         * TODO The spec is not clear if the returned device id is always the same as the one you pass to the server.
         * If it can be different this assertion has to be removed.
         */
        assertEquals(deviceId, client.getDeviceId().get());
    }

    @Test
    public void loginWrongPassword() throws URISyntaxException {
        stubFor(post(urlEqualTo(loginUrl))
                .withRequestBody(equalToJson("{\"type\": \"m.login.password\"," + //
                        "\"user\": \"" + user.getLocalPart() + "\"," + //
                        "\"password\": \"" + password + "\"}"))
                .willReturn(aResponse().withStatus(403).withBody(error403Response)));

        MatrixHomeserver hs = new MatrixHomeserver(domain, baseUrl);
        MatrixClientContext context = new MatrixClientContext(hs);
        MatrixHttpClient client = new MatrixHttpClient(context);

        MatrixPasswordLoginCredentials credentials = new MatrixPasswordLoginCredentials(user.getLocalPart(), password);
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                () -> client.login(credentials));
        checkErrorInfo403(e);
    }

    @Test
    public void setDisplayName() throws URISyntaxException {
        stubFor(put(urlEqualTo(setDisplaynameUrl)).willReturn(aResponse().withStatus(200)));
        createClientObject().setDisplayName(displayName);
    }

    @Test
    public void setDisplayNameErrorRateLimited() throws URISyntaxException {
        stubFor(put(urlEqualTo(setDisplaynameUrl)).willReturn(aResponse().withStatus(429).withBody(error429Response)));

        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                () -> createClientObject().setDisplayName(displayName));
        checkErrorInfo429(e);
    }

    private MatrixHttpClient createClientObject() throws URISyntaxException {
        MatrixClientContext context = createClientContext();
        return new MatrixHttpClient(context);
    }
}
