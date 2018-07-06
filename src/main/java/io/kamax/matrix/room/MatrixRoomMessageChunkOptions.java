/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2018 Kamax Sarl
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

package io.kamax.matrix.room;

import java.util.Optional;

public class MatrixRoomMessageChunkOptions implements _MatrixRoomMessageChunkOptions {

    public static class Builder {

        private MatrixRoomMessageChunkOptions obj;

        public Builder() {
            this.obj = new MatrixRoomMessageChunkOptions();
        }

        public Builder setFromToken(String token) {
            obj.from = token;
            return this;
        }

        public Builder setToToken(String token) {
            obj.to = token;
            return this;
        }

        public Builder setDirection(String direction) {
            obj.dir = direction;
            return this;
        }

        public Builder setDirection(_MatrixRoomMessageChunkOptions.Direction direction) {
            return setDirection(direction.get());
        }

        public Builder setLimit(long limit) {
            obj.limit = limit;
            return this;
        }

        public MatrixRoomMessageChunkOptions get() {
            return obj;
        }

    }

    public static Builder build() {
        return new Builder();
    }

    private String from;
    private String to;
    private String dir;
    private Long limit;

    @Override
    public String getFromToken() {
        return from;
    }

    @Override
    public Optional<String> getToToken() {
        return Optional.ofNullable(to);
    }

    @Override
    public String getDirection() {
        return dir;
    }

    @Override
    public Optional<Long> getLimit() {
        return Optional.ofNullable(limit);
    }

}
