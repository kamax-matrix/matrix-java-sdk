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

package io.kamax.matrix.json;

import com.google.gson.JsonObject;

import io.kamax.matrix.MatrixID;
import io.kamax.matrix._MatrixID;
import io.kamax.matrix.event.EventKey;
import io.kamax.matrix.event._RoomCanonicalAliasEvent;
import io.kamax.matrix.json.event.MatrixJsonRoomCanonicalAliasEvent;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

import static org.junit.Assert.assertFalse;

public class MatrixJsonRoomCanonicalAliasEventTest {

    private final _MatrixID userId = MatrixID.from("user", "example.org").valid();

    private MatrixJsonRoomCanonicalAliasEvent makeEvent(JsonObject content) {
        JsonObject obj = new JsonObject();
        obj.addProperty(EventKey.Type.get(), _RoomCanonicalAliasEvent.Type);
        obj.addProperty(EventKey.Timestamp.get(), System.currentTimeMillis());
        obj.addProperty(EventKey.Sender.get(), userId.getId());
        obj.add(EventKey.Content.get(), content);
        return new MatrixJsonRoomCanonicalAliasEvent(obj);
    }

    @Test
    public void contentEmpty() {
        MatrixJsonRoomCanonicalAliasEvent event = makeEvent(new JsonObject());
        assertFalse(event.hasAlias());
        assertTrue(event.getAlias().isEmpty());
    }

    @Test
    public void valueNull() {
        MatrixJsonRoomCanonicalAliasEvent event = makeEvent(GsonUtil.makeObj("alias", null));
        assertFalse(event.hasAlias());
        assertTrue(event.getAlias().isEmpty());
    }

    @Test
    public void valueEmpty() {
        MatrixJsonRoomCanonicalAliasEvent event = makeEvent(GsonUtil.makeObj("alias", ""));
        assertFalse(event.hasAlias());
        assertTrue(event.getAlias().isEmpty());
    }

    @Test
    public void valuePresent() {
        MatrixJsonRoomCanonicalAliasEvent event = makeEvent(GsonUtil.makeObj("alias", "#alias:domain.tld"));
        assertTrue(event.hasAlias());
        assertTrue(event.getAlias().isPresent());
    }

    @Test(expected = InvalidJsonException.class)
    public void nullObject() {
        new MatrixJsonRoomCanonicalAliasEvent(null);
    }

    @Test(expected = InvalidJsonException.class)
    public void emptyObject() {
        new MatrixJsonRoomCanonicalAliasEvent(new JsonObject());
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidType() {
        JsonObject obj = new JsonObject();
        obj.addProperty(EventKey.Type.get(), "m");
        obj.addProperty(EventKey.Timestamp.get(), System.currentTimeMillis());
        obj.addProperty(EventKey.Sender.get(), userId.getId());
        obj.add(EventKey.Content.get(), new JsonObject());
        new MatrixJsonRoomCanonicalAliasEvent(obj);
    }

}
