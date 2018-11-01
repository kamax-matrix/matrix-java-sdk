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

import io.kamax.matrix.event._RoomCanonicalAliasEvent;
import io.kamax.matrix.json.GsonUtil;
import io.kamax.matrix.json.MatrixJsonObject;

import org.apache.commons.lang3.StringUtils;

import java8.util.Optional;

public class MatrixJsonRoomCanonicalAliasEvent extends MatrixJsonRoomEvent implements _RoomCanonicalAliasEvent {

    private class Content extends MatrixJsonObject {

        private String alias;

        public Content(JsonObject content) {
            super(content);
            findString("alias").filter(StringUtils::isNotEmpty).ifPresent(this::setAlias);
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

    }

    private Content content;

    public MatrixJsonRoomCanonicalAliasEvent(JsonObject obj) {
        super(obj);

        if (!StringUtils.equals(Type, getType())) { // FIXME check should be done in the abstract class
            throw new IllegalArgumentException("Type is not " + Type);
        }

        this.content = new Content(GsonUtil.getObj(obj, "content"));
    }

    @Override
    public boolean hasAlias() {
        return StringUtils.isNotEmpty(content.getAlias());
    }

    @Override
    public Optional<String> getAlias() {
        return Optional.ofNullable(content.getAlias());
    }

}
