/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2018 Arne Augenstein
 *
 * https://www.kamax.io/
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

package io.kamax.matrix.json;

import io.kamax.matrix.event._MatrixEvent;
import io.kamax.matrix.json.event.MatrixJsonRoomPowerLevelsEvent;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RoomPowerLevelsEventParsingTest {
    private String getJson() {
        return getJson("src/test/resources/json/powerLevels.json");
    }

    private String getJsonEmpty() {
        return getJson("src/test/resources/json/powerLevelsEmpty.json");
    }

    private String getJson(String jsonFile) {
        try {
            InputStream is = new FileInputStream(jsonFile);
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void parsePowerLevelEvent() {
        _MatrixEvent event = MatrixJsonEventFactory.get(GsonUtil.parseObj(getJson()));

        assertThat(event, instanceOf(MatrixJsonRoomPowerLevelsEvent.class));

        MatrixJsonRoomPowerLevelsEvent powerLevelsEvent = (MatrixJsonRoomPowerLevelsEvent) event;

        assertThat(powerLevelsEvent.getEventsDefault().get(), is(0.0));
        assertThat(powerLevelsEvent.getStateDefault().get(), is(50.0));
        assertThat(powerLevelsEvent.getRedact().get(), is(40.0));
        assertThat(powerLevelsEvent.getBan().get(), is(30.0));
        assertThat(powerLevelsEvent.getUsersDefault().get(), is(10.0));
        assertThat(powerLevelsEvent.getKick().get(), is(55.0));
        assertThat(powerLevelsEvent.getInvite().get(), is(5.0));

        Map<String, Double> users = powerLevelsEvent.getUsers();
        assertThat(users.size(), is(1));
        assertThat(users.get("@testuser1:testmatrix.localtoast.de"), is(99.0));

        Map<String, Double> events = powerLevelsEvent.getEvents();
        assertThat(events.size(), is(5));
        assertThat(events.get("m.room.avatar"), is(60.0));
        assertThat(events.get("m.room.name"), is(70.0));
        assertThat(events.get("m.room.canonical_alias"), is(80.0));
        assertThat(events.get("m.room.history_visibility"), is(90.0));
        assertThat(events.get("m.room.power_levels"), is(95.0));

    }

    @Test
    public void parseEmptyPowerLevelEvent() {
        _MatrixEvent event = MatrixJsonEventFactory.get(GsonUtil.parseObj(getJsonEmpty()));

        assertThat(event, instanceOf(MatrixJsonRoomPowerLevelsEvent.class));

        MatrixJsonRoomPowerLevelsEvent powerLevelsEvent = (MatrixJsonRoomPowerLevelsEvent) event;

        assertThat(powerLevelsEvent.getEventsDefault().isPresent(), is(false));
        assertThat(powerLevelsEvent.getStateDefault().isPresent(), is(false));
        assertThat(powerLevelsEvent.getRedact().isPresent(), is(false));
        assertThat(powerLevelsEvent.getBan().isPresent(), is(false));
        assertThat(powerLevelsEvent.getUsersDefault().isPresent(), is(false));
        assertThat(powerLevelsEvent.getKick().isPresent(), is(false));
        assertThat(powerLevelsEvent.getInvite().isPresent(), is(false));

        Map<String, Double> users = powerLevelsEvent.getUsers();
        assertThat(users.size(), is(0));

        Map<String, Double> events = powerLevelsEvent.getEvents();
        assertThat(events.size(), is(0));
    }

}
