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

package io.kamax.matrix.client;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import io.kamax.matrix.MatrixID;
import io.kamax.matrix.hs.MatrixHomeserver;

import org.junit.Rule;

import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

public class MatrixHttpTest {
    protected static final String TEST_TOKEN = "testToken";
    protected static final int PORT = 8098;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(PORT));

    protected MatrixClientContext createClientContext() throws URISyntaxException {
        String domain = "localhost";
        String baseUrl = "http://localhost:" + PORT;
        MatrixHomeserver hs = new MatrixHomeserver(domain, baseUrl);
        return new MatrixClientContext(hs, new MatrixID("testuser", domain), TEST_TOKEN);
    }

    protected String getAcessTokenParameter() {
        return "?access_token=" + TEST_TOKEN;
    }
}
