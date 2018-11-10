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

public class LoginPostBody {

    private static String MEDIUM_EMAIL_TYPE = "email";
    private static String IDENTIFIER_TYPE = "m.id.thirdparty";

    private String type = "m.login.password";
    private String user;
    private String password;
    private String deviceId;
    private String initialDeviceDisplayName;
    private String medium;
    private Identifier identifier;

    public LoginPostBody(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public LoginPostBody(String user, String password, String deviceId) {
        this(user, password);
        this.deviceId = deviceId;
    }

    public LoginPostBody(String user, String password, String deviceId, boolean hasEmailSpecificIdenfier){
        this(user, password, deviceId);
        this.identifier.setAddress(user);
        this.identifier.setMedium(MEDIUM_EMAIL_TYPE);
        this.identifier.setType(IDENTIFIER_TYPE);
        this.medium=MEDIUM_EMAIL_TYPE;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getInitialDeviceDisplayName() {
        return initialDeviceDisplayName;
    }

    public void setInitialDeviceDisplayName(String initialDeviceDisplayName) {
        this.initialDeviceDisplayName = initialDeviceDisplayName;
    }

}

