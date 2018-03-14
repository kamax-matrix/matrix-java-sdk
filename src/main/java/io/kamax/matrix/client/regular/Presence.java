/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2018 Kamax Sàrl
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

import com.google.gson.JsonObject;

import io.kamax.matrix.client._Presence;
import io.kamax.matrix.json.GsonUtil;

import java.time.Instant;

public class Presence implements _Presence {

    private String status;
    private Instant lastActive;

    public Presence(JsonObject json) {
        this.status = GsonUtil.getStringOrThrow(json, "presence");
        this.lastActive = Instant.ofEpochMilli(GsonUtil.getLong(json, "last_active_ago"));
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public Instant getLastActive() {
        return lastActive;
    }

}
