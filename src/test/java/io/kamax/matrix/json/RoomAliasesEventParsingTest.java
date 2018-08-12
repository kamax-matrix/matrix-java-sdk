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
import io.kamax.matrix.json.event.MatrixJsonRoomAliasesEvent;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RoomAliasesEventParsingTest {
    private String getJson() {
        return getJson("src/test/resources/json/aliases.json");
    }

    private String getJsonEmpty() {
        return getJson("src/test/resources/json/aliasesEmpty.json");
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
    public void parseAliasesEvent() {
        _MatrixEvent event = MatrixJsonEventFactory.get(GsonUtil.parseObj(getJson()));

        assertThat(event, instanceOf(MatrixJsonRoomAliasesEvent.class));

        MatrixJsonRoomAliasesEvent aliasesEvent = (MatrixJsonRoomAliasesEvent) event;

        List<String> aliases = aliasesEvent.getAliases();
        assertThat(aliases.size(), is(2));
        assertThat(aliases.contains("#test1:testmatrix.localtoast.de"), is(true));
        assertThat(aliases.contains("#test2:testmatrix.localtoast.de"), is(true));
    }

    @Test
    public void parseEmptyAliasesEvent() {
        _MatrixEvent event = MatrixJsonEventFactory.get(GsonUtil.parseObj(getJsonEmpty()));

        assertThat(event, instanceOf(MatrixJsonRoomAliasesEvent.class));

        MatrixJsonRoomAliasesEvent aliasesEvent = (MatrixJsonRoomAliasesEvent) event;

        List<String> aliases = aliasesEvent.getAliases();
        assertThat(aliases.size(), is(0));
    }

}
