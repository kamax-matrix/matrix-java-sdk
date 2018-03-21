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

package io.kamax.matrix.client;

import io.kamax.matrix._MatrixID;
import io.kamax.matrix._MatrixUser;
import io.kamax.matrix.hs._MatrixRoom;
import io.kamax.matrix.room.RoomAlias;
import io.kamax.matrix.room._RoomAliasLookup;
import io.kamax.matrix.room._RoomCreationOptions;

import java.util.Optional;

public interface _MatrixClient extends _MatrixClientRaw {

    void setDisplayName(String name);

    _RoomAliasLookup lookup(RoomAlias alias);

    _MatrixRoom createRoom(_RoomCreationOptions options);

    _MatrixRoom getRoom(String roomId);

    _MatrixRoom joinRoom(String roomIdOrAlias);

    _MatrixUser getUser(_MatrixID mxId);

    Optional<String> getDeviceId();

    void login(MatrixPasswordLoginCredentials credentials);

    void logout();

    _SyncData sync(_SyncOptions options);

}
