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

package io.kamax.matrix.json.event;

import com.google.gson.JsonObject;

import io.kamax.matrix.event._RoomMessageEvent;
import io.kamax.matrix.json.GsonUtil;

import java.util.Optional;

public class MatrixJsonRoomMessageEvent extends MatrixJsonRoomEvent implements _RoomMessageEvent {

    private JsonObject content;

    public MatrixJsonRoomMessageEvent(JsonObject obj) {
        super(obj);

        this.content = obj.getAsJsonObject("content");
    }

    @Override
    public String getBody() {
        return content.get("body").getAsString();
    }

    @Override
    public String getBodyType() {
        return content.has("msgtype") ? content.get("msgtype").getAsString() : null;
    }

    @Override
    public Optional<String> getFormat() {
        return GsonUtil.findString(content, "format");
    }

    @Override
    public Optional<String> getFormattedBody() {
        return GsonUtil.findString(content, "formatted_body");
    }

}
