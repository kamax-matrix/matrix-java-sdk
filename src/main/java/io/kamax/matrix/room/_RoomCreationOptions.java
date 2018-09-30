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

package io.kamax.matrix.room;

import com.google.gson.JsonElement;

import io.kamax.matrix._MatrixID;

import java.util.Map;
import java.util.Set;
import java8.util.Optional;

/**
 * Possible options that can be passed to the room creation call.
 */
public interface _RoomCreationOptions {

    /**
     * Get the room directory visibility.
     * 
     * @return the optional value.
     */
    Optional<String> getVisibility();

    /**
     * Get the desired room alias local part.
     * 
     * @return the optional value.
     */
    Optional<String> getAliasName();

    /**
     * Get the room name.
     * 
     * @return the optional value.
     */
    Optional<String> getName();

    /**
     * Get the room topic.
     * 
     * @return the optional value.
     */
    Optional<String> getTopic();

    /**
     * Get the list of user Matrix IDs to invite to the room.
     * 
     * @return the optional value.
     */
    Optional<Set<_MatrixID>> getInvites();

    /**
     * Get the extra keys to be added to the content of the m.room.create event.
     * 
     * @return the optional value.
     */
    Optional<Map<String, JsonElement>> getCreationContent();

    /**
     * Get the convenience parameter for setting various default state events.
     * 
     * @return the optional value.
     */
    Optional<String> getPreset();

    /**
     * What the is_direct flag on the m.room.member event for the invites should be set to.
     * 
     * @return the optional value.
     */
    Optional<Boolean> isDirect();

    /**
     * Get guest allowance to join the room.
     * 
     * @return the optional value.
     */
    Optional<Boolean> isGuestCanJoin();

}
