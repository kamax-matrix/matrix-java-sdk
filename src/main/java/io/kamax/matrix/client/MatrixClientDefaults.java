/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2018 Kamax Sarl
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

public class MatrixClientDefaults {

    private int connectTimeout = 30 * 1000; // 30 sec
    private int requestTimeout = 5 * 60 * 1000; // 5 min
    private int socketTimeout = requestTimeout;

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public MatrixClientDefaults setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;

        return this;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public MatrixClientDefaults setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;

        return this;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public MatrixClientDefaults setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;

        return this;
    }

}
