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

import java.io.IOException;
import java.util.*;
import java8.util.Optional;


import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MatrixHttpContentResult {

    private final boolean valid;
    private final Map<String, List<String>> headers;
    private final Optional<String> contentType;
    private final byte[] data;

    public MatrixHttpContentResult(Response response) throws IOException {
        try (ResponseBody body = response.body()) {
            boolean hasBody = Objects.nonNull(body);
            valid = hasBody && response.code() == 200;
            headers = response.headers().toMultimap();

            if (hasBody) {
                contentType = Optional.ofNullable(body.contentType()).map(MediaType::toString);
                data = body.bytes();
            } else {
                contentType = Optional.empty();
                data = new byte[0];
            }
        }
    }

    public boolean isValid() {
        return valid;
    }

    public Optional<List<String>> getHeader(String name) {
        return Optional.ofNullable(headers.get(name));
    }

    public Optional<String> getContentType() {
        return contentType;
    }

    public byte[] getData() {
        return data;
    }

}
