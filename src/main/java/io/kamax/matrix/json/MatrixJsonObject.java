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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.Optional;

public class MatrixJsonObject {

    private JsonObject obj;

    public MatrixJsonObject(JsonObject obj) {
        this.obj = obj;
    }

    protected Optional<String> findString(String field) {
        return GsonUtil.findString(obj, field);
    }

    protected String getString(String field) {
        return GsonUtil.getStringOrNull(obj, field);
    }

    protected String getStringOrNull(JsonObject obj, String field) {
        return GsonUtil.findString(obj, field).orElse(null);
    }

    protected String getStringOrNull(String field) {
        return getStringOrNull(obj, field);
    }

    protected int getInt(String field) {
        return GsonUtil.getPrimitive(obj, field).getAsInt();
    }

    protected int getInt(String field, int failover) {
        return GsonUtil.findPrimitive(obj, field).map(JsonPrimitive::getAsInt).orElse(failover);
    }

    protected long getLong(String field) {
        return GsonUtil.getLong(obj, field);
    }

    protected JsonObject asObj(JsonElement el) {
        if (!el.isJsonObject()) {
            throw new IllegalArgumentException("Not a JSON object");
        }

        return el.getAsJsonObject();
    }

    protected JsonObject getObj(String field) {
        return GsonUtil.getObj(obj, field);
    }

    protected Optional<JsonObject> findObj(String field) {
        return GsonUtil.findObj(obj, field);
    }

    protected Optional<JsonArray> findArray(String field) {
        return GsonUtil.findArray(obj, field);
    }

    public JsonObject getJson() {
        return obj;
    }

}
