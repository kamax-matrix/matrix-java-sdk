/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2018 Kamax Sarl
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

import org.junit.Test;

import java.nio.file.FileSystems;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class KeyManagerTest {

    private void testLoad(KeyManager mgr) {
        assertThat(mgr.getCurrentIndex(), is(equalTo(0)));
        assertThat(mgr.getPrivateKeyBase64(0), is(equalTo(KeyTest.Private)));
        assertThat(mgr.getPublicKeyBase64(0), is(equalTo(KeyTest.Public)));
    }

    @Test
    public void keyGeneration() {
        KeyManager mgr = KeyManager.fromMemory();
        assertThat(mgr.getCurrentIndex(), is(equalTo(0)));
        assertThat(mgr.getPrivateKeyBase64(0).length(), is(equalTo(43)));
        assertThat(mgr.getPublicKeyBase64(0).length(), is(equalTo(43)));
    }

    @Test
    public void loadKeyFromFile() {
        testLoad(KeyManager.fromFile("src/test/resources/crypto/seed"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void loadKeyFromInexistantFile() {
        KeyManager.fromFile("ThisFileDoesNotExists.yarly");
    }

    @Test(expected = IllegalArgumentException.class)
    public void loadKeyFromDirectory() {
        KeyManager.fromFile(FileSystems.getDefault().getRootDirectories().iterator().next().toFile().getAbsolutePath());
    }

    @Test
    public void loadKeyFromMemory() {
        testLoad(new KeyManager(new KeyMemoryStore(KeyTest.Private)));
    }

}
