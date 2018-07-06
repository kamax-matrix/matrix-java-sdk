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

package io.kamax.matrix.client.regular;

import io.kamax.matrix.client._SyncOptions;

import java.util.Optional;

public class SyncOptions implements _SyncOptions {

    public static class Builder {

        private final SyncOptions obj;

        public Builder() {
            this.obj = new SyncOptions();
        }

        public SyncOptions get() {
            return obj;
        }

        public Builder setSince(String since) {
            obj.since = since;
            return this;
        }

        public Builder setFilter(String filter) {
            obj.filter = filter;
            return this;
        }

        public Builder setWithFullState(boolean withFullState) {
            obj.fullState = withFullState;
            return this;
        }

        public Builder setPresence(boolean setPresence) {
            obj.setPresence = setPresence ? null : "offline";
            return this;
        }

        public Builder setTimeout(long timeout) {
            obj.timeout = timeout;
            return this;
        }

    }

    public static Builder build() {
        return new Builder();
    }

    private String since;
    private String filter;
    private Boolean fullState;
    private String setPresence;
    private Long timeout;

    @Override
    public Optional<String> getSince() {
        return Optional.ofNullable(since);
    }

    @Override
    public Optional<String> getFilter() {
        return Optional.ofNullable(filter);
    }

    @Override
    public Optional<Boolean> withFullState() {
        return Optional.ofNullable(fullState);
    }

    @Override
    public Optional<String> getSetPresence() {
        return Optional.ofNullable(setPresence);
    }

    @Override
    public Optional<Long> getTimeout() {
        return Optional.ofNullable(timeout);
    }

}
