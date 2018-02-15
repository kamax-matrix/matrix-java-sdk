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

package io.kamax.matrix.client;

import java.util.Optional;

/**
 * Possible options that can be passed to the sync call.
 */
public interface _SyncOptions {

    /**
     * The point of time to continue the sync from.
     * 
     * @return A token that was provided in a previous sync call, if set.
     */
    Optional<String> getSince();

    /**
     * The filter to use for the sync.
     * 
     * @return The ID or raw JSON filter, if set.
     */
    Optional<String> getFilter();

    /**
     * If the full state should be included in the sync.
     * 
     * @return The full state option, if set.
     */
    Optional<Boolean> withFullState();

    /**
     * If the client should automatically be marked as online.
     * 
     * @return The set presence option, if set.
     */
    Optional<String> getSetPresence();

    /**
     * The maximum time to wait, in milliseconds, before ending the sync call.
     * 
     * @return The timeout option, if set.
     */
    Optional<Long> getTimeout();

}
