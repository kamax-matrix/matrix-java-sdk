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

package io.kamax.matrix.json.event;

import com.google.gson.JsonObject;

import io.kamax.matrix.event._RoomAliasesEvent;
import io.kamax.matrix.json.GsonUtil;
import io.kamax.matrix.json.MatrixJsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MatrixJsonRoomAliasesEvent extends MatrixJsonRoomEvent implements _RoomAliasesEvent {

    private class Content extends MatrixJsonObject {

        private List<String> aliases = new ArrayList<>();

        public Content(JsonObject obj) {
            super(obj);

            GsonUtil.findArray(obj, "aliases").ifPresent(aliasesJson -> {
                setAliases(GsonUtil.asList(aliasesJson, String.class));
            });
        }

        public List<String> getAliases() {
            return aliases;
        }

        public void setAliases(List<String> aliases) {
            this.aliases = aliases;
        }

    }

    private Content content;

    public MatrixJsonRoomAliasesEvent(JsonObject obj) {
        super(obj);
        this.content = new Content(getObj("content"));
    }

    @Override
    public List<String> getAliases() {
        return Collections.unmodifiableList(content.getAliases());
    }

}
