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

import io.kamax.matrix.codec.MxBase64;

import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.KeyPairGenerator;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

public class KeyManager {

    public static KeyManager fromFile(String path) {
        return new KeyManager(new KeyFileStore(path));
    }

    public static KeyManager fromMemory() {
        return new KeyManager(new KeyMemoryStore());
    }

    private EdDSAParameterSpec keySpecs;
    private List<KeyPair> keys;

    public KeyManager(_KeyStore store) {
        keySpecs = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
        keys = new ArrayList<>();

        String seedBase64 = store.load().orElseGet(() -> {
            KeyPair pair = (new KeyPairGenerator()).generateKeyPair();
            String keyEncoded = getPrivateKeyBase64((EdDSAPrivateKey) pair.getPrivate());
            store.store(keyEncoded);
            return keyEncoded;
        });
        byte[] seed = Base64.getDecoder().decode(seedBase64);
        EdDSAPrivateKeySpec privKeySpec = new EdDSAPrivateKeySpec(seed, keySpecs);
        EdDSAPublicKeySpec pubKeySpec = new EdDSAPublicKeySpec(privKeySpec.getA(), keySpecs);
        keys.add(new KeyPair(new EdDSAPublicKey(pubKeySpec), new EdDSAPrivateKey(privKeySpec)));
    }

    public int getCurrentIndex() {
        return 0;
    }

    public KeyPair getKeys(int index) {
        return keys.get(index);
    }

    public EdDSAPrivateKey getPrivateKey(int index) {
        return (EdDSAPrivateKey) getKeys(index).getPrivate();
    }

    protected String getPrivateKeyBase64(EdDSAPrivateKey key) {
        return MxBase64.encode(key.getSeed());
    }

    public String getPrivateKeyBase64(int index) {
        return getPrivateKeyBase64(getPrivateKey(index));
    }

    public EdDSAPublicKey getPublicKey(int index) {
        return (EdDSAPublicKey) getKeys(index).getPublic();
    }

    public EdDSAParameterSpec getSpecs() {
        return keySpecs;
    }

    public String getPublicKeyBase64(int index) {
        return MxBase64.encode(getPublicKey(index).getAbyte());
    }

}
