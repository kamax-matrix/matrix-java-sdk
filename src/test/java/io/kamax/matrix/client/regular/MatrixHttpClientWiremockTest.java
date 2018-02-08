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

import org.junit.Test;

import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class MatrixHttpClientWiremockTest extends AMatrixHttpClientTest {
    private String setDisplaynameUrl = String.format("/_matrix/client/r0/profile/%s/displayname",
            getOrCreateClientContext().getUser().get().getId()) + tokenParameter;

    public MatrixHttpClientWiremockTest() throws URISyntaxException {
    }

    @Override
    public void login() throws URISyntaxException {
    }

    @Override
    public void logout() {
    }

    @Test
    public void setDisplayName() throws URISyntaxException {
        stubFor(put(urlEqualTo(setDisplaynameUrl)).willReturn(aResponse().withStatus(200)));
        super.setDisplayName();
    }

    @Test
    public void setDisplayNameErrorRateLimited() throws URISyntaxException {
        stubFor(put(urlEqualTo(setDisplaynameUrl))
                .willReturn(aResponse().withStatus(429).withBody(errorRateLimitedResponse)));
        super.setDisplayNameErrorRateLimited();
    }
}
