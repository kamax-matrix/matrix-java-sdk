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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.kamax.matrix.event._TagsEvent;
import io.kamax.matrix.json.GsonUtil;
import io.kamax.matrix.json.MatrixJsonObject;
import io.kamax.matrix.room.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MatrixJsonRoomTagsEvent extends MatrixJsonObject implements _TagsEvent {

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

        private List<Tag> tags = new ArrayList<>();

        public Content(JsonObject obj) {
            super(obj);

            GsonUtil.findObj(obj, "tags").ifPresent(tagsJson -> {
                List<Tag> tags = tagsJson.entrySet().stream().map(it -> {
                    String completeName = it.getKey();
                    String name = completeName;
                    String namespace = "";

                    int lastDotIndex = completeName.lastIndexOf(".");

                    if (lastDotIndex >= 0 && lastDotIndex < completeName.length() - 1) {
                        namespace = completeName.substring(0, lastDotIndex);
                        name = completeName.substring(lastDotIndex + 1);
                    }

                    JsonElement jsonOrder = it.getValue().getAsJsonObject().get("order");

                    if (jsonOrder != null) {
                        return new Tag(namespace, name, jsonOrder.getAsDouble());
                    }
                    return new Tag(namespace, name, null);
                }).collect(Collectors.toList());
                setTags(tags);
            });
        }

        public List<Tag> getTags() {
            return tags;
        }

        public void setTags(List<Tag> tags) {
            this.tags = new ArrayList<>(tags);
        }

    }

    @Override
    public List<Tag> getTags() {
        return Collections.unmodifiableList(content.getTags());
    }

}
