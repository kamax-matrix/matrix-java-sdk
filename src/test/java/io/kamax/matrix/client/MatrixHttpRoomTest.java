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
import static org.junit.Assert.fail;

public class MatrixHttpRoomTest extends MatrixHttpTest {
    private String roomId = "roomId892347847";

    private String nameUrl = String.format("/_matrix/client/r0/rooms/%s/state/m.room.name", roomId) + tokenParameter;
    private String nameOfRoom = "test room";
    private String nameResponse = String.format("{\"name\": \"%s\"}", nameOfRoom);

    private String topicUrl = String.format("/_matrix/client/r0/rooms/%s/state/m.room.topic", roomId) + tokenParameter;
    private String testTopic = "test topic";
    private String topicResponse = String.format("{\"topic\": \"%s\"}", testTopic);

    @Test
    public void getName() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(200).withBody(nameResponse)));
        assertThat(createRoomObject().getName(), IsEqual.equalTo(Optional.of(nameOfRoom)));
    }

    @Test
    public void getName404() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(404).withBody(nameResponse)));
        assertThat(createRoomObject().getName(), IsEqual.equalTo(Optional.empty()));
    }

    @Test
    public void getNameError403() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(403).withBody(error403Response)));

        try {
            createRoomObject().getName();
        } catch (MatrixClientRequestException e) {
            // TODO getName does not throw Exceptions with error infos, yet
            // checkErrorInfo403(e);
            return;
        }
        fail("In this case, an exception has to be thrown.");
    }

    @Test
    public void getNameError429() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(429).withBody(error403Response)));

        try {
            createRoomObject().getName();
        } catch (MatrixClientRequestException e) {
            // TODO getName does not throw Exceptions with error infos, yet
            // checkErrorInfo429(e);
            return;
        }
        fail("In this case, an exception has to be thrown.");
    }

    @Test
    public void getTopic() throws URISyntaxException {
        stubFor(get(urlEqualTo(topicUrl)).willReturn(aResponse().withStatus(200).withBody(topicResponse)));
        assertThat(createRoomObject().getTopic(), IsEqual.equalTo(Optional.of(testTopic)));
    }

    @Test
    public void getTopicError404() throws URISyntaxException {
        stubFor(get(urlEqualTo(topicUrl)).willReturn(aResponse().withStatus(404).withBody(topicResponse)));
        assertThat(createRoomObject().getTopic(), IsEqual.equalTo(Optional.empty()));
    }

    @Test
    public void getTopicError403() throws URISyntaxException {
        stubFor(get(urlEqualTo(topicUrl)).willReturn(aResponse().withStatus(403).withBody(error403Response)));

        try {
            createRoomObject().getTopic();
        } catch (MatrixClientRequestException e) {
            checkErrorInfo403(e);
            return;
        }
        fail("In this case, an exception has to be thrown.");
    }

    @Test
    public void getTopicError429() throws URISyntaxException {
        stubFor(get(urlEqualTo(topicUrl)).willReturn(aResponse().withStatus(429).withBody(error429Response)));

        try {
            createRoomObject().getTopic();
        } catch (MatrixClientRequestException e) {
            checkErrorInfo429(e);
            return;
        }
        fail("In this case, an exception has to be thrown.");
    }

    @Test
    public void join() throws URISyntaxException {
        String url = createJoinUrl();
        String body = String.format("{\"roomId\": \"%s\"}", roomId);
        TestResponseBuilder responseBuilder = new TestResponseBuilder(200).setBody(body);

        new TestRunnerPostPut<Void>(new TestRequestBuilder(url), responseBuilder).runPostTest(createRoomObject()::join);
    }

    @Test
    public void joinError404() throws URISyntaxException {
        joinExceptionExpected(404);
    }

    @Test
    public void joinError403() throws URISyntaxException {
        TestRunnerPostPut<Void> runner = new TestRunnerPostPut<>(new TestRequestBuilder(createJoinUrl()),
                new TestResponseBuilder(403));
        runner.runPostTest(createRoomObject()::join);
    }

    @Test
    public void joinError429() throws URISyntaxException {
        joinExceptionExpected(429);
    }

    private void joinExceptionExpected(int responseStatus) throws URISyntaxException {
        TestRunnerPostPut<Void> runner = new TestRunnerPostPut<>(new TestRequestBuilder(createJoinUrl()),
                new TestResponseBuilder(responseStatus));
        runner.runPostTestExceptionExpected(createRoomObject()::join);
    }

    @Test
    public void leave() throws URISyntaxException {
        String url = createLeaveUrl();
        String body = String.format("{\"roomId\": \"%s\"}", roomId);
        TestResponseBuilder responseBuilder = new TestResponseBuilder(200).setBody(body);

        new TestRunnerPostPut<Void>(new TestRequestBuilder(url), responseBuilder)
                .runPostTest(createRoomObject()::leave);
    }

    @Test
    public void leaveError404() throws URISyntaxException {
        leaveErrorWithoutException(404);
    }

    @Test
    public void leaveError403() throws URISyntaxException {
        leaveErrorWithoutException(403);
    }

    @Test
    public void leaveError429() throws URISyntaxException {
        leaveExceptionExpected(429);
    }

    private void leaveErrorWithoutException(int responseStatus) throws URISyntaxException {
        TestRunnerPostPut<Void> runner = new TestRunnerPostPut<>(new TestRequestBuilder(createLeaveUrl()),
                new TestResponseBuilder(responseStatus));
        runner.runPostTest(createRoomObject()::leave);
    }

    private void leaveExceptionExpected(int responseStatus) throws URISyntaxException {
        TestRunnerPostPut<Void> runner = new TestRunnerPostPut<>(new TestRequestBuilder(createLeaveUrl()),
                new TestResponseBuilder(responseStatus));
        runner.runPostTestExceptionExpected(createRoomObject()::leave);
    }

    @Test
    public void sendText() throws URISyntaxException {
        String url = createSendTextUrl();
        TestResponseBuilder responseBuilder = new TestResponseBuilder(200);
        TestRequestBuilder requestBuilder = new TestRequestBuilder(url)
                .setMatchingType(TestRequestBuilder.MatchingType.REGEX);

        String testText = "test text";
        new TestRunnerPostPut<String>(requestBuilder, responseBuilder).runPutTest(createRoomObject()::sendText,
                testText, sendTextVerifyBody(testText));
    }

    @Test
    public void sendTextError404() throws URISyntaxException {
        sendTextExceptionExpected(404);
    }

    @Test
    public void sendTextError403() throws URISyntaxException {
        sendTextErrorWithoutException(403);
    }

    @Test
    public void sendTextError429() throws URISyntaxException {
        sendTextExceptionExpected(429);
    }

    private void sendTextErrorWithoutException(int responseStatus) throws URISyntaxException {
        TestRequestBuilder requestBuilder = new TestRequestBuilder(createSendTextUrl())
                .setMatchingType(TestRequestBuilder.MatchingType.REGEX);
        TestRunnerPostPut<String> runner = new TestRunnerPostPut<>(requestBuilder,
                new TestResponseBuilder(responseStatus));
        String testText = "test text";
        runner.runPutTest(createRoomObject()::sendText, testText, sendTextVerifyBody(testText));
    }

    private void sendTextExceptionExpected(int responseStatus) throws URISyntaxException {
        TestRequestBuilder requestBuilder = new TestRequestBuilder(createSendTextUrl())
                .setMatchingType(TestRequestBuilder.MatchingType.REGEX);
        TestRunnerPostPut<String> runner = new TestRunnerPostPut<>(requestBuilder,
                new TestResponseBuilder(responseStatus));
        runner.runPutTestExceptionExpected(createRoomObject()::sendText, "test text");
    }

    private String sendTextVerifyBody(String testText) {
        return String.format("\"msgtype\":\"m.text\",\"body\":\"%s\"", testText);
    }

    @Test
    public void getJoinedUsers() throws URISyntaxException {
        String testuser1 = "@test:testserver.org";
        String testuser2 = "@test2:testserver.org";

        String url = createGetJoinedUsersUrl();
        String responseBody = String.format("{\"joined\": {\"%s\": \"1\", \"%s\": \"2\"}}", testuser1, testuser2);
        TestResponseBuilder responseBuilder = new TestResponseBuilder(200).setBody(responseBody);

        List<_MatrixID> expectedResult = new ArrayList<>();
        expectedResult.add(new MatrixID(testuser1));
        expectedResult.add(new MatrixID(testuser2));

        new TestRunnerGet<List<_MatrixID>>(new TestRequestBuilder(url), responseBuilder)
                .runTest(createRoomObject()::getJoinedUsers, expectedResult);
    }

    @Test
    public void getJoinedUsersError404() throws URISyntaxException {
        TestRunnerGet<List<_MatrixID>> runner = new TestRunnerGet<>(new TestRequestBuilder(createGetJoinedUsersUrl()),
                new TestResponseBuilder(404));
        runner.runTestExceptionExpected(createRoomObject()::getJoinedUsers);
    }

    @Test
    public void getJoinedUsers429() throws URISyntaxException {
        TestRunnerGet<List<_MatrixID>> runner = new TestRunnerGet<>(new TestRequestBuilder(createGetJoinedUsersUrl()),
                new TestResponseBuilder(429));
        runner.runTestExceptionExpected(createRoomObject()::getJoinedUsers);
    }

    private MatrixHttpRoom createRoomObject() throws URISyntaxException {
        MatrixClientContext context = createClientContext();
        return new MatrixHttpRoom(context, roomId);
    }

    private String createJoinUrl() {
        return String.format("/_matrix/client/r0/rooms/%s/join", roomId) + getAcessTokenParameter();
    }

    private String createLeaveUrl() {
        return String.format("/_matrix/client/r0/rooms/%s/leave", roomId) + getAcessTokenParameter();
    }

    private String createSendTextUrl() {
        return String.format("/_matrix/client/r0/rooms/%s/send/m.room.message/([0-9.]+)\\", roomId)
                + getAcessTokenParameter();
    }

    private String createGetJoinedUsersUrl() {
        return String.format("/_matrix/client/r0/rooms/%s/joined_members", roomId) + getAcessTokenParameter();
    }
}
