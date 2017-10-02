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

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;

import io.kamax.matrix.MatrixErrorInfo;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.junit.Assert.*;

/**
 * Test runner for error cases, in which MatrixClientRequestException get thrown.
 * 
 * @param <R>
 *            Return type of the tested get-method
 * @param <P>
 *            Parameter type of the tested consume-method
 */
public class ExceptionTestRunner<R, P> {

    private final RequestBuilder requestBuilder;
    private final ResponseBuilder responseBuilder;

    public ExceptionTestRunner(RequestBuilder requestBuilder, ResponseBuilder responseBuilder) {
        this.requestBuilder = requestBuilder;
        this.responseBuilder = responseBuilder;
    }

    public ResponseDefinitionBuilder getResponse() {
        return aResponse().withStatus(responseBuilder.getStatus()).withBody(responseBuilder.getBody().get());
    }

    public void runGetTest(Supplier<R> method) {
        stubFor(get(urlEqualTo(requestBuilder.getUrl())).willReturn(getResponse()));

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

    public void runPostTest(Consumer<P> method, P parameter) {
        stubFor(post(urlEqualTo(requestBuilder.getUrl())).willReturn(getResponse()));
        runErrorTest(method, parameter);
    }

    public void runPutTest(Consumer<P> method, P parameter) {
        stubFor(put(urlEqualTo(requestBuilder.getUrl())).willReturn(getResponse()));
        runErrorTest(method, parameter);
    }

    private void runErrorTest(Consumer<P> method, P parameter) {
        try {
            method.accept(parameter);
        } catch (MatrixClientRequestException e) {
            checkErrorInfo(e);
            return;
        }
        fail("In this case, an exception has to be thrown.");
    }

    private void checkErrorInfo(MatrixClientRequestException e) {
        Optional<MatrixErrorInfo> errorOptional = e.getError();
        assertTrue(errorOptional.isPresent());
        assertEquals(errorOptional.get().getErrcode(), responseBuilder.getErrcode());
        assertEquals(errorOptional.get().getError(), responseBuilder.getError());
    }
}
