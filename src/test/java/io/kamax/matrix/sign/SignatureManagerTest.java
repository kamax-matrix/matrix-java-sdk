/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Maxime Dor
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

package io.kamax.matrix.sign;

import com.google.gson.JsonObject;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.*;
import static org.junit.Assert.assertThat;

public class SignatureManagerTest {

    private static SignatureManager signMgr;

    @BeforeClass
    public static void beforeClass() {
        signMgr = new SignatureManager(
                new KeyManager(new KeyMemoryStore("YJDBA9Xnr2sVqXD9Vj7XVUnmFZcZrlw8Md7kMW+3XA1")), "domain");
    }

    @Test
    public void emptyObject() {
        assertThat(signMgr.sign("{}"),
                is(equalTo("K8280/U9SSy9IVtjBuVeLr+HpOB4BQFWbg+UZaADMtTdGYI7Geitb76LTrr5QV/7Xg4ahLwYGYZzuHGZKM5ZAQ")));
    }

    @Test
    public void simpleObject() {
        assertThat(signMgr.sign("{\"one\":1,\"two\":\"Two\"}"),
                is(equalTo("KqmLSbO39/Bzb0QIYE82zqLwsA+PDzYIpIRA2sRQ4sL53+sN6/fpNSoqE7BP7vBZhG6kYdD13EIMJpvhJI+6Bw")));
    }

    @Test
    public void test() {
        SignatureManager mgr = new SignatureManager(
                new KeyManager(new KeyMemoryStore("1QblgjFeL3IxoY4DKOR7p5mL5sQTC0ChmeMJlqb4d5M")),
                "synapse.local.kamax.io");
        JsonObject o = new JsonObject();
        o.addProperty("method", "GET");
        o.addProperty("uri", "/_matrix/federation/v1/query/directory?room_alias=%23a%3Amxhsd.local.kamax.io%3A8447");
        o.addProperty("origin", "synapse.local.kamax.io");
        o.addProperty("destination", "mxhsd.local.kamax.io:8447");
        String signature = mgr.sign(o);
        assertThat(signature,
                is(equalTo("SEMGSOJEsoalrBfHqPO2QrSlbLaUYLHLk4e3q4IJ2JbgvCynT1onp7QF1U4Sl3G3NzybrgdnVvpqcaEgV0WPCw")));
    }

}
