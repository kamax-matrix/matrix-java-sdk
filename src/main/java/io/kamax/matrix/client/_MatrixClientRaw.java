/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Kamax Sarl
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

package io.kamax.matrix.client;

import io.kamax.matrix._MatrixID;
import io.kamax.matrix.hs._MatrixHomeserver;

import java.util.List;
import java.util.Optional;

public interface _MatrixClientRaw {

    MatrixClientContext getContext();

    _MatrixHomeserver getHomeserver();

    Optional<String> getAccessToken();

    Optional<_MatrixID> getUser();

    Optional<_AutoDiscoverySettings> discoverSettings();

    // FIXME
    // we should maybe have a dedicated object for HS related items and be merged into getHomeserver() which is only
    // holding state at this point and is not functional
    List<String> getHomeApiVersions();

    // FIXME
    // we should maybe have a dedicated object for IS related items. Will reconsider when implementing
    // other part of the IS API
    boolean validateIsBaseUrl();

}
