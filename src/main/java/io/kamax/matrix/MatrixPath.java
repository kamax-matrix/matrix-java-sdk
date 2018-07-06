/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2018 Kamax Sàrl
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

package io.kamax.matrix;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MatrixPath {

    public static String encode(String element) {
        try {
            return URLEncoder.encode(element, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            // This would be madness if it happened, but we need to handle it still
            throw new RuntimeException(e);
        }
    }

    private static MatrixPath with(String base) {
        return new MatrixPath().add(base);
    }

    public static MatrixPath root() {
        return with("");
    }

    public static MatrixPath base() {
        return root().add("_matrix");
    }

    public static MatrixPath client() {
        return base().add("client");
    }

    public static MatrixPath clientR0() {
        return client().add("r0");
    }

    private StringBuilder path = new StringBuilder();

    /**
     * Add the raw element to this path
     * 
     * @param element
     *            The raw element to be added as is to the path, without encoding or path separator
     * @return The MatrixPath
     */
    public MatrixPath put(String element) {
        path.append(element);
        return this;
    }

    /**
     * URL encode and add a new path element
     *
     * This method handle path separators
     * 
     * @param element
     *            The element to be encoded and added.
     * @return The MatrixPath
     */
    public MatrixPath add(String element) {
        // We add a path separator if this is the first character or if the last character is not a path separator
        // already
        if (path.length() == 0 || path.lastIndexOf("/", 0) < path.length() - 1) {
            put("/");
        }
        put(encode(element));
        return this;
    }

    public String get() {
        return path.toString();
    }

    public String toString() {
        return get();
    }

    public URI toURI() {
        return URI.create(toString());
    }

}
