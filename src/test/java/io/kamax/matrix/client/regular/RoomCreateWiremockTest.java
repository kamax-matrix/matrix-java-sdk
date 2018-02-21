/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2018 Maxime Dor
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

package io.kamax.matrix.client.regular;

import com.google.gson.JsonObject;

import io.kamax.matrix.MatrixID;
import io.kamax.matrix.client.MatrixClientContext;
import io.kamax.matrix.client.MatrixHttpTest;
import io.kamax.matrix.hs.MatrixHomeserver;
import io.kamax.matrix.hs._MatrixRoom;
import io.kamax.matrix.json.GsonUtil;
import io.kamax.matrix.room.RoomCreationOptions;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static junit.framework.TestCase.assertTrue;

public class RoomCreateWiremockTest extends MatrixHttpTest {

    private final String syncPath = "/_matrix/client/r0/createRoom";

    @Test
    public void createRoomWithNoOption() throws Exception {
        String roomId = "!roomId:" + domain;
        JsonObject resBody = new JsonObject();
        resBody.addProperty("room_id", roomId);
        String resBodyRaw = GsonUtil.get().toJson(resBody);
        stubFor(post(urlPathEqualTo("/_matrix/client/r0/createRoom"))
                .willReturn(aResponse().withStatus(200).withBody(resBodyRaw)));

        MatrixHomeserver hs = new MatrixHomeserver(domain, baseUrl);
        MatrixClientContext context = new MatrixClientContext(hs, MatrixID.asValid("@user:localhost"), "test");
        MatrixHttpClient client = new MatrixHttpClient(context);

        _MatrixRoom room = client.createRoom(RoomCreationOptions.none());
        assertTrue(StringUtils.equals(room.getAddress(), roomId));

        verify(postRequestedFor(urlPathEqualTo(syncPath)));
    }

}
