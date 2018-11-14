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

package io.kamax.matrix.client.regular;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.kamax.matrix.client._GlobalPushRulesSet;
import io.kamax.matrix.json.GsonUtil;

import java.util.List;

public class GlobalPushRulesSet implements _GlobalPushRulesSet {

    private JsonObject data;

    public GlobalPushRulesSet(JsonObject data) {
        this.data = data;
    }

    private List<JsonObject> getForKey(String key) {
        return GsonUtil.asList(GsonUtil.findArray(data, key).orElseGet(JsonArray::new), JsonObject.class);
    }

    @Override
    public List<JsonObject> getContent() {
        return getForKey("content");
    }

    @Override
    public List<JsonObject> getOverride() {
        return getForKey("override");
    }

    @Override
    public List<JsonObject> getRoom() {
        return getForKey("room");
    }

    @Override
    public List<JsonObject> getSender() {
        return getForKey("sender");
    }

    @Override
    public List<JsonObject> getUnderride() {
        return getForKey("underride");
    }

}
