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

import com.google.gson.JsonObject;

import io.kamax.matrix.event._MatrixEvent;
import io.kamax.matrix.json.event.MatrixJsonEvent;
import io.kamax.matrix.json.event.MatrixJsonRoomMembershipEvent;
import io.kamax.matrix.json.event.MatrixJsonRoomMessageEvent;

public class MatrixJsonEventFactory {

    public static _MatrixEvent get(JsonObject obj) {
        String type = obj.get("type").getAsString();

        if ("m.room.member".contentEquals(type)) {
            return new MatrixJsonRoomMembershipEvent(obj);
        } else if ("m.room.message".contentEquals(type)) {
            return new MatrixJsonRoomMessageEvent(obj);
        } else {
            return new MatrixJsonEvent(obj);
        }
    }

}
