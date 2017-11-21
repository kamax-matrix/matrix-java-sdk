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
import io.kamax.matrix.client.regular.MatrixHttpClient;
import io.kamax.matrix.hs.MatrixHomeserver;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static junit.framework.TestCase.assertEquals;

import static org.junit.Assert.assertTrue;

public class MatrixHttpTest {
    protected String testToken = "testToken";
    protected String tokenParameter = "?access_token=" + testToken;
    protected int port = 8098;
    protected String resourcePath = "src/test/resources/wiremock";

    protected String domain = "localhost";
    protected String hostname = "localhost";
    protected String baseUrl = "http://" + hostname + ":" + port;
    protected String username = "testuser";
    protected String password = "";
    protected MatrixID user = new MatrixID(username, domain);

    private String errorResponseTemplate = "{\"errcode\": \"%s\", \"error\": \"%s\"}";

    private String errcode403 = "M_FORBIDDEN";
    private String error403 = "Access denied.";
    protected String error403Response = String.format(errorResponseTemplate, errcode403, error403);

    private String errcode404 = "M_NOT_FOUND";
    private String error404 = "Element not found.";
    protected String error404Response = String.format(errorResponseTemplate, errcode404, error404);

    private String errcode429 = "M_LIMIT_EXCEEDED";
    private String error429 = "Too many requests have been sent in a short period of time. Wait a while then try again.";
    protected String error429Response = String.format(errorResponseTemplate, errcode429, error429);

    /**
     * This method logs in to a homeserver, if the appropriate config file is present. It has to be commented out in
     * Wiremock test cases.
     * 
     * @throws URISyntaxException
     */
    @Before
    public void login() throws URISyntaxException {
        InputStream configFile = this.getClass().getResourceAsStream("/test.conf");
        if (configFile != null) {
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(configFile))) {
                Map<String, String> configValues = buffer.lines().filter(line -> !line.startsWith("#")).collect(
                        Collectors.toMap(line -> line.split("=")[0].trim(), line -> line.split("=")[1].trim()));

                port = Integer.valueOf(configValues.get("Port"));
                hostname = configValues.get("Hostname");
                domain = configValues.get("Domain");
                baseUrl = "https://" + hostname + ":" + port;
                username = configValues.get("Username");
                password = configValues.get("Password");
                user = new MatrixID(username, domain);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
            try {
                MatrixHomeserver homeserver = new MatrixHomeserver(domain, baseUrl);
                MatrixClientContext context = new MatrixClientContext(homeserver);
                MatrixPasswordLoginCredentials credentials = new MatrixPasswordLoginCredentials(username, password);

                client = new MatrixHttpClient(context);
                client.login(credentials);
                testToken = client.getAccessTokenOrThrow();
            } catch (URISyntaxException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * This method logs out of a homeserver, if the appropriate config file is present. It has to be commented out in
     * Wiremock test cases.
     */
    @After
    public void logout() {
        client.logout();
        client = null;
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(port).usingFilesUnderDirectory(resourcePath));
    private MatrixHttpClient client;

    protected MatrixClientContext getOrCreateClientContext() throws URISyntaxException {
        if (client != null) {
            return client.getContext();
        } else {
            MatrixHomeserver hs = new MatrixHomeserver(domain, baseUrl);
            return new MatrixClientContext(hs, user, testToken);
        }
    }

    protected void checkErrorInfo403(MatrixClientRequestException e) {
        checkErrorInfo(errcode403, error403, e.getError());
    }

    protected void checkErrorInfo404(MatrixClientRequestException e) {
        checkErrorInfo(errcode404, error404, e.getError());
    }

    protected void checkErrorInfo429(MatrixClientRequestException e) {
        checkErrorInfo(errcode429, error429, e.getError());
    }

    private void checkErrorInfo(String errcode, String error, Optional<MatrixErrorInfo> errorOptional) {
        assertTrue(errorOptional.isPresent());
        assertEquals(errcode, errorOptional.get().getErrcode());
        assertEquals(error, errorOptional.get().getError());
    }
}
