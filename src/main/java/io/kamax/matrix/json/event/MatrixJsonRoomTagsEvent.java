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

import io.kamax.matrix.event._RoomTagsEvent;
import io.kamax.matrix.json.GsonUtil;
import io.kamax.matrix.json.MatrixJsonObject;
import io.kamax.matrix.room.RoomTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MatrixJsonRoomTagsEvent extends MatrixJsonObject implements _RoomTagsEvent {

    private Content content;

    private String type;

    public MatrixJsonRoomTagsEvent(JsonObject obj) {
        super(obj);
        type = getString("type");
        content = new Content(getObj("content"));
    }

    @Override
    public String getType() {
        return type;
    }

    private class Content extends MatrixJsonObject {

        private List<RoomTag> tags = new ArrayList<>();

        public Content(JsonObject obj) {
            super(obj);

            String roomId = getStringOrNull("roomId");

            GsonUtil.findObj(obj, "tags").ifPresent(tagsJson -> {
                List<RoomTag> tags = tagsJson.entrySet().stream().map(it -> {
                    String completeName = it.getKey();
                    String name = completeName;
                    String namespace = "";

                    int lastDotIndex = completeName.lastIndexOf(".");

                    if (lastDotIndex >= 0 && lastDotIndex < completeName.length() - 1) {
                        namespace = completeName.substring(0, lastDotIndex);
                        name = completeName.substring(lastDotIndex + 1);
                    }

                    Double order = it.getValue().getAsJsonObject().get("order").getAsDouble();
                    return new RoomTag(namespace, name, order, roomId);
                }).collect(Collectors.toList());
                setTags(tags);
            });
        }

        public List<RoomTag> getTags() {
            return tags;
        }

        public void setTags(List<RoomTag> tags) {
            this.tags = new ArrayList<>(tags);
        }

    }

    @Override
    public List<RoomTag> getTags() {
        return Collections.unmodifiableList(content.getTags());
    }

}
