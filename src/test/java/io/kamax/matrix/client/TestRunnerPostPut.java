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

import com.github.tomakehurst.wiremock.matching.UrlPattern;

import java.util.function.Consumer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.junit.Assert.*;

public class TestRunnerPostPut<P> extends TestRunner {

    public TestRunnerPostPut(RequestBuilder requestBuilder, ResponseBuilder responseBuilder) {
        super(requestBuilder, responseBuilder);
    }

    public void runPostTest(Consumer<P> method, P parameter, String verifyBody) {
        stubFor(post(getUrlPattern()).willReturn(createResponse()));
        method.accept(parameter);
        verify(postRequestedFor(getUrlPattern()).withRequestBody(containing(verifyBody)));
    }

    public void runPostTest(Runnable method, String verifyBody) {
        stubFor(post(getUrlPattern()).willReturn(createResponse()));
        method.run();
        verify(postRequestedFor(getUrlPattern()).withRequestBody(containing(verifyBody)));
    }

    public void runPostTest(Runnable method) {
        runPostTest(method, "");
    }

    public void runPostTestExceptionExpected(Consumer<P> method, P parameter) {
        stubFor(post(getUrlPattern()).willReturn(createResponse()));
        runErrorTest(method, parameter);
    }

    public void runPostTestExceptionExpected(Runnable method) {
        stubFor(post(getUrlPattern()).willReturn(createResponse()));
        runErrorTest(method);
    }

    public void runPutTest(Consumer<P> method, P parameter, String verifyBody) {
        stubFor(put(getUrlPattern()).willReturn(createResponse()));
        method.accept(parameter);
        verify(putRequestedFor(getUrlPattern()).withRequestBody(containing(verifyBody)));
    }

    public void runPutTest(Runnable method, String verifyBody) {

        stubFor(put(getUrlPattern()).willReturn(createResponse()));
        method.run();
        verify(putRequestedFor(getUrlPattern()).withRequestBody(containing(verifyBody)));
    }

    public void runPutTestExceptionExpected(Consumer<P> method, P parameter) {
        stubFor(put(getUrlPattern()).willReturn(createResponse()));
        runErrorTest(method, parameter);
    }

    public void runPutTestExceptionExpected(Runnable method) {
        stubFor(put(getUrlPattern()).willReturn(createResponse()));
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
        // TODO at the moment not every call throws a MatrixClientRequestException, so we cannot always evaluate the
        // error. This code can be activated after the upcoming refactoring.
        /*
         * Optional<MatrixErrorInfo> errorOptional = e.getError();
         * assertTrue(errorOptional.isPresent());
         * assertEquals(errorOptional.get().getErrcode(), responseBuilder.getErrcode());
         * assertEquals(errorOptional.get().getError(), responseBuilder.getError());
         */
    }

    private UrlPattern getUrlPattern() {
        String url = requestBuilder.getUrl();

        if (requestBuilder.getMatchingType() == RequestBuilder.MatchingType.REGEX) {
            return urlMatching(url);
        }

        return urlEqualTo(url);
    }

}
