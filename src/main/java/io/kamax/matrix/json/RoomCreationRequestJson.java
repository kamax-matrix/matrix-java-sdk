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

package io.kamax.matrix.json;

import com.google.gson.JsonElement;

import io.kamax.matrix._MatrixID;
import io.kamax.matrix.room._RoomCreationOptions;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class RoomCreationRequestJson {

    private String visibility;
    private String roomAliasName;
    private String name;
    private String topic;
    private Set<String> invite;
    private Map<String, JsonElement> creationContent;
    private String preset;
    private Boolean isDirect;
    private Boolean guestCanJoin;

    public RoomCreationRequestJson(_RoomCreationOptions options) {
        this.visibility = options.getVisibility().orElse(null);
        this.roomAliasName = options.getAliasName().orElse(null);
        this.name = options.getName().orElse(null);
        this.topic = options.getTopic().orElse(null);
        this.invite = options.getInvites().filter(ids -> !ids.isEmpty())
                .map(ids -> ids.stream().map(_MatrixID::getId).collect(Collectors.toSet())).orElse(null);
        this.creationContent = options.getCreationContent().filter(c -> !c.isEmpty()).orElse(null);
        this.preset = options.getPreset().orElse(null);
        this.isDirect = options.isDirect().orElse(null);
        this.guestCanJoin = options.isGuestCanJoin().orElse(null);
    }

}
