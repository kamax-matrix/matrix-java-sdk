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

package io.kamax.matrix.client;

import io.kamax.matrix.client.regular.SyncOptions;
import io.kamax.matrix.event._MatrixAccountDataEvent;
import io.kamax.matrix.event._MatrixEvent;
import io.kamax.matrix.hs._MatrixRoom;
import io.kamax.matrix.json.MatrixJsonEventFactory;
import io.kamax.matrix.json.event.MatrixJsonRoomTagsEvent;
import io.kamax.matrix.room.RoomCreationOptions;
import io.kamax.matrix.room.RoomTag;

import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertTrue;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AMatrixHttpRoomTagTest extends MatrixHttpTest {
    protected String testTag = "usertag";
    private String testRoomName = "TagTestRoom";

    @Test
    public void setAndReadTags() {
        _MatrixRoom room = getAndPrepareRoom();

        room.addFavouriteTag();
        room.addLowpriorityTag();
        room.addUserTag(testTag);

        assertFalse(room.getUserTags().isEmpty());
        assertTrue(room.getFavouriteTag().isPresent());
        assertTrue(room.getLowpriorityTag().isPresent());

        room.deleteFavouriteTag();
        room.deleteLowpriorityTag();
        room.deleteUserTag(testTag);

        assertTrue(room.getUserTags().isEmpty());
        assertFalse(room.getFavouriteTag().isPresent());
        assertFalse(room.getLowpriorityTag().isPresent());
    }

    @Test
    public void setTagWithOrderOutOfRange() {
        _MatrixRoom room = getAndPrepareRoom();

        assertThrows(IllegalArgumentException.class, () -> room.addUserTag("test", -1.0));
        assertThrows(IllegalArgumentException.class, () -> room.addUserTag("test", -0.1));
        assertThrows(IllegalArgumentException.class, () -> room.addUserTag("test", 1.1));
    }

    @Test
    public void readTagsFromSync() {
        _MatrixRoom room = getAndPrepareRoom();

        room.addFavouriteTag();
        room.addUserTag(testTag);

        _SyncData syncData = client.sync(SyncOptions.build().get());
        Optional<_SyncData.JoinedRoom> roomFromSync = syncData.getRooms().getJoined().stream()
                .filter(joinedRoom -> room.getId().equals(joinedRoom.getId())).findFirst();

        assertTrue(roomFromSync.isPresent());

        boolean userTagFound = false;
        boolean favouriteTagFound = false;

        for (_MatrixAccountDataEvent event : roomFromSync.get().getAccountData().getEvents()) {
            _MatrixEvent parsedEvent = MatrixJsonEventFactory.get(event.getJson());
            if (parsedEvent instanceof MatrixJsonRoomTagsEvent) {
                List<RoomTag> tags = ((MatrixJsonRoomTagsEvent) parsedEvent).getTags();
                List<String> tagNames = tags.stream().map(it -> it.getNamespace() + "." + it.getName())
                        .collect(Collectors.toList());
                if (tagNames.contains("u." + testTag)) {
                    userTagFound = true;
                }

                if (tagNames.contains("m.favourite")) {
                    favouriteTagFound = true;
                }
            }
        }

        assertTrue(userTagFound);
        assertTrue(favouriteTagFound);

        room.deleteFavouriteTag();
        room.deleteUserTag(testTag);
    }

    private _MatrixRoom getAndPrepareRoom() {
        _MatrixRoom room = null;

        for (_MatrixRoom joinedRoom : client.getJoinedRooms()) {
            if (joinedRoom.getName().isPresent() && testRoomName.equals(joinedRoom.getName().get())) {
                room = joinedRoom;
                break;
            }
        }

        if (room == null) {
            RoomCreationOptions.Builder roomOptions = new RoomCreationOptions.Builder().setName(testRoomName);
            room = client.createRoom(roomOptions.get());
        }

        if (room.getFavouriteTag().isPresent()) {
            room.deleteFavouriteTag();
        }

        if (room.getLowpriorityTag().isPresent()) {
            room.deleteLowpriorityTag();
        }

        for (RoomTag tag : room.getUserTags()) {
            room.deleteUserTag(tag.getName());
        }

        // Assert, that we start with a clean room without tags
        assertTrue(room.getUserTags().isEmpty());
        return room;
    }
}
