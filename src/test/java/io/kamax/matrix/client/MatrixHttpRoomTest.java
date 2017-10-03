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

import org.junit.Test;

import java.net.URISyntaxException;
import java.util.Optional;

public class MatrixHttpRoomTest extends MatrixHttpTest {
    // TODO join, leave, sendText, sendNotice, invite, getJoinedUsers

    private static final String ROOM_NAME = "test room";
    private static final String ROOM_ID = "roomId892347847";
    private static final String TOPIC_NAME = "the room's topic";

    @Test
    public void getName() throws URISyntaxException {
        getNameSuccessful(Optional.of(ROOM_NAME), 200);
    }

    @Test
    public void getName404() throws URISyntaxException {
        getNameSuccessful(Optional.empty(), 404);
    }

    @Test
    public void getNameError403() throws URISyntaxException {
        TestRunnerGet<Optional<String>> runner = new TestRunnerGet<>(new RequestBuilder(createGetNameUrl()),
                new ResponseBuilder(403));
        runner.runTestExceptionExpected(createRoomObject()::getName);
    }

    @Test
    public void getNameError429() throws URISyntaxException {
        TestRunnerGet<Optional<String>> runner = new TestRunnerGet<>(new RequestBuilder(createGetNameUrl()),
                new ResponseBuilder(429));
        runner.runTestExceptionExpected(createRoomObject()::getName);
    }

    private void getNameSuccessful(Optional<String> expectedResult, int responseStatus) throws URISyntaxException {
        String body = ("{`name`: `" + ROOM_NAME + "`}").replace('`', '"');
        ResponseBuilder responseBuilder = new ResponseBuilder(responseStatus).setBody(body);

        new TestRunnerGet<Optional<String>>(new RequestBuilder(createGetNameUrl()), responseBuilder)
                .runTest(createRoomObject()::getName, expectedResult);
    }

    @Test
    public void getTopic() throws URISyntaxException {
        getTopicSuccessful(Optional.of(TOPIC_NAME), 200);
    }

    @Test
    public void getTopic404() throws URISyntaxException {
        getTopicSuccessful(Optional.empty(), 404);
    }

    @Test
    public void getTopicError403() throws URISyntaxException {
        TestRunnerGet<Optional<String>> runner = new TestRunnerGet<>(new RequestBuilder(createGetTopicUrl()),
                new ResponseBuilder(403));
        runner.runTestExceptionExpected(createRoomObject()::getTopic);
    }

    @Test
    public void getTopic429() throws URISyntaxException {
        TestRunnerGet<Optional<String>> runner = new TestRunnerGet<>(new RequestBuilder(createGetTopicUrl()),
                new ResponseBuilder(429));
        runner.runTestExceptionExpected(createRoomObject()::getTopic);
    }

    private void getTopicSuccessful(Optional<String> expectedResult, int responseStatus) throws URISyntaxException {
        String url = createGetTopicUrl();
        String body = ("{`topic`: `" + TOPIC_NAME + "`}").replace('`', '"');
        ResponseBuilder responseBuilder = new ResponseBuilder(responseStatus).setBody(body);

        new TestRunnerGet<Optional<String>>(new RequestBuilder(url), responseBuilder)
                .runTest(createRoomObject()::getTopic, expectedResult);
    }

    @Test
    public void join() throws URISyntaxException {
        String url = createJoinUrl();
        String body = ("{`roomId`: `" + ROOM_ID + "`}").replace('`', '"');
        ResponseBuilder responseBuilder = new ResponseBuilder(200).setBody(body);

        new TestRunnerPostPut<Void>(new RequestBuilder(url), responseBuilder).runPostTest(createRoomObject()::join);
    }

    @Test
    public void joinError404() throws URISyntaxException {
        joinExceptionExpected(404);
    }

    @Test
    public void joinError403() throws URISyntaxException {
        TestRunnerPostPut<Void> runner = new TestRunnerPostPut<>(new RequestBuilder(createJoinUrl()),
                new ResponseBuilder(403));
        runner.runPostTest(createRoomObject()::join);
    }

    @Test
    public void joinError429() throws URISyntaxException {
        joinExceptionExpected(429);
    }

    private void joinExceptionExpected(int responseStatus) throws URISyntaxException {
        TestRunnerPostPut<Void> runner = new TestRunnerPostPut<>(new RequestBuilder(createJoinUrl()),
                new ResponseBuilder(responseStatus));
        runner.runPostTestExceptionExpected(createRoomObject()::join);
    }

    @Test
    public void leave() throws URISyntaxException {
        String url = createLeaveUrl();
        String body = ("{`roomId`: `" + ROOM_ID + "`}").replace('`', '"');
        ResponseBuilder responseBuilder = new ResponseBuilder(200).setBody(body);

        new TestRunnerPostPut<Void>(new RequestBuilder(url), responseBuilder).runPostTest(createRoomObject()::leave);
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
        TestRunnerPostPut<Void> runner = new TestRunnerPostPut<>(new RequestBuilder(createLeaveUrl()),
                new ResponseBuilder(responseStatus));
        runner.runPostTest(createRoomObject()::leave);
    }

    private void leaveExceptionExpected(int responseStatus) throws URISyntaxException {
        TestRunnerPostPut<Void> runner = new TestRunnerPostPut<>(new RequestBuilder(createLeaveUrl()),
                new ResponseBuilder(responseStatus));
        runner.runPostTestExceptionExpected(createRoomObject()::leave);
    }

    @Test
    public void sendText() throws URISyntaxException {
        String url = createSendTextUrl();
        ResponseBuilder responseBuilder = new ResponseBuilder(200);
        RequestBuilder requestBuilder = new RequestBuilder(url).setMatchingType(RequestBuilder.MatchingType.REGEX);

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
        RequestBuilder requestBuilder = new RequestBuilder(createSendTextUrl())
                .setMatchingType(RequestBuilder.MatchingType.REGEX);
        TestRunnerPostPut<String> runner = new TestRunnerPostPut<>(requestBuilder, new ResponseBuilder(responseStatus));
        String testText = "test text";
        runner.runPutTest(createRoomObject()::sendText, testText, sendTextVerifyBody(testText));
    }

    private void sendTextExceptionExpected(int responseStatus) throws URISyntaxException {
        RequestBuilder requestBuilder = new RequestBuilder(createSendTextUrl())
                .setMatchingType(RequestBuilder.MatchingType.REGEX);
        TestRunnerPostPut<String> runner = new TestRunnerPostPut<>(requestBuilder, new ResponseBuilder(responseStatus));
        runner.runPutTestExceptionExpected(createRoomObject()::sendText, "test text");
    }

    private String sendTextVerifyBody(String testText) {
        return ("`msgtype`:`m.text`,`body`:`" + testText + "`").replace('`', '"');
    }

    private MatrixHttpRoom createRoomObject() throws URISyntaxException {
        MatrixClientContext context = createClientContext();
        return new MatrixHttpRoom(context, ROOM_ID);
    }

    private String createGetNameUrl() {
        return "/_matrix/client/r0/rooms/" + ROOM_ID + "/state/m.room.name" + getAcessTokenParameter();
    }

    private String createGetTopicUrl() {
        return "/_matrix/client/r0/rooms/" + ROOM_ID + "/state/m.room.topic" + getAcessTokenParameter();
    }

    private String createJoinUrl() {
        return "/_matrix/client/r0/rooms/" + ROOM_ID + "/join" + getAcessTokenParameter();
    }

    private String createLeaveUrl() {
        return "/_matrix/client/r0/rooms/" + ROOM_ID + "/leave" + getAcessTokenParameter();
    }

    private String createSendTextUrl() {
        return "/_matrix/client/r0/rooms/" + ROOM_ID + "/send/m.room.message/([0-9.]+)\\" + getAcessTokenParameter();
    }
}
