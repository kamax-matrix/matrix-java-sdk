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

import org.junit.Test;

import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class MatrixHttpRoomWiremockTest extends AMatrixHttpRoomTest {
    private String nameUrl = String.format("/_matrix/client/r0/rooms/%s/state/m.room.name", roomId) + tokenParameter;
    private String nameResponse = String.format("{\"name\": \"%s\"}", nameOfRoom);

    private String topicUrl = String.format("/_matrix/client/r0/rooms/%s/state/m.room.topic", roomId) + tokenParameter;
    private String topicResponse = String.format("{\"topic\": \"%s\"}", testTopic);

    private String joinUrl = String.format("/_matrix/client/r0/rooms/%s/join", roomId) + tokenParameter;
    private String joinResponse = String.format("{\"roomId\": \"%s\"}", roomId);

    private String leaveUrl = String.format("/_matrix/client/r0/rooms/%s/leave", roomId) + tokenParameter;
    private String leaveResponse = String.format("{\"roomId\": \"%s\"}", roomId);

    private String sendTextUrl = String.format("/_matrix/client/r0/rooms/%s/send/m.room.message/([0-9.]+)\\", roomId)
            + tokenParameter;
    private String sendTextResponse = String.format("{\"event_id\": \"%s\"}", eventId) + tokenParameter;

    private String getJoinedUsersUrl = String.format("/_matrix/client/r0/rooms/%s/joined_members", roomId)
            + tokenParameter;
    private String getJoinedUsersResponse = String.format("{\"joined\": {\"%s\": \"1\", \"%s\": \"2\"}}", joinedUser1,
            joinedUser2);

    @Override
    public void login() throws URISyntaxException {
    }

    @Override
    public void logout() {
    }

    @Test
    public void getName() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(200).withBody(nameResponse)));
        super.getName();
    }

    @Test
    public void getEmptyName() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(404).withBody(errorNotFoundResponse)));
        super.getEmptyName();
    }

    @Test
    public void getNameAccessDenied() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(403).withBody(errorAccessDeniedResponse)));
        super.getNameAccessDenied();
    }

    @Test
    public void getNameRateLimited() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(429).withBody(errorRateLimitedResponse)));
        super.getNameRateLimited();
    }

    @Test
    public void getTopic() throws URISyntaxException {
        stubFor(get(urlEqualTo(topicUrl)).willReturn(aResponse().withStatus(200).withBody(topicResponse)));
        super.getTopic();
    }

    @Test
    public void getEmptyTopic() throws URISyntaxException {
        stubFor(get(urlEqualTo(topicUrl)).willReturn(aResponse().withStatus(404).withBody(errorNotFoundResponse)));
        super.getEmptyTopic();
    }

    @Test
    public void getTopicAccessDenied() throws URISyntaxException {
        stubFor(get(urlEqualTo(topicUrl)).willReturn(aResponse().withStatus(403).withBody(errorAccessDeniedResponse)));
        super.getTopicAccessDenied();
    }

    @Test
    public void getTopicRateLimited() throws URISyntaxException {
        stubFor(get(urlEqualTo(topicUrl)).willReturn(aResponse().withStatus(429).withBody(errorRateLimitedResponse)));
        super.getTopicRateLimited();
    }

    @Test
    public void join() throws URISyntaxException {
        stubFor(post(urlEqualTo(joinUrl)).willReturn(aResponse().withStatus(200).withBody(joinResponse)));
        super.join();
    }

    @Test
    public void joinRoomNotFound() throws URISyntaxException {
        stubFor(post(urlEqualTo(joinUrl)).willReturn(aResponse().withStatus(404).withBody(errorNotFoundResponse)));
        super.joinRoomNotFound();
    }

    @Test
    public void joinAccessDenied() throws URISyntaxException {
        stubFor(post(urlEqualTo(joinUrl)).willReturn(aResponse().withStatus(403).withBody(errorAccessDeniedResponse)));
        super.joinAccessDenied();
    }

    @Test
    public void joinRateLimited() throws URISyntaxException {
        stubFor(post(urlEqualTo(joinUrl)).willReturn(aResponse().withStatus(429).withBody(errorRateLimitedResponse)));
        super.joinRateLimited();
    }

    @Test
    public void leave() throws URISyntaxException {
        stubFor(post(urlEqualTo(leaveUrl)).willReturn(aResponse().withStatus(200).withBody(leaveResponse)));
        super.leave();
    }

    @Test
    public void leaveAccessDenied() throws URISyntaxException {
        stubFor(post(urlEqualTo(leaveUrl)).willReturn(aResponse().withStatus(403).withBody(errorAccessDeniedResponse)));
        super.leaveAccessDenied();
    }

    @Test
    public void leaveRoomNotFound() throws URISyntaxException {
        stubFor(post(urlEqualTo(leaveUrl)).willReturn(aResponse().withStatus(404).withBody(errorNotFoundResponse)));
        super.leaveRoomNotFound();
    }

    @Test
    public void leaveRateLimited() throws URISyntaxException {
        stubFor(post(urlEqualTo(leaveUrl)).willReturn(aResponse().withStatus(429).withBody(errorRateLimitedResponse)));
        super.leaveRateLimited();
    }

    @Test
    public void sendText() throws URISyntaxException {
        stubFor(put(urlMatching(sendTextUrl)).willReturn(aResponse().withStatus(200).withBody(sendTextResponse)));
        super.sendText();
    }

    @Test
    public void sendTextAccessDenied() throws URISyntaxException {
        stubFor(put(urlMatching(sendTextUrl))
                .willReturn(aResponse().withStatus(403).withBody(errorAccessDeniedResponse)));
        super.sendTextAccessDenied();
    }

    @Test
    public void sendTextRoomNotFound() throws URISyntaxException {
        stubFor(put(urlMatching(sendTextUrl)).willReturn(aResponse().withStatus(404).withBody(errorNotFoundResponse)));
        super.sendTextRoomNotFound();
    }

    @Test
    public void sendTextRateLimited() throws URISyntaxException {
        stubFor(put(urlMatching(sendTextUrl))
                .willReturn(aResponse().withStatus(429).withBody(errorRateLimitedResponse)));
        super.sendTextRateLimited();
    }

    @Test
    public void getJoinedUsers() throws URISyntaxException {
        stubFor(get(urlEqualTo(getJoinedUsersUrl))
                .willReturn(aResponse().withStatus(200).withBody(getJoinedUsersResponse)));
        super.getJoinedUsers();
    }

    @Test
    public void getJoinedUsersRoomNotFound() throws URISyntaxException {
        stubFor(get(urlEqualTo(getJoinedUsersUrl))
                .willReturn(aResponse().withStatus(404).withBody(errorNotFoundResponse)));
        super.getJoinedUsersRoomNotFound();
    }

    @Test
    public void getJoinedUsersRateLimited() throws URISyntaxException {
        stubFor(get(urlEqualTo(getJoinedUsersUrl))
                .willReturn(aResponse().withStatus(429).withBody(errorRateLimitedResponse)));
        super.getJoinedUsersRateLimited();
    }
}
