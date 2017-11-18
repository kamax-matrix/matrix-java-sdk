/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Arne Augenstein
 *
 * https://max.kamax.io/
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

import io.kamax.matrix.MatrixID;
import io.kamax.matrix._MatrixID;

import org.hamcrest.core.IsEqual;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MatrixHttpRoomTest extends MatrixHttpTest {
    private String roomId = "roomId892347847";
    private String eventId = "YUwRidLecu";

    private String nameUrl = String.format("/_matrix/client/r0/rooms/%s/state/m.room.name", roomId) + tokenParameter;
    private String nameOfRoom = "test room";
    private String nameResponse = String.format("{\"name\": \"%s\"}", nameOfRoom);

    private String topicUrl = String.format("/_matrix/client/r0/rooms/%s/state/m.room.topic", roomId) + tokenParameter;
    private String testTopic = "test topic";
    private String topicResponse = String.format("{\"topic\": \"%s\"}", testTopic);

    private String joinUrl = String.format("/_matrix/client/r0/rooms/%s/join", roomId) + tokenParameter;
    private String joinResponse = String.format("{\"roomId\": \"%s\"}", roomId);

    private String leaveUrl = String.format("/_matrix/client/r0/rooms/%s/leave", roomId) + tokenParameter;
    private String leaveResponse = String.format("{\"roomId\": \"%s\"}", roomId);

    private String sendTextUrl = String.format("/_matrix/client/r0/rooms/%s/send/m.room.message/([0-9.]+)\\", roomId)
            + tokenParameter;
    private String testText = "test text";
    private String sendTextResponse = String.format("{\"event_id\": \"%s\"}", eventId) + tokenParameter;

    private String getJoinedUsersUrl = String.format("/_matrix/client/r0/rooms/%s/joined_members", roomId)
            + tokenParameter;
    private String joinedUser1 = "@test:testserver.org";
    private String joinedUser2 = "@test2:testserver.org";
    private String getJoinedUsersResponse = String.format("{\"joined\": {\"%s\": \"1\", \"%s\": \"2\"}}", joinedUser1,
            joinedUser2);

    @Test
    public void getName() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(200).withBody(nameResponse)));

        login();
        assertThat(createRoomObject().getName(), IsEqual.equalTo(Optional.of(nameOfRoom)));
        logout();
    }

    @Test
    public void getEmptyName() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(404).withBody(error404Response)));

        login();
        assertThat(createRoomObject().getName(), IsEqual.equalTo(Optional.empty()));
        logout();
    }

    @Test
    public void getNameAccessDenied() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(403).withBody(error403Response)));

        login();
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::getName);
        checkErrorInfo403(e);
        logout();
    }

    @Test
    public void getNameRateLimited() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(429).withBody(error429Response)));

        login();
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::getName);
        checkErrorInfo429(e);
        logout();
    }

    @Test
    public void getTopic() throws URISyntaxException {
        stubFor(get(urlEqualTo(topicUrl)).willReturn(aResponse().withStatus(200).withBody(topicResponse)));

        login();
        assertThat(createRoomObject().getTopic(), IsEqual.equalTo(Optional.of(testTopic)));
        logout();
    }

    @Test
    public void getEmptyTopic() throws URISyntaxException {
        stubFor(get(urlEqualTo(topicUrl)).willReturn(aResponse().withStatus(404).withBody(error404Response)));

        login();
        assertThat(createRoomObject().getTopic(), IsEqual.equalTo(Optional.empty()));
        logout();
    }

    @Test
    public void getTopicAccessDenied() throws URISyntaxException {
        stubFor(get(urlEqualTo(topicUrl)).willReturn(aResponse().withStatus(403).withBody(error403Response)));

        login();
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::getTopic);
        checkErrorInfo403(e);
        logout();
    }

    @Test
    public void getTopicRateLimited() throws URISyntaxException {
        stubFor(get(urlEqualTo(topicUrl)).willReturn(aResponse().withStatus(429).withBody(error429Response)));

        login();
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::getTopic);
        checkErrorInfo429(e);
        logout();
    }

    @Test
    public void join() throws URISyntaxException {
        stubFor(post(urlEqualTo(joinUrl)).willReturn(aResponse().withStatus(200).withBody(joinResponse)));

        login();
        createRoomObject().join();
        logout();
    }

    @Test
    public void joinRoomNotFound() throws URISyntaxException {
        stubFor(post(urlEqualTo(joinUrl)).willReturn(aResponse().withStatus(404).withBody(error404Response)));

        login();
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::join);
        checkErrorInfo404(e);
        logout();
    }

    @Test
    public void joinAccessDenied() throws URISyntaxException {
        stubFor(post(urlEqualTo(joinUrl)).willReturn(aResponse().withStatus(403).withBody(error403Response)));

        login();
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::join);
        checkErrorInfo403(e);
        logout();
    }

    @Test
    public void joinRateLimited() throws URISyntaxException {
        stubFor(post(urlEqualTo(joinUrl)).willReturn(aResponse().withStatus(429).withBody(error429Response)));

        login();
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::join);
        checkErrorInfo429(e);
        logout();
    }

    @Test
    public void leave() throws URISyntaxException {
        stubFor(post(urlEqualTo(leaveUrl)).willReturn(aResponse().withStatus(200).withBody(leaveResponse)));

        login();
        createRoomObject().leave();
        logout();
    }

    @Test
    public void leaveAccessDenied() throws URISyntaxException {
        stubFor(post(urlEqualTo(leaveUrl)).willReturn(aResponse().withStatus(403).withBody(error403Response)));

        login();
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::leave);
        checkErrorInfo403(e);
        logout();
    }

    @Test
    public void leaveRoomNotFound() throws URISyntaxException {
        stubFor(post(urlEqualTo(leaveUrl)).willReturn(aResponse().withStatus(404).withBody(error404Response)));

        login();
        createRoomObject().leave();
        logout();
    }

    @Test
    public void leaveRateLimited() throws URISyntaxException {
        stubFor(post(urlEqualTo(leaveUrl)).willReturn(aResponse().withStatus(429).withBody(error429Response)));

        login();
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::leave);
        checkErrorInfo429(e);
        logout();
    }

    @Test
    public void sendText() throws URISyntaxException {
        stubFor(put(urlMatching(sendTextUrl)).willReturn(aResponse().withStatus(200).withBody(sendTextResponse)));

        login();
        createRoomObject().sendText(testText);
        logout();
    }

    @Test
    public void sendTextAccessDenied() throws URISyntaxException {
        stubFor(put(urlMatching(sendTextUrl)).willReturn(aResponse().withStatus(403).withBody(error403Response)));

        login();
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                () -> createRoomObject().sendText(testText));
        checkErrorInfo403(e);
        logout();
    }

    @Test
    public void sendTextRoomNotFound() throws URISyntaxException {
        stubFor(put(urlMatching(sendTextUrl)).willReturn(aResponse().withStatus(404).withBody(error404Response)));

        login();
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                () -> createRoomObject().sendText(testText));
        checkErrorInfo404(e);
        logout();
    }

    @Test
    public void sendTextRateLimited() throws URISyntaxException {
        stubFor(put(urlMatching(sendTextUrl)).willReturn(aResponse().withStatus(429).withBody(error429Response)));

        login();
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                () -> createRoomObject().sendText(testText));
        checkErrorInfo429(e);
        logout();
    }

    @Test
    public void getJoinedUsers() throws URISyntaxException {
        stubFor(get(urlEqualTo(getJoinedUsersUrl))
                .willReturn(aResponse().withStatus(200).withBody(getJoinedUsersResponse)));

        List<_MatrixID> expectedResult = new ArrayList<>();
        expectedResult.add(new MatrixID(joinedUser1));
        expectedResult.add(new MatrixID(joinedUser2));

        login();
        assertThat(createRoomObject().getJoinedUsers(), IsEqual.equalTo(expectedResult));
        logout();
    }

    @Test
    public void getJoinedUsersRoomNotFound() throws URISyntaxException {
        stubFor(get(urlEqualTo(getJoinedUsersUrl)).willReturn(aResponse().withStatus(404).withBody(error404Response)));

        login();
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                createRoomObject()::getJoinedUsers);
        checkErrorInfo404(e);
        logout();
    }

    @Test
    public void getJoinedUsersRateLimited() throws URISyntaxException {
        stubFor(get(urlEqualTo(getJoinedUsersUrl)).willReturn(aResponse().withStatus(429).withBody(error429Response)));

        login();
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                createRoomObject()::getJoinedUsers);
        checkErrorInfo429(e);
        logout();
    }

    private MatrixHttpRoom createRoomObject() throws URISyntaxException {
        MatrixClientContext context = createClientContext();
        return new MatrixHttpRoom(context, roomId);
    }
}
