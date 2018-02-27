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

import io.kamax.matrix.client.MatrixClientContext;
import io.kamax.matrix.client._AutoDiscoverySettings;
import io.kamax.matrix.client._MatrixClient;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static junit.framework.TestCase.assertTrue;

public class AutoDiscoveryTest {

    /*
     * FIXME
     * we shouldn't hardcode this, but since auto-discover use TCP 443, we might create a conflict with an existing
     * process.
     * So until we find a better solution...
     */
    private final String domain = "kamax.io";
    private final String autoUrl = "https://" + domain;

    @Test
    public void kamaxAutoDiscover() {
        _MatrixClient client = new MatrixHttpClient(domain);

        Optional<_AutoDiscoverySettings> opt = client.discoverSettings();
        assertTrue(opt.isPresent());

        assertTrue(opt.get().getHsBaseUrls().size() == 1);
        assertTrue(autoUrl.equals(opt.get().getHsBaseUrls().get(0).toString()));
        assertTrue(opt.get().getIsBaseUrls().size() == 1);
        assertTrue(autoUrl.equals(opt.get().getIsBaseUrls().get(0).toString()));

        MatrixClientContext context = client.getContext();
        assertTrue(domain.equals(context.getDomain()));
        assertTrue(autoUrl.equals(context.getHsBaseUrl().toString()));
        assertTrue(autoUrl.equals(context.getIsBaseUrl().toString()));
    }

    @Test
    public void noDataAutoDiscover() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            _MatrixClient client = new MatrixHttpClient("example.org");
            client.discoverSettings();
        });
    }

    // TODO test cases if the HS URL is set, and we call auto-discovery
    // Add Two test cases: one with data (HS URL is changed), one without (HS URL remains).

}
