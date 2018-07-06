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

/**
 * Room creation presets available in the specification.
 */
public enum RoomCreationPreset {

    /**
     * The following room state will be set:
     * - join_rules is set to invite.
     * - history_visibility is set to shared.
     */
    Private("private_chat"),

    /**
     * The following room state will be set:
     * - join_rules is set to invite.
     * - history_visibility is set to shared.
     * - All invitees are given the same power level as the room creator.
     */
    TrustedPrivate("trusted_private_chat"),

    /**
     * The following room state will be set:
     * - join_rules is set to public.
     * - history_visibility is set to shared.
     */
    PublicChat("public_chat");

    private String id;

    RoomCreationPreset(String id) {
        this.id = id;
    }

    /**
     * Get the Matrix value for this setting.
     * 
     * @return the value.
     */
    public String get() {
        return id;
    }

}
