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
        assertThat(createRoomObject().getName(), IsEqual.equalTo(Optional.of(nameOfRoom)));
    }

    @Test
    public void getName404() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(404).withBody(error404Response)));
        assertThat(createRoomObject().getName(), IsEqual.equalTo(Optional.empty()));
    }

    @Test
    public void getNameError403() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(403).withBody(error403Response)));

        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::getName);
        checkErrorInfo403(e);
    }

    @Test
    public void getNameError429() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(429).withBody(error429Response)));

        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::getName);
        checkErrorInfo429(e);
    }

    @Test
    public void getTopic() throws URISyntaxException {
        stubFor(get(urlEqualTo(topicUrl)).willReturn(aResponse().withStatus(200).withBody(topicResponse)));
        assertThat(createRoomObject().getTopic(), IsEqual.equalTo(Optional.of(testTopic)));
    }

    @Test
    public void getTopicError404() throws URISyntaxException {
        stubFor(get(urlEqualTo(topicUrl)).willReturn(aResponse().withStatus(404).withBody(error404Response)));
        assertThat(createRoomObject().getTopic(), IsEqual.equalTo(Optional.empty()));
    }

    @Test
    public void getTopicError403() throws URISyntaxException {
        stubFor(get(urlEqualTo(topicUrl)).willReturn(aResponse().withStatus(403).withBody(error403Response)));

        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::getTopic);
        checkErrorInfo403(e);
    }

    @Test
    public void getTopicError429() throws URISyntaxException {
        stubFor(get(urlEqualTo(topicUrl)).willReturn(aResponse().withStatus(429).withBody(error429Response)));

        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::getTopic);
        checkErrorInfo429(e);
    }

    @Test
    public void join() throws URISyntaxException {
        stubFor(post(urlEqualTo(joinUrl)).willReturn(aResponse().withStatus(200).withBody(joinResponse)));
        createRoomObject().join();
    }

    @Test
    public void joinError404() throws URISyntaxException {
        stubFor(post(urlEqualTo(joinUrl)).willReturn(aResponse().withStatus(404).withBody(error404Response)));

        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::join);
        checkErrorInfo404(e);
    }

    @Test
    public void joinError403() throws URISyntaxException {
        stubFor(post(urlEqualTo(joinUrl)).willReturn(aResponse().withStatus(403).withBody(error403Response)));

        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::join);
        checkErrorInfo403(e);
    }

    @Test
    public void joinError429() throws URISyntaxException {
        stubFor(post(urlEqualTo(joinUrl)).willReturn(aResponse().withStatus(429).withBody(error429Response)));

        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::join);
        checkErrorInfo429(e);
    }

    @Test
    public void leave() throws URISyntaxException {
        stubFor(post(urlEqualTo(leaveUrl)).willReturn(aResponse().withStatus(200).withBody(leaveResponse)));
        createRoomObject().leave();
    }

    @Test
    public void leaveError403() throws URISyntaxException {
        stubFor(post(urlEqualTo(leaveUrl)).willReturn(aResponse().withStatus(403).withBody(error403Response)));

        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::leave);
        checkErrorInfo403(e);
    }

    @Test
    public void leaveError404() throws URISyntaxException {
        stubFor(post(urlEqualTo(leaveUrl)).willReturn(aResponse().withStatus(404).withBody(error404Response)));
        createRoomObject().leave();
    }

    @Test
    public void leaveError429() throws URISyntaxException {
        stubFor(post(urlEqualTo(leaveUrl)).willReturn(aResponse().withStatus(429).withBody(error429Response)));

        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createRoomObject()::leave);
        checkErrorInfo429(e);
    }

    @Test
    public void sendText() throws URISyntaxException {
        stubFor(put(urlMatching(sendTextUrl)).willReturn(aResponse().withStatus(200).withBody(sendTextResponse)));
        createRoomObject().sendText(testText);
    }

    @Test
    public void sendTextError403() throws URISyntaxException {
        stubFor(put(urlMatching(sendTextUrl)).willReturn(aResponse().withStatus(403).withBody(error403Response)));

        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                () -> createRoomObject().sendText(testText));
        checkErrorInfo403(e);
    }

    @Test
    public void sendTextError404() throws URISyntaxException {
        stubFor(put(urlMatching(sendTextUrl)).willReturn(aResponse().withStatus(404).withBody(error404Response)));

        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                () -> createRoomObject().sendText(testText));
        checkErrorInfo404(e);
    }

    @Test
    public void sendTextError429() throws URISyntaxException {
        stubFor(put(urlMatching(sendTextUrl)).willReturn(aResponse().withStatus(429).withBody(error429Response)));

        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                () -> createRoomObject().sendText(testText));
        checkErrorInfo429(e);
    }

    @Test
    public void getJoinedUsers() throws URISyntaxException {
        stubFor(get(urlEqualTo(getJoinedUsersUrl))
                .willReturn(aResponse().withStatus(200).withBody(getJoinedUsersResponse)));

        List<_MatrixID> expectedResult = new ArrayList<>();
        expectedResult.add(new MatrixID(joinedUser1));
        expectedResult.add(new MatrixID(joinedUser2));

        assertThat(createRoomObject().getJoinedUsers(), IsEqual.equalTo(expectedResult));
    }

    @Test
    public void getJoinedUsersError404() throws URISyntaxException {
        stubFor(get(urlEqualTo(getJoinedUsersUrl)).willReturn(aResponse().withStatus(404).withBody(error404Response)));

        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                createRoomObject()::getJoinedUsers);
        checkErrorInfo404(e);
    }

    @Test
    public void getJoinedUsersError429() throws URISyntaxException {
        stubFor(get(urlEqualTo(getJoinedUsersUrl)).willReturn(aResponse().withStatus(429).withBody(error429Response)));

        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                createRoomObject()::getJoinedUsers);
        checkErrorInfo429(e);
    }

    private MatrixHttpRoom createRoomObject() throws URISyntaxException {
        MatrixClientContext context = createClientContext();
        return new MatrixHttpRoom(context, roomId);
    }
}
