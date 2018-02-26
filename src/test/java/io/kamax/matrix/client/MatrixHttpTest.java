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
    protected String password = "MostSecretPasswordEver";
    protected MatrixID user = new MatrixID(username, domain);

    protected String errorResponseTemplate = "{\"errcode\": \"%s\", \"error\": \"%s\"}";

    protected String errcodeForbidden = "M_FORBIDDEN";
    private String errorAccessDenied = "Access denied.";
    protected String errorAccessDeniedResponse = String.format(errorResponseTemplate, errcodeForbidden,
            errorAccessDenied);

    private String errcodeNotFound = "M_NOT_FOUND";
    private String errorNotFound = "Element not found.";
    protected String errorNotFoundResponse = String.format(errorResponseTemplate, errcodeNotFound, errorNotFound);

    private String errcodeRateLimited = "M_LIMIT_EXCEEDED";
    private String errorRateLimited = "Too many requests have been sent in a short period of time. Wait a while then try again.";
    protected String errorRateLimitedResponse = String.format(errorResponseTemplate, errcodeRateLimited,
            errorRateLimited);

    /**
     * This method logs in to a homeserver, if the appropriate config file is present. It has to be commented out in
     * Wiremock test cases.
     *
     * @throws URISyntaxException
     */
    @Before
    public void login() throws URISyntaxException {
        InputStream configFile = readConfigFile();
        if (configFile != null) {
            MatrixHomeserver homeserver = new MatrixHomeserver(domain, baseUrl);
            MatrixClientContext context = new MatrixClientContext(homeserver);
            MatrixPasswordLoginCredentials credentials = new MatrixPasswordLoginCredentials(username, password);

            client = new MatrixHttpClient(context);
            client.login(credentials);
            testToken = client.getAccessTokenOrThrow();
        }
    }

    protected InputStream readConfigFile() {
        InputStream configFile = this.getClass().getResourceAsStream("/HomeserverTest.conf");
        if (configFile != null) {
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(configFile))) {
                Map<String, String> configValues = buffer.lines()
                        .filter(line -> !line.startsWith("#") && !line.isEmpty()).collect(
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
        }
        return configFile;
    }

    /**
     * This method logs out of a homeserver, if the appropriate config file is present. It has to be commented out in
     * Wiremock test cases.
     */
    @After
    public void logout() {
        if (client != null) {
            client.logout();
            client = null;
        }
    }

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(options().port(port).usingFilesUnderDirectory(resourcePath));
    protected MatrixHttpClient client;

    protected MatrixClientContext getOrCreateClientContext() {
        if (client != null) {
            return client.getContext();
        } else {
            MatrixHomeserver hs = new MatrixHomeserver(domain, baseUrl);
            return new MatrixClientContext(hs, user, testToken);
        }
    }

    protected void checkErrorInfoAccessDenied(MatrixClientRequestException e) {
        checkErrorInfo(errcodeForbidden, errorAccessDenied, e.getError());
    }

    protected void checkErrorInfoNotFound(MatrixClientRequestException e) {
        checkErrorInfo(errcodeNotFound, errorNotFound, e.getError());
    }

    protected void checkErrorInfoRateLimited(MatrixClientRequestException e) {
        checkErrorInfo(errcodeRateLimited, errorRateLimited, e.getError());
    }

    protected void checkErrorInfo(String errcode, String error, Optional<MatrixErrorInfo> errorOptional) {
        assertTrue(errorOptional.isPresent());
        assertEquals(errcode, errorOptional.get().getErrcode());
        assertEquals(error, errorOptional.get().getError());
    }
}
