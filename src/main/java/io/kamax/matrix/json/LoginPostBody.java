/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Kamax Sarl
 * Copyright (C) 2018 Kamax Sarl
 *
 * https://www.kamax.io/
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.matrix.json;

public class LoginPostBody extends LoginBasePostBody {

//    private static String MEDIUM_EMAIL_TYPE = "email";
//    private static String IDENTIFIER_TYPE = "m.id.thirdparty";

    private String user;
    private String deviceId;


    public LoginPostBody(String user, String password) {
        super(password);
        this.user = user;
    }

    public LoginPostBody(String user, String password, String deviceId) {
        this(user, password);
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

}

