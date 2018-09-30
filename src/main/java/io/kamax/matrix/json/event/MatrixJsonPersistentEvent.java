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

package io.kamax.matrix.json.event;

import com.google.gson.JsonObject;

import io.kamax.matrix.MatrixID;
import io.kamax.matrix._MatrixID;
import io.kamax.matrix.event._MatrixPersistentEvent;
import io.kamax.matrix.json.MatrixJsonObject;

public class MatrixJsonPersistentEvent extends MatrixJsonObject implements _MatrixPersistentEvent {

    private String id;
    private String type;
    private Long time;
    private int age;
    private _MatrixID sender;

    public MatrixJsonPersistentEvent(JsonObject obj) {
        super(obj);

        id = getString("event_id");
        type = getString("type");
        time = getLong("origin_server_ts");
        age = getInt("age", -1);
        sender = MatrixID.asAcceptable(getString("sender"));
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Long getTime() {
        return time;
    }

    @Override
    public _MatrixID getSender() {
        return sender;
    }

}
