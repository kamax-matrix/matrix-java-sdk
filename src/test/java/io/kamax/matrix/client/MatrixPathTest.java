/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2018 Kamax Sàrl
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

import io.kamax.matrix.MatrixPath;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class MatrixPathTest {

    @Test
    public void root() {
        assertThat(MatrixPath.root().get(), is("/"));
    }

    @Test
    public void base() {
        assertThat(MatrixPath.base().get(), is("/_matrix"));
    }

    @Test
    public void client() {
        assertThat(MatrixPath.client().get(), is("/_matrix/client"));
    }

    @Test
    public void clientR0() {
        assertThat(MatrixPath.clientR0().get(), is("/_matrix/client/r0"));
    }

    @Test
    public void encodedRoomId() {
        assertThat(MatrixPath.root().add("!a:b.c:1").get(), is("/%21a%3Ab.c%3A1"));
    }

    @Test
    public void encodedUserId() {
        assertThat(MatrixPath.root().add("@a:b.c:1").get(), is("/%40a%3Ab.c%3A1"));
    }

    @Test
    public void roomMessages() {
        assertThat(MatrixPath.clientR0().add("rooms").add("!a:b.c:1").add("messages").get(),
                is("/_matrix/client/r0/rooms/%21a%3Ab.c%3A1/messages"));
    }

    @Test
    public void encodedElements() {
        assertThat(MatrixPath.root().add("!@:%+$").get(), is("/%21%40%3A%25%2B%24"));
    }

    @Test
    public void addRawElement() {
        assertThat(MatrixPath.root().put("!@:%+$").get(), is("/!@:%+$"));
    }

}
