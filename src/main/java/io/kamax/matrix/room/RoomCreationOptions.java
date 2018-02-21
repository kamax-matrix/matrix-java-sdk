/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2018 Maxime Dor
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

package io.kamax.matrix.room;

import com.google.gson.JsonElement;

import io.kamax.matrix._MatrixID;

import java.util.*;

public class RoomCreationOptions implements _RoomCreationOptions {

    public static class Builder {

        private RoomCreationOptions obj;

        public Builder() {
            this.obj = new RoomCreationOptions();
        }

        public Builder setVisibility(String visibility) {
            obj.visibility = visibility;
            return this;
        }

        public Builder setVisibility(RoomDirectoryVisibility visibility) {
            return setVisibility(visibility.get());
        }

        public Builder setAliasName(String aliasName) {
            obj.aliasName = aliasName;
            return this;
        }

        public Builder setName(String name) {
            obj.name = name;
            return this;
        }

        public Builder setTopic(String topic) {
            obj.topic = topic;
            return this;
        }

        public Builder setInvites(Set<_MatrixID> invites) {
            obj.invites = Collections.unmodifiableSet(new HashSet<>(invites));
            return this;
        }

        public Builder setCreationContent(Map<String, JsonElement> creationContent) {
            obj.creationContent = Collections.unmodifiableMap(new HashMap<>(creationContent));
            return this;
        }

        public Builder setPreset(String preset) {
            obj.preset = preset;
            return this;
        }

        public Builder setPreset(RoomCreationPreset preset) {
            return setPreset(preset.get());
        }

        public Builder setDirect(boolean isDirect) {
            obj.isDirect = isDirect;
            return this;
        }

        public Builder setGuestCanJoin(boolean guestCanJoin) {
            obj.guestCanJoin = guestCanJoin;
            return this;
        }

        public RoomCreationOptions get() {
            return obj;
        }
    }

    public static Builder build() {
        return new Builder();
    }

    public static RoomCreationOptions none() {
        return build().get();
    }

    private String visibility;
    private String aliasName;
    private String name;
    private String topic;
    private Set<_MatrixID> invites = new HashSet<>();
    private Map<String, JsonElement> creationContent = new HashMap<>();
    private String preset;
    private Boolean isDirect;
    private Boolean guestCanJoin;

    @Override
    public Optional<String> getVisibility() {
        return Optional.ofNullable(visibility);
    }

    @Override
    public Optional<String> getAliasName() {
        return Optional.ofNullable(aliasName);
    }

    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    @Override
    public Optional<String> getTopic() {
        return Optional.ofNullable(topic);
    }

    @Override
    public Optional<Set<_MatrixID>> getInvites() {
        return Optional.ofNullable(invites);
    }

    @Override
    public Optional<Map<String, JsonElement>> getCreationContent() {
        return Optional.ofNullable(creationContent);
    }

    @Override
    public Optional<String> getPreset() {
        return Optional.ofNullable(preset);
    }

    @Override
    public Optional<Boolean> isDirect() {
        return Optional.ofNullable(isDirect);
    }

    @Override
    public Optional<Boolean> isGuestCanJoin() {
        return Optional.ofNullable(guestCanJoin);
    }

}
