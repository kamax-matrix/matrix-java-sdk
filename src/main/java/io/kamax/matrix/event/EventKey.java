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

package io.kamax.matrix.event;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.kamax.matrix.MalformedEventException;
import io.kamax.matrix.json.GsonUtil;

import java.util.Optional;

public enum EventKey {

    AuthEvents("auth_events"),
    Content("content"),
    Depth("depth"),
    Hashes("hashes"),
    Id("event_id"),
    Origin("origin"),
    Timestamp("origin_server_ts"),
    PreviousEvents("prev_events"),
    PreviousState("prev_state"),
    RoomId("room_id"),
    Sender("sender"),
    Signatures("signatures"),
    StateKey("state_key"),
    Type("type"),
    Membership("membership"),
    Unsigned("unsigned");

    private String key;

    EventKey(String key) {
        this.key = key;
    }

    public String get() {
        return key;
    }

    public JsonObject getObj(JsonObject o) {
        return findObj(o).orElseThrow(() -> new MalformedEventException(key));
    }

    public JsonElement getElement(JsonObject o) {
        return GsonUtil.findElement(o, key).orElseThrow(() -> new MalformedEventException(key));
    }

    public Optional<JsonObject> findObj(JsonObject o) {
        return GsonUtil.findObj(o, key);
    }

    public Optional<String> findString(JsonObject o) {
        return GsonUtil.findString(o, key);
    }

    public String getString(JsonObject o) {
        return findString(o).orElseThrow(() -> new MalformedEventException(key));
    }

    public String getStringOrNull(JsonObject o) {
        return GsonUtil.getStringOrNull(o, key);
    }

    public long getLong(JsonObject o) {
        return GsonUtil.getLong(o, key);
    }

}
