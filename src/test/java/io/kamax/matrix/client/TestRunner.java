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

import java.util.Map;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

public class TestRunner {
    protected RequestBuilder requestBuilder;
    protected ResponseBuilder responseBuilder;

    public TestRunner(RequestBuilder requestBuilder, ResponseBuilder responseBuilder) {
        this.requestBuilder = requestBuilder;
        this.responseBuilder = responseBuilder;
    }

    protected ResponseDefinitionBuilder createResponse() {
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
