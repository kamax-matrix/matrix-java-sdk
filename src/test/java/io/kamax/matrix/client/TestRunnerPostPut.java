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

import io.kamax.matrix.MatrixErrorInfo;

import java.util.Optional;
import java.util.function.Consumer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.junit.Assert.*;

public class TestRunnerPostPut<P> extends TestRunner {

    public TestRunnerPostPut(RequestBuilder requestBuilder, ResponseBuilder responseBuilder) {
        super(requestBuilder, responseBuilder);
    }

    public void runPostTest(Consumer<P> method, P parameter, String verifyBody) {
        String url = requestBuilder.getUrl();
        stubFor(post(urlEqualTo(url)).willReturn(createResponse()));
        method.accept(parameter);
        verify(postRequestedFor(urlEqualTo(url)).withRequestBody(containing(verifyBody)));
    }

    public void runPostTest(Runnable method, String verifyBody) {
        String url = requestBuilder.getUrl();
        stubFor(post(urlEqualTo(url)).willReturn(createResponse()));
        method.run();
        verify(postRequestedFor(urlEqualTo(url)).withRequestBody(containing(verifyBody)));
    }

    public void runPostTest(Runnable method) {
        runPostTest(method, "");
    }

    public void runPostTestExceptionExpected(Consumer<P> method, P parameter) {
        stubFor(post(urlEqualTo(requestBuilder.getUrl())).willReturn(createResponse()));
        runErrorTest(method, parameter);
    }

    public void runPostTestExceptionExpected(Runnable method) {
        stubFor(post(urlEqualTo(requestBuilder.getUrl())).willReturn(createResponse()));
        runErrorTest(method);
    }

    public void runPutTest(Consumer<P> method, P parameter, String verifyBody) {
        String url = requestBuilder.getUrl();
        stubFor(put(urlEqualTo(url)).willReturn(createResponse()));
        method.accept(parameter);
        verify(putRequestedFor(urlEqualTo(url)).withRequestBody(containing(verifyBody)));
    }

    public void runPutTest(Runnable method, String verifyBody) {
        String url = requestBuilder.getUrl();
        stubFor(put(urlEqualTo(url)).willReturn(createResponse()));
        method.run();
        verify(putRequestedFor(urlEqualTo(url)).withRequestBody(containing(verifyBody)));
    }

    public void runPutTestExceptionExpected(Consumer<P> method, P parameter) {
        stubFor(put(urlEqualTo(requestBuilder.getUrl())).willReturn(createResponse()));
        runErrorTest(method, parameter);
    }

    public void runPutTestExceptionExpected(Runnable method) {
        stubFor(put(urlEqualTo(requestBuilder.getUrl())).willReturn(createResponse()));
        runErrorTest(method);
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

    private void runErrorTest(Runnable method) {
        try {
            method.run();
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
