/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Arne Augenstein
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

package io.kamax.matrix;

import org.junit.Test;

import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class MatrixHttpUserWiremockTest extends AMatrixHttpUserTest {
    private String nameUrl = String.format("/_matrix/client/r0/profile/%s/displayname", user.getId()) + tokenParameter;
    private String nameResponse = String.format("{\"displayname\": \"%s\"}", username);

    private String avatarUrl = String.format("/_matrix/client/r0/profile/%s/avatar_url", user.getId()) + tokenParameter;
    private String avatarResponse = String.format("{\"avatar_url\": \"%s\"}", avatarMediaUrl);

    @Override
    public void login() throws URISyntaxException {
    }

    @Override
    public void logout() {
    }

    @Test
    public void getName() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(200).withBody(nameResponse)));
        super.getName();
    }

    @Test
    public void getName404() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(404).withBody(error404Response)));
        super.getName404();
    }

    @Test
    public void getNameError403() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(403).withBody(error403Response)));
        super.getNameError403();
    }

    @Test
    public void getNameError429() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(429).withBody(error429Response)));
        super.getNameError429();
    }

    @Test
    public void getAvatar() throws URISyntaxException {
        stubFor(get(urlEqualTo(avatarUrl)).willReturn(aResponse().withStatus(200).withBody(avatarResponse)));
        super.getAvatar();
    }

    @Test
    public void getAvatar404() throws URISyntaxException {
        stubFor(get(urlEqualTo(avatarUrl)).willReturn(aResponse().withStatus(404).withBody(error404Response)));
        super.getAvatar404();
    }

    @Test
    public void getAvatarError403() throws URISyntaxException {
        stubFor(get(urlEqualTo(avatarUrl)).willReturn(aResponse().withStatus(403).withBody(error403Response)));
        super.getAvatarError403();
    }

    @Test
    public void getAvatarError429() throws URISyntaxException {
        stubFor(get(urlEqualTo(avatarUrl)).willReturn(aResponse().withStatus(429).withBody(error429Response)));
        super.getAvatarError429();
    }

}
