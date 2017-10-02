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

import org.hamcrest.core.IsEqual;

import java.util.function.Supplier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.junit.Assert.*;

public class TestRunnerGet<R> extends TestRunner {

    public TestRunnerGet(RequestBuilder requestBuilder, ResponseBuilder responseBuilder) {
        super(requestBuilder, responseBuilder);
    }

    public void runGetTest(Supplier<R> method, R expectedResult) {
        stubFor(get(urlEqualTo(requestBuilder.getUrl())).willReturn(createResponse()));

        assertThat(method.get(), IsEqual.equalTo(expectedResult));
    }

    public void runGetTestExceptionExpected(Supplier<R> method) {
        stubFor(get(urlEqualTo(requestBuilder.getUrl())).willReturn(createResponse()));

        try {
            method.get();
        } catch (MatrixClientRequestException e) {
            /*
             * TODO refactor error handling, so that the error is returned. Afterwards, the error
             * values can be checked here by using e.getError().
             */
            // checkErrorInfo( e);
            return;
        }
        fail("In this case, an exception has to be thrown.");
    }
}
