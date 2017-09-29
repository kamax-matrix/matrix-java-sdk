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

package io.kamax.matrix.client.as;

import io.kamax.matrix.client.*;
import io.kamax.matrix.hs.MatrixHomeserver;

import org.junit.Test;

import java.net.URISyntaxException;
import java.util.function.Consumer;

public class MatrixApplicationServiceClientTest extends MatrixHttpTest {

    @Test
    public void createUser() throws URISyntaxException {
        MatrixHttpPostTesterSuccessful tester = new MatrixHttpPostTesterSuccessful(createClientObject()::createUser,
                "testuser", createUserUrl(), ("{}"));
        tester.runTest();
    }

    @Test
    public void createUserError429() throws URISyntaxException {
        error429(createUserUrl(), createClientObject()::createUser, "testuser");
    }

    private String createUserUrl() throws URISyntaxException {
        return "/_matrix/client/r0/register" + getAcessTokenParameter();
    }

    private MatrixApplicationServiceClient createClientObject() throws URISyntaxException {
        String domain = "localhost";
        String baseUrl = "http://localhost:" + PORT;
        MatrixHomeserver hs = new MatrixHomeserver(domain, baseUrl);
        return new MatrixApplicationServiceClient(hs, TEST_TOKEN, "testuser");
    }

    private void error429(String url, Consumer<String> methodToTest, String valueToConsume) throws URISyntaxException {
        String errcode = "M_LIMIT_EXCEEDED";
        String error = "Too many requests have been sent in a short period of time. " + "Wait a while then try again.";

        new MatrixHttpPostTesterUnsuccessful(methodToTest, valueToConsume, url, 429, errcode, error).runTest();
    }
}
