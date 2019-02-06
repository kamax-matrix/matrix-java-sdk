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

import io.kamax.matrix.event._RoomTopicEvent;
import io.kamax.matrix.json.MatrixJsonObject;

import java8.util.Optional;

public class MatrixJsonRoomTopicEvent extends MatrixJsonRoomEvent implements _RoomTopicEvent {

    public static class Content extends MatrixJsonObject {

        private String topic;

        public Content(JsonObject obj) {
            super(obj);

            setTopic(getStringOrNull("topic"));
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

    }

    private Content content;

    public MatrixJsonRoomTopicEvent(JsonObject obj) {
        super(obj);
        this.content = new Content(getObj("content"));
    }

    @Override
    public Optional<String> getTopic() {
        return Optional.ofNullable(content.getTopic());
    }

}
