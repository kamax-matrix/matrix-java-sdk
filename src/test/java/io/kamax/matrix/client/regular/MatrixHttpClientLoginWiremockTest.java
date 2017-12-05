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

import org.junit.Test;

import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class MatrixHttpClientLoginWiremockTest extends AMatrixHttpClientLoginTest {

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

        super.loginWithDeviceId();
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

        super.loginWithDeviceIdAndLogout();
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

        super.login();
    }

    @Test
    public void loginWrongPassword() throws URISyntaxException {
        stubFor(post(urlEqualTo(loginUrl))
                .withRequestBody(equalToJson("{\"type\": \"m.login.password\"," + //
                        "\"user\": \"" + user.getLocalPart() + "\"," + //
                        "\"password\": \"" + password + "\"}"))
                .willReturn(aResponse().withStatus(403).withBody(errorAccessDeniedResponse)));

        super.loginWrongPassword();
    }

}
