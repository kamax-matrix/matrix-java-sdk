/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Arne Augenstein
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

import io.kamax.matrix.MatrixID;
import io.kamax.matrix._MatrixID;

import org.hamcrest.core.IsEqual;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class AMatrixHttpRoomTest extends MatrixHttpTest {
    protected String roomId = "roomId892347847";
    protected String eventId = "YUwRidLecu";

    protected String nameOfRoom = "test room";
    protected String testTopic = "test topic";
    protected String testText = "test text";
    protected String joinedUser1 = "@test:testserver.org";
    protected String joinedUser2 = "@test2:testserver.org";

    @Test
    public void getName() throws URISyntaxException {
        assertThat(createRoomObject().getName(), IsEqual.equalTo(Optional.of(nameOfRoom)));
    }

    @Test
    public void getEmptyName() throws URISyntaxException {
        assertThat(createRoomObject().getName(), IsEqual.equalTo(Optional.empty()));
    }

    @Test
    public void getNameAccessDenied() throws URISyntaxException {
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::getName);
        checkErrorInfoAccessDenied(e);
    }

    @Test
    public void getNameRateLimited() throws URISyntaxException {
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::getName);
        checkErrorInfoRateLimited(e);
    }

    @Test
    public void getTopic() throws URISyntaxException {
        assertThat(createRoomObject().getTopic(), IsEqual.equalTo(Optional.of(testTopic)));
    }

    @Test
    public void getEmptyTopic() throws URISyntaxException {
        assertThat(createRoomObject().getTopic(), IsEqual.equalTo(Optional.empty()));
    }

    @Test
    public void getTopicAccessDenied() throws URISyntaxException {
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::getTopic);
        checkErrorInfoAccessDenied(e);
    }

    @Test
    public void getTopicRateLimited() throws URISyntaxException {
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::getTopic);
        checkErrorInfoRateLimited(e);
    }

    @Test
    public void join() throws URISyntaxException {
        createRoomObject().join();
    }

    @Test
    public void joinRoomNotFound() throws URISyntaxException {
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::join);
        checkErrorInfoNotFound(e);
    }

    @Test
    public void joinAccessDenied() throws URISyntaxException {
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::join);
        checkErrorInfoAccessDenied(e);
    }

    @Test
    public void joinRateLimited() throws URISyntaxException {
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::join);
        checkErrorInfoRateLimited(e);
    }

    @Test
    public void leave() throws URISyntaxException {
        createRoomObject().leave();
    }

    @Test
    public void leaveAccessDenied() throws URISyntaxException {
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::leave);
        checkErrorInfoAccessDenied(e);
    }

    @Test
    public void leaveRoomNotFound() throws URISyntaxException {
        createRoomObject().leave();
    }

    @Test
    public void leaveRateLimited() throws URISyntaxException {
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::leave);
        checkErrorInfoRateLimited(e);
    }

    @Test
    public void sendText() throws URISyntaxException {
        createRoomObject().sendText(testText);
    }

    @Test
    public void sendTextAccessDenied() throws URISyntaxException {
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                () -> createRoomObject().sendText(testText));
        checkErrorInfoAccessDenied(e);
    }

    @Test
    public void sendTextRoomNotFound() throws URISyntaxException {
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                () -> createRoomObject().sendText(testText));
        checkErrorInfoNotFound(e);
    }

    @Test
    public void sendTextRateLimited() throws URISyntaxException {
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                () -> createRoomObject().sendText(testText));
        checkErrorInfoRateLimited(e);
    }

    @Test
    public void getJoinedUsers() throws URISyntaxException {
        List<_MatrixID> expectedResult = new ArrayList<>();
        expectedResult.add(new MatrixID(joinedUser1));
        expectedResult.add(new MatrixID(joinedUser2));

        assertThat(createRoomObject().getJoinedUsers(), IsEqual.equalTo(expectedResult));
    }

    @Test
    public void getJoinedUsersRoomNotFound() throws URISyntaxException {
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                createRoomObject()::getJoinedUsers);
        checkErrorInfoNotFound(e);
    }

    @Test
    public void getJoinedUsersRateLimited() throws URISyntaxException {
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                createRoomObject()::getJoinedUsers);
        checkErrorInfoRateLimited(e);
    }

    private MatrixHttpRoom createRoomObject() throws URISyntaxException {
        MatrixClientContext context = getOrCreateClientContext();
        return new MatrixHttpRoom(context, roomId);
    }
}
