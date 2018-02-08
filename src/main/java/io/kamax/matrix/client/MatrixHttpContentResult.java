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

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

public class MatrixHttpContentResult {
    private final boolean valid;
    private final List<Header> headers;
    private final Optional<String> contentType;
    private final byte[] data;

    public MatrixHttpContentResult(CloseableHttpResponse response) throws IOException {
        HttpEntity entity = response.getEntity();
        valid = entity != null && response.getStatusLine().getStatusCode() == 200;

        if (entity != null) {
            headers = Arrays.asList(response.getAllHeaders());
            Header contentTypeHeader = entity.getContentType();
            if (contentTypeHeader != null) {
                contentType = Optional.of(contentTypeHeader.getValue());
            } else {
                contentType = Optional.empty();
            }
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            entity.writeTo(outStream);
            data = outStream.toByteArray();
        } else {
            headers = new ArrayList<>();
            contentType = Optional.empty();
            data = new byte[0];
        }
    }

    public boolean isValid() {
        return valid;
    }

    public Optional<Header> getHeader(String name) {
        for (Header header : headers) {
            if (Objects.equals(header.getName(), name)) {
                return Optional.of(header);
            }
        }
        return Optional.empty();
    }

    public Optional<String> getContentType() {
        return contentType;
    }

    public byte[] getData() {
        return data;
    }
}
