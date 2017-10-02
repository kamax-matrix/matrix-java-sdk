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
        ErrorTestRunner<Optional<String>, Void> runner = new ErrorTestRunner(createGetNameUrl(), 403);
        runner.runGetTest(createRoomObject()::getName);
    }

    @Test
    public void getNameError429() throws URISyntaxException {
        ErrorTestRunner<Optional<String>, Void> runner = new ErrorTestRunner(createGetNameUrl(), 429);
        runner.runGetTest(createRoomObject()::getName);
    }

    private void getNameSuccessful(Optional<String> expectedResult, int responseStatus) throws URISyntaxException {
        String body = ("{`name`: `" + ROOM_NAME + "`}").replace('`', '"');
        ResponseBuilder responseBuilder = new ResponseBuilder(responseStatus).setBody(body);

        new SuccessTestRunner<Optional<String>, Void>(new RequestBuilder(createGetNameUrl()), responseBuilder)
                .runGetTest(createRoomObject()::getName, expectedResult);
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
        ErrorTestRunner<Optional<String>, Void> runner = new ErrorTestRunner(createGetTopicUrl(), 403);
        runner.runGetTest(createRoomObject()::getTopic);
    }

    @Test
    public void getTopic429() throws URISyntaxException {
        ErrorTestRunner<Optional<String>, Void> runner = new ErrorTestRunner(createGetTopicUrl(), 429);
        runner.runGetTest(createRoomObject()::getTopic);
    }

    private void getTopicSuccessful(Optional<String> expectedResult, int responseStatus) throws URISyntaxException {
        String url = createGetTopicUrl();
        String body = ("{`topic`: `" + TOPIC_NAME + "`}").replace('`', '"');
        ResponseBuilder responseBuilder = new ResponseBuilder(responseStatus).setBody(body);

        new SuccessTestRunner<Optional<String>, Void>(new RequestBuilder(url), responseBuilder)
                .runGetTest(createRoomObject()::getTopic, expectedResult);
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
}
