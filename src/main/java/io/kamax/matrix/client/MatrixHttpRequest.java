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

import org.apache.http.client.methods.HttpRequestBase;

import java.util.ArrayList;
import java.util.List;

public class MatrixHttpRequest {
    private final HttpRequestBase httpRequest;
    private List<Integer> ignoredErrorCodes = new ArrayList<>();

    public MatrixHttpRequest(HttpRequestBase request) {
        this.httpRequest = request;
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
