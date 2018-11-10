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

package io.kamax.matrix.client;

import io.kamax.matrix.json.Identifier;

public class MatrixIdentifierCredentials implements MatrixCredentials {

    private final Identifier identifier;
    private final String password;
    private String initialDeviceDisplayName;

    public MatrixIdentifierCredentials(String password, String type, String address, String medium) {
        this.password = password;
        this.identifier = new Identifier();
        this.identifier.setType(type);
        this.identifier.setMedium(medium);
        this.identifier.setAddress(address);
    }
    public MatrixIdentifierCredentials(String password, String type, String address, String medium, String initialDeviceDisplayName) {
        this(password, type, address, medium);
        this.initialDeviceDisplayName = initialDeviceDisplayName;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public String getPassword() {
        return password;
    }

    public String getInitialDeviceDisplayName() {
        return initialDeviceDisplayName;
    }

}
