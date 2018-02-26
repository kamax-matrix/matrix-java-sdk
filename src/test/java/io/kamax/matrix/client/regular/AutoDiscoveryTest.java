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

import io.kamax.matrix.client._MatrixClient;
import io.kamax.matrix.hs._MatrixHomeserver;

import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertTrue;

public class AutoDiscoveryTest {

    private final String domain = "kamax.io";
    private final String autoUrl = "https://" + domain;

    @Test
    public void AutoDiscoveryTest() {
        _MatrixClient client = new MatrixHttpClient(domain);

        client.discoverSettings();
        _MatrixHomeserver hs = client.getHomeserver();
        assertTrue(domain.equals(hs.getDomain()));
        assertTrue(autoUrl.equals(hs.getBaseEndpoint().toString()));
    }

}
