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

import io.kamax.matrix.client.MatrixHttpTest;
import io.kamax.matrix.client._SyncData;

import org.junit.platform.commons.util.StringUtils;

import static junit.framework.TestCase.assertTrue;

import static org.junit.Assert.assertNotNull;

public abstract class AMatrixHttpClientSyncTest extends MatrixHttpTest {

    private MatrixHttpClient client;

    protected void setClient(MatrixHttpClient client) {
        this.client = client;
    }

    private void validateSyncData(_SyncData data) {
        assertNotNull(data);
        assertTrue(StringUtils.isNotBlank(data.nextBatchToken()));
        assertNotNull(data.getRooms());
        assertNotNull(data.getRooms().getInvited());
        assertNotNull(data.getRooms().getJoined());
        assertNotNull(data.getRooms().getLeft());
    }

    public void getInitialSync() throws Exception {
        _SyncData data = client.sync(SyncOptions.build().get());
        validateSyncData(data);
    }

    public void getSeveralSync() throws Exception {
        _SyncData data = client.sync(SyncOptions.build().get());
        data = client.sync(SyncOptions.build().setSince(data.nextBatchToken()).setTimeout(0).get());
        validateSyncData(data);
    }

}
