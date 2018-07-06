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

package io.kamax.matrix.json;

public class RoomMessageFormattedTextPutBody extends RoomMessageTextPutBody {

    private String formatted_body;
    private String format = "org.matrix.custom.html";

    public RoomMessageFormattedTextPutBody(String body, String formattedBody) {
        super(body);

        this.formatted_body = formattedBody;
    }

    public RoomMessageFormattedTextPutBody(String msgtype, String body, String formattedBody) {
        super(msgtype, body);

        this.formatted_body = formattedBody;
    }

    public String getFormatted_body() {
        return formatted_body;
    }

    public String getFormat() {
        return format;
    }

}
