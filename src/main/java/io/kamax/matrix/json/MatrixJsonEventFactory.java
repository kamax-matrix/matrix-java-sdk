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

import com.google.gson.JsonObject;

import io.kamax.matrix.event.EventKey;
import io.kamax.matrix.event._MatrixEvent;
import io.kamax.matrix.json.event.*;

import java.util.Optional;

public class MatrixJsonEventFactory {

    public static _MatrixEvent get(JsonObject obj) {
        String type = obj.get("type").getAsString();

        if ("m.room.member".contentEquals(type)) {
            return new MatrixJsonRoomMembershipEvent(obj);
        } else if ("m.room.power_levels".contentEquals(type)) {
            return new MatrixJsonRoomPowerLevelsEvent(obj);
        } else if ("m.room.avatar".contentEquals(type)) {
            return new MatrixJsonRoomAvatarEvent(obj);
        } else if ("m.room.name".contentEquals(type)) {
            return new MatrixJsonRoomNameEvent(obj);
        } else if ("m.room.topic".contentEquals(type)) {
            return new MatrixJsonRoomTopicEvent(obj);
        } else if ("m.room.aliases".contentEquals(type)) {
            return new MatrixJsonRoomAliasesEvent(obj);
        } else if ("m.room.message".contentEquals(type)) {
            return new MatrixJsonRoomMessageEvent(obj);
        } else if ("m.receipt".contentEquals(type)) {
            return new MatrixJsonReadReceiptEvent(obj);
        } else {
            Optional<String> timestamp = EventKey.Timestamp.findString(obj);
            Optional<String> sender = EventKey.Sender.findString(obj);

            if (!timestamp.isPresent() || !sender.isPresent()) {
                return new MatrixJsonEphemeralEvent(obj);
            } else {

                Optional<String> rId = EventKey.RoomId.findString(obj);
                if (rId.isPresent()) {
                    return new MatrixJsonRoomEvent(obj);
                }

                return new MatrixJsonPersistentEvent(obj);
            }
        }
    }

}
