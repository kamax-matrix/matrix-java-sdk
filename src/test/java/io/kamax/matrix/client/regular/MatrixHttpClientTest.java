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

import io.kamax.matrix.client.*;

import org.junit.Test;

import java.net.URISyntaxException;

public class MatrixHttpClientTest extends MatrixHttpTest {

    @Test
    public void setDisplayname() throws URISyntaxException {
        String url = createSetDisplaynameUrl();
        ResponseBuilder responseBuilder = new ResponseBuilder(200);
        String displayname = "new name";
        String verifyBody = ("`displayname`:`" + displayname + "`").replace('`', '"');

        new TestRunnerPostPut<String>(new RequestBuilder(url), responseBuilder)
                .runPutTest(createClientObject()::setDisplayName, displayname, verifyBody);
    }

    @Test
    public void setDisplaynameError429() throws URISyntaxException {
        TestRunnerPostPut<String> runner = new TestRunnerPostPut<>(new RequestBuilder(createSetDisplaynameUrl()),
                new ResponseBuilder(429));
        runner.runPutTestExceptionExpected(createClientObject()::setDisplayName, "new name");
    }

    private String createSetDisplaynameUrl() throws URISyntaxException {
        return "/_matrix/client/r0/profile/" + createClientContext().getUser().getId() + "/displayname"
                + getAcessTokenParameter();
    }

    private MatrixHttpClient createClientObject() throws URISyntaxException {
        MatrixClientContext context = createClientContext();
        return new MatrixHttpClient(context);
    }
}
