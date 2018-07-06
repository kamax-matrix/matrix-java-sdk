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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class KeyFileStore implements _KeyStore {

    private final Charset charset = StandardCharsets.UTF_8;

    private File file;

    public KeyFileStore(String path) {
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException("Signing key file storage " + path + " does not exist");
        }

        if (file.isDirectory()) {
            throw new IllegalArgumentException("Signing key file storage " + path + " is a directory");
        }

        if (!file.isFile()) {
            throw new IllegalArgumentException("Signing key file storage " + path + " is not a regular file");
        }

        if (!file.canRead()) {
            throw new IllegalArgumentException("Signing key file storage " + path + " is not readable");
        }

        this.file = file;
    }

    @Override
    public Optional<String> load() {
        try {
            List<String> keys = FileUtils.readLines(file, charset);
            return keys.stream().filter(StringUtils::isNotBlank).findFirst();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void store(String key) {
        try {
            FileUtils.writeLines(file, charset.name(), Collections.singletonList(key), false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
