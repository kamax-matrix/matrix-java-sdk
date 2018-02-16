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

import io.kamax.matrix.json.GsonUtil;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static junit.framework.TestCase.assertTrue;

public class SyncDataJsonTest {

    @Test
    public void readValidJson() throws IOException {
        InputStream is = new FileInputStream("src/test/resources/json/client/syncInitial.json");
        String rawJson = IOUtils.toString(is, StandardCharsets.UTF_8);
        SyncDataJson data = new SyncDataJson(GsonUtil.parseObj(rawJson));

        assertTrue(StringUtils.equals(data.nextBatchToken(), "s70_301_72_2_36_1_1_5_1"));
        assertTrue(!data.getRooms().getInvited().isEmpty());
        assertTrue(!data.getRooms().getJoined().isEmpty());
        assertTrue(data.getRooms().getLeft().isEmpty());
    }

}
