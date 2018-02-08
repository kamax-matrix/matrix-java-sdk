/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Maxime Dor
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

package io.kamax.matrix.json;

import org.junit.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MatrixJsonTest {

    @Test
    public void encodeCanonicalEmptyObject() throws IOException {
        String s = MatrixJson.encodeCanonical("{}");
        assertTrue("{}".equals(s), s);
    }

    @Test
    public void encodeCanonicalObjectCanonical() throws IOException {
        String s = MatrixJson.encodeCanonical("{\"a\":\"1\",\"b\":\"2\"}\n");
        assertTrue("{\"a\":\"1\",\"b\":\"2\"}".equals(s), s);
    }

    @Test
    public void encodeCanonicalObjectNotCanonical() throws IOException {
        String s = MatrixJson.encodeCanonical("{\"b\":\"2\",\"a\":\"1\"}");
        assertTrue("{\"a\":\"1\",\"b\":\"2\"}".equals(s), s);

    }

    @Test
    public void encodeCanonicalJsonWithLineFeeds() {
        String s = MatrixJson.encodeCanonical("{\n" + "    \"one\": 1,\n" + "    \"two\": \"Two\"\n" + "}");
        assertTrue("{\"one\":1,\"two\":\"Two\"}".equals(s), s);
    }

    @Test
    public void encodeCanonicalComplicatedObject() {
        String s = MatrixJson.encodeCanonical("{\n" + "  \"auth\": {\n" + "    \"success\": true,\n"
                + "    \"mxid\": \"@john.doe:example.com\",\n" + "    \"profile\": {\n"
                + "      \"display_name\": \"John Doe\",\n" + "      \"three_pids\": [\n" + "        {\n"
                + "          \"medium\": \"email\",\n" + "          \"address\": \"john.doe@example.org\"\n"
                + "        },\n" + "        {\n" + "          \"medium\": \"msisdn\",\n"
                + "          \"address\": \"123456789\"\n" + "        }\n" + "      ]\n" + "    }\n" + "  }\n" + "}\n");

        assertTrue(("{\"auth\":{\"mxid\":\"@john.doe:example.com\",\"profile\":{\"display_name\":\"John Doe\","
                + "\"three_pids\":[{\"address\":\"john.doe@example.org\",\"medium\":\"email\"},"
                + "{\"address\":\"123456789\",\"medium\":\"msisdn\"}]},\"success\":true}}").equals(s), s);
    }

    @Test
    public void encodeCanonicalSimpleUtf8Canonical() {
        String s = MatrixJson.encodeCanonical("{\"a\":\"日本語\"}");
        assertTrue("{\"a\":\"日本語\"}".equals(s), s);
    }

    @Test
    public void encodeCanonicalSimpleUtf8NotCanonical() {
        String s = MatrixJson.encodeCanonical("{\"本\":2,\"日\":1}");
        assertTrue("{\"日\":1,\"本\":2}".equals(s), s);
    }

    @Test
    public void encodeCanonicalUtf8Escaped() {
        String s = MatrixJson.encodeCanonical("{\"a\":\"\\u65E5\"}");
        assertTrue("{\"a\":\"日\"}".equals(s), s);
    }

    @Test
    public void encodeCanonicalNull() {
        String s = MatrixJson.encodeCanonical("{\"a\":null}");
        assertTrue("{\"a\":null}".equals(s), s);
    }

}
