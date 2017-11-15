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

import com.github.tomakehurst.wiremock.WireMockServer;

import io.kamax.matrix.MatrixErrorInfo;
import io.kamax.matrix.MatrixID;
import io.kamax.matrix.client.regular.MatrixHttpClient;
import io.kamax.matrix.hs.MatrixHomeserver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ParameterResolutionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
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

    protected WireMockServer server;
    private MatrixHttpClient client;

    protected MatrixClientContext createClientContext() throws URISyntaxException {
        MatrixHomeserver hs = new MatrixHomeserver(domain, baseUrl);
        return new MatrixClientContext(hs, user, testToken);
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
        assertEquals(errorOptional.get().getErrcode(), errcode);
        assertEquals(errorOptional.get().getError(), error);
    }

    @BeforeEach
    protected void init() throws URISyntaxException {
        // TODO It's probably a good idea to move the config file to another location
        InputStream configFile = this.getClass().getResourceAsStream("/test.conf");
        if (configFile != null) {
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(configFile))) {
                Map<String, String> configValues = buffer.lines().filter(line -> !line.startsWith("#")).collect(
                        Collectors.toMap(line -> line.split("=")[0].trim(), line -> line.split("=")[1].trim()));

                String portKey = "Port";
                port = Integer.valueOf(configValues.get(portKey));
                hostname = configValues.get("Hostname");
                domain = configValues.get("Domain");
                baseUrl = "https://" + hostname + ":" + port;
                MatrixHomeserver homeserver = new MatrixHomeserver(domain, baseUrl);

                username = configValues.get("Username");
                MatrixClientContext context = new MatrixClientContext(homeserver);
                MatrixPasswordLoginCredentials credentials = new MatrixPasswordLoginCredentials(username,
                        configValues.get("Password"));

                client = new MatrixHttpClient(context);
                client.login(credentials);
                testToken = client.getAccessTokenOrThrow();
            } catch (IOException e) {
                throw new ParameterResolutionException("Config file could not be read", e);
            }
        } else {
            server = new WireMockServer(options().port(port).usingFilesUnderDirectory(resourcePath));
            server.start();
        }
    }

    @AfterEach
    protected void tearDown() {
        if (server != null) {
            server.resetAll();
            server.stop();
        } else {
            client.logout();
        }
    }

    public void configureWiremock(Consumer<WireMockServer> var1) {
        if (server != null) {
            var1.accept(server);
        }
    }
}
