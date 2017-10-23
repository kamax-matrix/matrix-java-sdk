/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Maxime Dor
 *
 * https://max.kamax.io/
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

import io.kamax.matrix._MatrixID;
import io.kamax.matrix.hs._MatrixHomeserver;

import java.util.Optional;

public class MatrixClientContext {

    private _MatrixHomeserver hs;
    private _MatrixID user;
    private String token;
    private boolean isVirtualUser;
    private String deviceId;

    public MatrixClientContext(_MatrixHomeserver hs, _MatrixID user, String token) {
        this(hs, user, token, false);
    }

    public MatrixClientContext(_MatrixHomeserver hs, _MatrixID user, String token, boolean isVirtualUser) {
        this(hs, user, isVirtualUser);
        this.token = token;
    }

    public MatrixClientContext(_MatrixHomeserver hs) {
        this(hs, false);
    }

    public MatrixClientContext(_MatrixHomeserver hs, String deviceId) {
        this(hs, false);
        this.deviceId = deviceId;
    }

    private MatrixClientContext(_MatrixHomeserver hs, _MatrixID user, boolean isVirtualUser) {
        this(hs, isVirtualUser);
        this.user = user;
    }

    private MatrixClientContext(_MatrixHomeserver hs, boolean isVirtualUser) {
        this.hs = hs;
        this.isVirtualUser = isVirtualUser;
    }

    public _MatrixHomeserver getHs() {
        return hs;
    }

    public _MatrixID getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isVirtualUser() {
        return isVirtualUser;
    }

    public Optional<String> getDeviceId() {
        return Optional.ofNullable(deviceId);
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
