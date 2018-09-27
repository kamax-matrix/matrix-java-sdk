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

package io.kamax.matrix.crypto;

import com.google.gson.JsonObject;

import io.kamax.matrix.codec.MxBase64;
import io.kamax.matrix.json.MatrixJson;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;


import net.i2p.crypto.eddsa.EdDSAEngine;

public class SignatureManager {

    private KeyManager keyMgr;
    private String domain;

    private EdDSAEngine signEngine;

    public SignatureManager(KeyManager keyMgr, String domain) {
        this.keyMgr = keyMgr;
        this.domain = domain;

        try {
            signEngine = new EdDSAEngine(MessageDigest.getInstance(keyMgr.getSpecs().getHashAlgorithm()));
            signEngine.initSign(keyMgr.getPrivateKey(keyMgr.getCurrentIndex()));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public String sign(JsonObject obj) {
        return sign(MatrixJson.encodeCanonical(obj));
    }

    public String sign(String message) {
        try {
            byte[] signRaw = signEngine.signOneShot(message.getBytes());
            return MxBase64.encode(signRaw);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        }
    }

    public JsonObject signMessageGson(String message) {
        String sign = sign(message);

        JsonObject keySignature = new JsonObject();
        // FIXME should create a signing key object what would give this ed and index values
        keySignature.addProperty("ed25519:" + keyMgr.getCurrentIndex(), sign);
        JsonObject signature = new JsonObject();
        signature.add(domain, keySignature);

        return signature;
    }

}
