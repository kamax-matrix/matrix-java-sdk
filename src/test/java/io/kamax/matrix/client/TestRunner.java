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

import org.hamcrest.core.IsEqual;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.junit.Assert.assertThat;

public class TestRunner<R, P> {
    private RequestBuilder requestBuilder;
    private ResponseBuilder responseBuilder;

    public TestRunner(RequestBuilder requestBuilder, ResponseBuilder responseBuilder) {
        this.requestBuilder = requestBuilder;
        this.responseBuilder = responseBuilder;
    }

    public void runGetTest(Supplier<R> method, R expectedResult) {
        stubFor(get(urlEqualTo(requestBuilder.getUrl())).willReturn(createResponse()));

        assertThat(method.get(), IsEqual.equalTo(expectedResult));
    }

    public void runPostTest(Consumer<P> method, P parameter, String verifyBody) {
        String url = requestBuilder.getUrl();
        stubFor(post(urlEqualTo(url)).willReturn(createResponse()));
        method.accept(parameter);
        verify(postRequestedFor(urlEqualTo(url)).withRequestBody(containing(verifyBody)));
    }

    public void runPutTest(Consumer<P> method, P parameter, String verifyBody) {
        String url = requestBuilder.getUrl();
        stubFor(put(urlEqualTo(url)).willReturn(createResponse()));
        method.accept(parameter);
        verify(putRequestedFor(urlEqualTo(url)).withRequestBody(containing(verifyBody)));
    }

    private ResponseDefinitionBuilder createResponse() {
        ResponseDefinitionBuilder response = aResponse().withStatus(responseBuilder.getStatus());
        Optional<String> body = responseBuilder.getBody();
        Optional<String> bodyFile = responseBuilder.getBodyFile();

        if (body.isPresent()) {
            response.withBody(body.get());
        } else if (bodyFile.isPresent()) {
            response.withBodyFile(bodyFile.get());
        }

        Optional<String> contentType = responseBuilder.getContentType();
        if (contentType.isPresent()) {
            response.withHeader("Content-Type", contentType.get());
        }

        Map<String, String> headers = responseBuilder.getHeaders();
        for (String header : headers.keySet()) {
            response.withHeader(header, headers.get(header));
        }
        return response;
    }
}
