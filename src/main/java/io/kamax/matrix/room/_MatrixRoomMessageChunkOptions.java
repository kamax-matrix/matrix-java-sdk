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

package io.kamax.matrix.room;

import java.util.Optional;

/**
 * Possible options that can be passed to the room messages chunk call.
 */
public interface _MatrixRoomMessageChunkOptions {

    /**
     * The direction to return events from.
     */
    enum Direction {

        /**
         * Fetch events backward.
         */
        Backward("b"),

        /**
         * Fetch events forward.
         */
        Forward("f");

        private String id;

        Direction(String id) {
            this.id = id;
        }

        /**
         * Get the protocol value.
         * 
         * @return the value.
         */
        public String get() {
            return id;
        }

    }

    /**
     * The token to start returning events from.
     * 
     * @return the token.
     */
    String getFromToken();

    /**
     * The token to stop returning events at, if any.
     * 
     * @return the token.
     */
    Optional<String> getToToken();

    /**
     * The direction to return events from.
     * 
     * @return the direction.
     */
    String getDirection();

    /**
     * The maximum number of events to return.
     * 
     * @return the value.
     */
    Optional<Long> getLimit();

}
