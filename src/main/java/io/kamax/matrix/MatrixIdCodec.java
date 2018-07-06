/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2018 Kamax Sarl
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

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.kamax.matrix.MatrixID.ALLOWED_CHARS;

public class MatrixIdCodec {

    public static final String DELIMITER = "=";
    public static final String ENCODE_REGEX = "[^" + ALLOWED_CHARS + "]+";
    public static final Pattern ENCODE_PATTERN = Pattern.compile(ENCODE_REGEX);
    public static final Pattern DECODE_PATTERN = Pattern.compile("(=[0-9a-f]{2})+");

    private MatrixIdCodec() {
        // not for public consumption
    }

    public static String encode(String decoded) {
        decoded = decoded.toLowerCase();

        StringBuilder builder = new StringBuilder();
        for (Character c : decoded.toCharArray()) {
            String s = c.toString();
            Matcher lp = ENCODE_PATTERN.matcher(s);
            if (!lp.find()) {
                builder.append(s);
            } else {
                for (byte b : c.toString().getBytes(StandardCharsets.UTF_8)) {
                    builder.append(DELIMITER);
                    builder.append(Hex.encodeHexString(new byte[] { b }));
                }
            }
        }

        return builder.toString();
    }

    public static String decode(String encoded) {
        StringBuilder builder = new StringBuilder();

        Matcher m = DECODE_PATTERN.matcher(encoded);
        int prevEnd = 0;
        while (m.find()) {
            try {
                int start = m.start();
                int end = m.end();
                String sub = encoded.substring(start, end).replaceAll(DELIMITER, "");
                String decoded = new String(Hex.decodeHex(sub.toCharArray()), StandardCharsets.UTF_8);
                builder.append(encoded, prevEnd, start);
                builder.append(decoded);
                prevEnd = end - 1;
            } catch (DecoderException e) {
                e.printStackTrace();
            }
        }
        prevEnd++;
        if (prevEnd < encoded.length()) {
            builder.append(encoded, prevEnd, encoded.length());
        }

        if (builder.length() == 0) {
            return encoded;
        } else {
            return builder.toString();
        }
    }

}
