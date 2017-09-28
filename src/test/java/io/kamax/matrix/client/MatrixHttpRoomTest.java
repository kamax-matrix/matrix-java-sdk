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
import java.util.function.Supplier;

public class MatrixHttpRoomTest extends MatrixHttpTest {

    private static final String ROOM_NAME = "test room";
    private static final String ROOM_ID = "roomId892347847";
    private static final String TOPIC_NAME = "the room's topic";

    @Test
    public void getName() throws URISyntaxException {
        MatrixHttpGetTesterSuccessful tester = new MatrixHttpGetTesterSuccessful(createRoomObject()::getName,
                createGetNameUrl(), ("{`name`: `" + ROOM_NAME + "`}").replace('`', '"'), Optional.of(ROOM_NAME));
        tester.runTest();
    }

    @Test
    public void getNameError403() throws URISyntaxException {
        error403(createGetNameUrl(), createRoomObject()::getName);
    }

    @Test
    public void getNameError404() throws URISyntaxException {
        error404(createGetNameUrl(), createRoomObject()::getName);
    }

    @Test
    public void getNameError429() throws URISyntaxException {
        error429(createGetNameUrl(), createRoomObject()::getName);
    }

    @Test
    public void getTopic() throws URISyntaxException {
        MatrixHttpGetTesterSuccessful tester = new MatrixHttpGetTesterSuccessful(createRoomObject()::getTopic,
                createGetTopicUrl(), ("{`topic`: `" + TOPIC_NAME + "`}").replace('`', '"'), Optional.of(TOPIC_NAME));
        tester.runTest();
    }

    @Test
    public void getTopicError403() throws URISyntaxException {
        error403(createGetTopicUrl(), createRoomObject()::getTopic);
    }

    @Test
    public void getTopicError404() throws URISyntaxException {
        error404(createGetTopicUrl(), createRoomObject()::getTopic);
    }

    @Test
    public void getTopic429() throws URISyntaxException {
        error429(createGetTopicUrl(), createRoomObject()::getTopic);
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

    private void error403(String url, Supplier<Optional<String>> methodToTest) throws URISyntaxException {
        String errcode = "M_FORBIDDEN";
        String error = "You aren't a member of the room and weren't previously a member of the room.";

        MatrixHttpGetTesterUnsuccessful tester = new MatrixHttpGetTesterUnsuccessful(methodToTest, url, 403, errcode,
                error);
        tester.runTest();
    }

    private void error404(String url, Supplier<Optional<String>> methodToTest) throws URISyntaxException {
        MatrixHttpGetTesterSuccessful tester = new MatrixHttpGetTesterSuccessful(methodToTest, url, 404, "{}",
                Optional.empty());
        tester.runTest();
    }

    private void error429(String url, Supplier<Optional<String>> methodToTest) throws URISyntaxException {
        String errcode = "M_LIMIT_EXCEEDED";
        String error = "Too many requests have been sent in a short period of time. " + "Wait a while then try again.";

        new MatrixHttpGetTesterUnsuccessful(methodToTest, url, 429, errcode, error).runTest();
    }

    // TODO join, leave, sendText, sendNotice, invite, getJoinedUsers
}
