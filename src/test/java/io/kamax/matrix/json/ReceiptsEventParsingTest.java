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
 *
 */

package io.kamax.matrix.json;

import io.kamax.matrix.MatrixID;
import io.kamax.matrix.event._MatrixEvent;
import io.kamax.matrix.json.event.MatrixJsonReadReceiptEvent;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ReceiptsEventParsingTest {
    private String getJson() {
        try {
            InputStream is = new FileInputStream("src/test/resources/json/receipts.json");
            return IOUtils.toString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void parseReceiptEvent() {
        _MatrixEvent event = MatrixJsonEventFactory.get(GsonUtil.parseObj(getJson()));

        assertThat(event, instanceOf(MatrixJsonReadReceiptEvent.class));

        List<MatrixJsonReadReceiptEvent.Receipt> receipts = ((MatrixJsonReadReceiptEvent) event).getReceipts();

        Map<String, Map<MatrixID, Instant>> receiptsMap = receipts.stream()
                .collect(Collectors.toMap(it -> it.getEventId(), it -> it.getUsersWithTimestamp()));

        String id1 = "$15296836321000niTzS:matrix.localtoast.de";
        String id2 = "$1520971505455BCXJZ:matrix.localtoast.de";

        Set<String> ids = receiptsMap.keySet();
        assertThat(ids.size(), is(2));
        assertTrue(ids.contains(id1));
        assertTrue(ids.contains(id2));

        MatrixID matrixId1a = MatrixID.asAcceptable("@caldavtestuser:matrix.localtoast.de");
        MatrixID matrixId1b = MatrixID.asAcceptable("@arne:matrix.localtoast.de");
        MatrixID matrixId2 = MatrixID.asAcceptable("@arne:testmatrix.localtoast.de");

        Instant instant1a = Instant.ofEpochMilli(1529683638046L);
        Instant instant1b = Instant.ofEpochMilli(1531668885573L);
        Instant instant2 = Instant.ofEpochMilli(1520971518671L);

        assertThat(receiptsMap.get(id1).get(matrixId1a), is(instant1a));
        assertThat(receiptsMap.get(id1).get(matrixId1b), is(instant1b));
        assertThat(receiptsMap.get(id2).get(matrixId2), is(instant2));
    }

}
