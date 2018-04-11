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

package io.kamax.matrix.crypto;

import com.google.gson.JsonObject;

import io.kamax.matrix.json.GsonUtil;
import io.kamax.matrix.json.MatrixJson;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.*;
import static org.junit.Assert.assertThat;

public class SignatureManagerTest {

    private static SignatureManager signMgr;

    @BeforeClass
    public static void beforeClass() {
        signMgr = new SignatureManager(new KeyManager(new KeyMemoryStore(KeyTest.Private)), "localhost");
    }

    private void testSign(String value, String sign) {
        assertThat(signMgr.sign(value), is(equalTo(sign)));
    }

    @Test
    public void onEmptyObject() {
        String value = "{}";
        String sign = "K8280/U9SSy9IVtjBuVeLr+HpOB4BQFWbg+UZaADMtTdGYI7Geitb76LTrr5QV/7Xg4ahLwYGYZzuHGZKM5ZAQ";

        testSign(value, sign);
    }

    @Test
    public void onSimpleObject() {
        JsonObject data = new JsonObject();
        data.addProperty("one", 1);
        data.addProperty("two", "Two");

        String value = GsonUtil.get().toJson(data);
        String sign = "KqmLSbO39/Bzb0QIYE82zqLwsA+PDzYIpIRA2sRQ4sL53+sN6/fpNSoqE7BP7vBZhG6kYdD13EIMJpvhJI+6Bw";

        testSign(value, sign);
    }

    @Test
    public void onFederationHeader() {
        SignatureManager mgr = new SignatureManager(
                new KeyManager(new KeyMemoryStore("1QblgjFeL3IxoY4DKOR7p5mL5sQTC0ChmeMJlqb4d5M")),
                "synapse.local.kamax.io");

        JsonObject o = new JsonObject();
        o.addProperty("method", "GET");
        o.addProperty("uri", "/_matrix/federation/v1/query/directory?room_alias=%23a%3Amxhsd.local.kamax.io%3A8447");
        o.addProperty("origin", "synapse.local.kamax.io");
        o.addProperty("destination", "mxhsd.local.kamax.io:8447");

        String signExpected = "SEMGSOJEsoalrBfHqPO2QrSlbLaUYLHLk4e3q4IJ2JbgvCynT1onp7QF1U4Sl3G3NzybrgdnVvpqcaEgV0WPCw";
        String signProduced = mgr.sign(o);
        assertThat(signProduced, is(equalTo(signExpected)));
    }

    @Test
    public void onIdentityLookup() {
        String value = MatrixJson.encodeCanonical("{\n" + "  \"address\": \"mxisd-federation-test@kamax.io\",\n"
                + "  \"medium\": \"email\",\n" + "  \"mxid\": \"@mxisd-lookup-test:kamax.io\",\n"
                + "  \"not_after\": 253402300799000,\n" + "  \"not_before\": 0,\n" + "  \"ts\": 1523482030147\n" + "}");

        String sign = "ObKA4PNQh2g6c7Yo2QcTcuDgIwhknG7ZfqmNYzbhrbLBOqZomU22xX9raufN2Y3ke1FXsDqsGs7WBDodmzZJCg";
        testSign(value, sign);
    }

}
