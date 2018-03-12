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

package io.kamax.matrix.client.regular;

import io.kamax.matrix.client.MatrixClientContext;
import io.kamax.matrix.client.MatrixClientRequestException;
import io.kamax.matrix.client.MatrixHttpTest;
import io.kamax.matrix.client.MatrixPasswordLoginCredentials;
import io.kamax.matrix.hs.MatrixHomeserver;

import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;

import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AMatrixHttpClientLoginTest extends MatrixHttpTest {
    protected String wrongPassword = "wrongPassword";

    protected String errorInvalidPassword = "Invalid password";
    protected String errorInvalidPasswordResponse = String.format(errorResponseTemplate, errcodeForbidden,
            errorInvalidPassword);

    @Override
    public void logout() {
    }

    @Override
    public void login() {
    }

    @Test
    public void loginAndLogout() throws URISyntaxException {
        MatrixHomeserver hs = new MatrixHomeserver(domain, baseUrl);
        MatrixClientContext context = new MatrixClientContext(hs);
        MatrixHttpClient client = new MatrixHttpClient(context);

        MatrixPasswordLoginCredentials credentials = new MatrixPasswordLoginCredentials(user.getLocalPart(), password);
        client.login(credentials);

        assertTrue(StringUtils.isNotBlank(client.getAccessToken().get()));
        assertTrue(StringUtils.isNotBlank(client.getDeviceId().get()));
        assertTrue(StringUtils.isNotBlank(client.getUser().get().getId()));

        client.logout();
    }

    @Test
    public void loginWithDeviceIdAndLogout() throws URISyntaxException {
        MatrixHomeserver hs = new MatrixHomeserver(domain, baseUrl);
        MatrixClientContext context = new MatrixClientContext(hs);
        MatrixHttpClient client = new MatrixHttpClient(context);

        MatrixPasswordLoginCredentials credentials = new MatrixPasswordLoginCredentials(user.getLocalPart(), password);
        client.login(credentials);

        String deviceId = client.getDeviceId().get();

        client.logout();

        context = new MatrixClientContext(hs).setDeviceId(deviceId);
        client = new MatrixHttpClient(context);
        client.login(credentials);

        assertTrue(StringUtils.isNotBlank(client.getAccessToken().get()));
        assertTrue(StringUtils.isNotBlank(client.getDeviceId().get()));
        assertTrue(StringUtils.isNotBlank(client.getUser().get().getId()));
        assertEquals(deviceId, client.getDeviceId().get());

        client.logout();
    }

    @Test
    public void loginWrongPassword() throws URISyntaxException {
        MatrixHomeserver hs = new MatrixHomeserver(domain, baseUrl);
        MatrixClientContext context = new MatrixClientContext(hs);
        MatrixHttpClient client = new MatrixHttpClient(context);

        MatrixPasswordLoginCredentials credentials = new MatrixPasswordLoginCredentials(user.getLocalPart(),
                wrongPassword);
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                () -> client.login(credentials));
        checkErrorInfo(errcodeForbidden, "Invalid password", e.getError());
    }

}
