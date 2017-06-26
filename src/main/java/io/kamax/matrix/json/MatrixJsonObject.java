/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Maxime Dor
 *
 * https://max.kamax.io/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.matrix.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MatrixJsonObject {

    private JsonObject obj;

    public MatrixJsonObject(JsonObject obj) {
        this.obj = obj;
    }

    protected String getString(String field) {
        return obj.get(field).getAsString();
    }

    protected String getStringOrNull(JsonObject obj, String field) {
        if (!obj.has(field)) {
            return null;
        }

        JsonElement el = obj.get(field);
        if (el.isJsonNull()) {
            return null;
        }

        return el.getAsString();
    }

    protected String getStringOrNull(String field) {
        return getStringOrNull(obj, field);
    }

    protected int getInt(String field) {
        return obj.get(field).getAsInt();
    }

    protected int getInt(String field, int failover) {
        if (!obj.has(field)) {
            return failover;
        }

        JsonElement el = obj.get(field);
        if (el.isJsonNull()) {
            return failover;
        }

        return el.getAsInt();
    }

    protected JsonObject getObj(String field) {
        return obj.get(field).getAsJsonObject();
    }

    public JsonObject getJson() {
        return obj;
    }

}
