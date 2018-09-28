/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Kamax Sarl
 *
 * https://www.kamax.io/
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

import java.util.ArrayList;
import java.util.List;


import okhttp3.Request;

public class MatrixHttpRequest {
    private final Request.Builder httpRequest;
    private List<Integer> ignoredErrorCodes = new ArrayList<>();

    public MatrixHttpRequest(Request.Builder request) {
        this.httpRequest = request;
    }

    public MatrixHttpRequest addIgnoredErrorCode(int errcode) {
        ignoredErrorCodes.add(errcode);
        return this;
    }

    public Request.Builder getHttpRequest() {
        return httpRequest;
    }

    public List<Integer> getIgnoredErrorCodes() {
        return ignoredErrorCodes;
    }

}
