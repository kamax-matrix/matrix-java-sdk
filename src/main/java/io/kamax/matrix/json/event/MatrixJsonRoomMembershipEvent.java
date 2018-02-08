/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Maxime Dor
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

package io.kamax.matrix.json.event;

import com.google.gson.JsonObject;

import io.kamax.matrix.MatrixID;
import io.kamax.matrix._MatrixID;
import io.kamax.matrix.event._RoomMembershipEvent;
import io.kamax.matrix.json.MatrixJsonObject;

import java.util.Optional;

public class MatrixJsonRoomMembershipEvent extends MatrixJsonRoomEvent implements _RoomMembershipEvent {

    private Content content;
    private _MatrixID invitee;

    public MatrixJsonRoomMembershipEvent(JsonObject obj) {
        super(obj);

        content = new Content(getObj("content"));
        invitee = new MatrixID(getString("state_key"));
    }

    @Override
    public String getMembership() {
        return content.getMembership();
    }

    @Override
    public Optional<String> getAvatarUrl() {
        return Optional.ofNullable(content.getAvatar());
    }

    @Override
    public Optional<String> getDisplayName() {
        return Optional.ofNullable(content.getDisplayName());
    }

    @Override
    public _MatrixID getInvitee() {
        return invitee;
    }

    private class Content extends MatrixJsonObject {

        private String membership;
        private String avatar;
        private String displayName;

        Content(JsonObject obj) {
            super(obj);

            setMembership(getString("membership"));
            setAvatar(avatar = getStringOrNull("avatar_url"));
            setDisplayName(displayName = getStringOrNull("displayname"));
        }

        String getMembership() {
            return membership;
        }

        void setMembership(String membership) {
            this.membership = membership;
        }

        String getAvatar() {
            return avatar;
        }

        void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        String getDisplayName() {
            return displayName;
        }

        void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

    }

}
