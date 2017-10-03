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

import org.junit.Test;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MatrixHttpRoomTest extends MatrixHttpTest {
    private static final String ROOM_ID = "roomId892347847";

    @Test
    public void getName() throws URISyntaxException {
        String nameOfRoom = "test room";
        getNameSuccessful(Optional.of(nameOfRoom), 200, nameOfRoom);
    }

    @Test
    public void getName404() throws URISyntaxException {
        String nameOfRoom = "test room";
        getNameSuccessful(Optional.empty(), 404, nameOfRoom);
    }

    @Test
    public void getNameError403() throws URISyntaxException {
        TestRunnerGet<Optional<String>> runner = new TestRunnerGet<>(new TestRequestBuilder(createGetNameUrl()),
                new TestResponseBuilder(403));
        runner.runTestExceptionExpected(createRoomObject()::getName);
    }

    @Test
    public void getNameError429() throws URISyntaxException {
        TestRunnerGet<Optional<String>> runner = new TestRunnerGet<>(new TestRequestBuilder(createGetNameUrl()),
                new TestResponseBuilder(429));
        runner.runTestExceptionExpected(createRoomObject()::getName);
    }

    private void getNameSuccessful(Optional<String> expectedResult, int responseStatus, String nameOfRoom)
            throws URISyntaxException {
        String body = String.format("{\"name\": \"%s\"}", nameOfRoom);
        TestResponseBuilder responseBuilder = new TestResponseBuilder(responseStatus).setBody(body);

        new TestRunnerGet<Optional<String>>(new TestRequestBuilder(createGetNameUrl()), responseBuilder)
                .runTest(createRoomObject()::getName, expectedResult);
    }

    @Test
    public void getTopic() throws URISyntaxException {
        String topic = "test topic";
        getTopicSuccessful(Optional.of(topic), 200, topic);
    }

    @Test
    public void getTopic404() throws URISyntaxException {
        String topic = "test topic";
        getTopicSuccessful(Optional.empty(), 404, topic);
    }

    @Test
    public void getTopicError403() throws URISyntaxException {
        TestRunnerGet<Optional<String>> runner = new TestRunnerGet<>(new TestRequestBuilder(createGetTopicUrl()),
                new TestResponseBuilder(403));
        runner.runTestExceptionExpected(createRoomObject()::getTopic);
    }

    @Test
    public void getTopic429() throws URISyntaxException {
        TestRunnerGet<Optional<String>> runner = new TestRunnerGet<>(new TestRequestBuilder(createGetTopicUrl()),
                new TestResponseBuilder(429));
        runner.runTestExceptionExpected(createRoomObject()::getTopic);
    }

    private void getTopicSuccessful(Optional<String> expectedResult, int responseStatus, String topic)
            throws URISyntaxException {
        String url = createGetTopicUrl();
        String body = String.format("{\"topic\": \"%s\"}", topic);
        TestResponseBuilder responseBuilder = new TestResponseBuilder(responseStatus).setBody(body);

        new TestRunnerGet<Optional<String>>(new TestRequestBuilder(url), responseBuilder)
                .runTest(createRoomObject()::getTopic, expectedResult);
    }

    @Test
    public void join() throws URISyntaxException {
        String url = createJoinUrl();
        String body = String.format("{\"roomId\": \"%s\"}", ROOM_ID);
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
        String body = String.format("{\"roomId\": \"%s\"}", ROOM_ID);
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
        return new MatrixHttpRoom(context, ROOM_ID);
    }

    private String createGetNameUrl() {
        return String.format("/_matrix/client/r0/rooms/%s/state/m.room.name", ROOM_ID) + getAcessTokenParameter();
    }

    private String createGetTopicUrl() {
        return String.format("/_matrix/client/r0/rooms/%s/state/m.room.topic", ROOM_ID) + getAcessTokenParameter();
    }

    private String createJoinUrl() {
        return String.format("/_matrix/client/r0/rooms/%s/join", ROOM_ID) + getAcessTokenParameter();
    }

    private String createLeaveUrl() {
        return String.format("/_matrix/client/r0/rooms/%s/leave", ROOM_ID) + getAcessTokenParameter();
    }

    private String createSendTextUrl() {
        return String.format("/_matrix/client/r0/rooms/%s/send/m.room.message/([0-9.]+)\\", ROOM_ID)
                + getAcessTokenParameter();
    }

    private String createGetJoinedUsersUrl() {
        return String.format("/_matrix/client/r0/rooms/%s/joined_members", ROOM_ID) + getAcessTokenParameter();
    }
}
