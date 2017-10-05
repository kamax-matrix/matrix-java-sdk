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

import io.kamax.matrix.MatrixErrorInfo;
import io.kamax.matrix.MatrixID;
import io.kamax.matrix.hs.MatrixHomeserver;

import org.junit.Rule;

import java.net.URISyntaxException;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static junit.framework.TestCase.assertEquals;

import static org.junit.Assert.assertTrue;

public class MatrixHttpTest {
    protected String testToken = "testToken";
    protected String tokenParameter = "?access_token=" + testToken;
    protected int port = 8098;
    protected String resourcePath = "src/test/resources/wiremock";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(port).usingFilesUnderDirectory(resourcePath));

    protected MatrixClientContext createClientContext() throws URISyntaxException {
        String domain = "localhost";
        String baseUrl = "http://localhost:" + port;
        MatrixHomeserver hs = new MatrixHomeserver(domain, baseUrl);
        return new MatrixClientContext(hs, new MatrixID("testuser", domain), testToken);
    }

    protected String getAcessTokenParameter() {
        return "?access_token=" + testToken;
    }

    protected void checkErrorInfo(MatrixClientRequestException e, String expectedErrcode, String expectedError) {
        // TODO at the moment not every call throws a MatrixClientRequestException, so we cannot always evaluate the
        // error. This code can be activated after the upcoming refactoring.

        Optional<MatrixErrorInfo> errorOptional = e.getError();
        assertTrue(errorOptional.isPresent());
        assertEquals(errorOptional.get().getErrcode(), expectedErrcode);
        assertEquals(errorOptional.get().getError(), expectedError);

    }

}
