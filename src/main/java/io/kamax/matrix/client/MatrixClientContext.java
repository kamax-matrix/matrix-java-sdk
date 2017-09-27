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

public class MatrixClientContext {

    private _MatrixHomeserver hs;
    private _MatrixID user;
    private String token;
    private boolean isVirtualUser;

    public MatrixClientContext(_MatrixHomeserver hs, _MatrixID user, String token) {
        this(hs, user, token, false);
    }

    public MatrixClientContext(_MatrixHomeserver hs, _MatrixID user, String token, boolean isVirtualUser) {
        this.hs = hs;
        this.user = user;
        this.token = token;
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

    public boolean isVirtualUser() {
        return isVirtualUser;
    }

}
