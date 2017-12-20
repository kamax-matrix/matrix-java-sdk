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

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.ArrayList;
import java.util.List;

public class MatrixHttpRequest {
    private final HttpRequestBase httpRequest;
    private List<Integer> ignoredErrorCodes = new ArrayList<>();

    public MatrixHttpRequest(HttpRequestBase request) {
        this.httpRequest = request;
        RequestConfig.Builder configBuilder = request.getConfig().custom();
        if (request.getConfig() != null) {
            configBuilder = RequestConfig.copy(request.getConfig());
        }
        configBuilder.setConnectionRequestTimeout(2000);
        configBuilder.setConnectTimeout(2000);
        configBuilder.setSocketTimeout(2000);
        httpRequest.setConfig(configBuilder.build());
    }

    public MatrixHttpRequest addIgnoredErrorCode(int errcode) {
        ignoredErrorCodes.add(errcode);
        return this;
    }

    public HttpRequestBase getHttpRequest() {
        return httpRequest;
    }

    public List<Integer> getIgnoredErrorCodes() {
        return ignoredErrorCodes;
    }

}
