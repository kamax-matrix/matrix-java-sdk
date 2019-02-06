/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2018 Arne Augenstein
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

import io.kamax.matrix.event._RoomHistoryVisibilityEvent;
import io.kamax.matrix.json.MatrixJsonObject;

public class MatrixJsonRoomHistoryVisibilityEvent extends MatrixJsonRoomEvent implements _RoomHistoryVisibilityEvent {

    public static class Content extends MatrixJsonObject {

        private String historyVisibility;

        public Content(JsonObject obj) {
            super(obj);
            setHistoryVisibility(getString("history_visibility"));
        }

        public String getHistoryVisibility() {
            return historyVisibility;
        }

        public void setHistoryVisibility(String historyVisibility) {
            this.historyVisibility = historyVisibility;
        }

    }

    protected Content content;

    public MatrixJsonRoomHistoryVisibilityEvent(JsonObject obj) {
        super(obj);

        content = new Content(getObj("content"));
    }

    @Override
    public String getHistoryVisibility() {
        return content.getHistoryVisibility();
    }

}
