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

import io.kamax.matrix.MatrixID;
import io.kamax.matrix._MatrixID;
import io.kamax.matrix.hs.MatrixHomeserver;
import io.kamax.matrix.hs._MatrixHomeserver;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;

public class MatrixClientContext {

    private String domain;
    private URL hsBaseUrl;
    private URL isBaseUrl;
    private _MatrixID user;
    private String token;
    private boolean isVirtual;
    private String deviceId;
    private String initialDeviceName;

    public MatrixClientContext() {
        // stub
    }

    public MatrixClientContext(MatrixClientContext other) {
        this.domain = other.domain;
        this.hsBaseUrl = other.hsBaseUrl;
        this.isBaseUrl = other.isBaseUrl;
        this.user = other.user;
        this.token = other.token;
        this.isVirtual = other.isVirtual;
        this.deviceId = other.deviceId;
        this.initialDeviceName = other.initialDeviceName;
    }

    public MatrixClientContext(_MatrixHomeserver hs) {
        setDomain(hs.getDomain());
        setHsBaseUrl(hs.getBaseEndpoint());
    }

    public MatrixClientContext(_MatrixHomeserver hs, _MatrixID user, String token) {
        this(hs);
        setUser(user);
        setToken(token);
    }

    public _MatrixHomeserver getHomeserver() {
        if (Objects.isNull(hsBaseUrl)) {
            throw new IllegalStateException("Homeserver Base URL is not set");
        }

        return new MatrixHomeserver(domain, hsBaseUrl.toString());
    }

    public String getDomain() {
        return domain;
    }

    public MatrixClientContext setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public URL getHsBaseUrl() {
        return hsBaseUrl;
    }

    public MatrixClientContext setHsBaseUrl(URL hsBaseUrl) {
        this.hsBaseUrl = hsBaseUrl;
        return this;
    }

    public URL getIsBaseUrl() {
        return isBaseUrl;
    }

    public MatrixClientContext setIsBaseUrl(URL isBaseUrl) {
        this.isBaseUrl = isBaseUrl;
        return this;
    }

    public Optional<_MatrixID> getUser() {
        return Optional.ofNullable(user);
    }

    public MatrixClientContext setUser(_MatrixID user) {
        this.user = user;
        return this;
    }

    public MatrixClientContext setUserWithLocalpart(String localpart) {
        setUser(MatrixID.asAcceptable(localpart, getDomain()));
        return this;
    }

    public String getToken() {
        return token;
    }

    public MatrixClientContext setToken(String token) {
        this.token = token;
        return this;
    }

    public boolean isVirtual() {
        return isVirtual;
    }

    public MatrixClientContext setVirtual(boolean virtual) {
        isVirtual = virtual;
        return this;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public MatrixClientContext setDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public String getInitialDeviceName() {
        return initialDeviceName;
    }

    public MatrixClientContext setInitialDeviceName(String initialDeviceName) {
        this.initialDeviceName = initialDeviceName;
        return this;
    }

}
