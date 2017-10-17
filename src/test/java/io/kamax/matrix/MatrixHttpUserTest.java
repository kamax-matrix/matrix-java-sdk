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

import io.kamax.matrix.client.MatrixClientContext;
import io.kamax.matrix.client.MatrixClientRequestException;
import io.kamax.matrix.client.MatrixHttpTest;
import io.kamax.matrix.client.MatrixHttpUser;

import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MatrixHttpUserTest extends MatrixHttpTest {
    private String nameUrl = String.format("/_matrix/client/r0/profile/%s/displayname", user.getId()) + tokenParameter;
    private String nameResponse = String.format("{\"displayname\": \"%s\"}", nameOfUser);

    private String avatarUrl = String.format("/_matrix/client/r0/profile/%s/avatar_url", user.getId()) + tokenParameter;
    private String avatarMediaUrl = "mxc://matrix.org/wefh34uihSDRGhw34";
    private String avatarResponse = String.format("{\"avatar_url\": \"%s\"}", avatarMediaUrl);

    @Test
    public void getName() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(200).withBody(nameResponse)));
        assertThat(createUserObject().getName(), is(equalTo(Optional.of(nameOfUser))));
    }

    @Test
    public void getName404() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(404).withBody(error404Response)));
        assertThat(createUserObject().getName(), is(equalTo(Optional.empty())));
    }

    @Test
    public void getNameError403() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(403).withBody(error403Response)));

        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createUserObject()::getName);
        checkErrorInfo403(e);
    }

    @Test
    public void getNameError429() throws URISyntaxException {
        stubFor(get(urlEqualTo(nameUrl)).willReturn(aResponse().withStatus(429).withBody(error429Response)));

        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createUserObject()::getName);
        checkErrorInfo429(e);
    }

    @Test
    public void getAvatar() throws URISyntaxException {
        stubFor(get(urlEqualTo(avatarUrl)).willReturn(aResponse().withStatus(200).withBody(avatarResponse)));
        Optional<_MatrixContent> matrixContent = createUserObject().getAvatar();
        assertTrue(matrixContent.isPresent());
        assertThat(matrixContent.get().getAddress(), is(equalTo(new URI(avatarMediaUrl))));
    }

    @Test
    public void getAvatar404() throws URISyntaxException {
        stubFor(get(urlEqualTo(avatarUrl)).willReturn(aResponse().withStatus(404).withBody(error404Response)));
        assertThat(createUserObject().getAvatar(), is(equalTo(Optional.empty())));
    }

    @Test
    public void getAvatarError403() throws URISyntaxException {
        stubFor(get(urlEqualTo(avatarUrl)).willReturn(aResponse().withStatus(403).withBody(error403Response)));

        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                createUserObject()::getAvatar);
        checkErrorInfo403(e);
    }

    @Test
    public void getAvatarError429() throws URISyntaxException {
        stubFor(get(urlEqualTo(avatarUrl)).willReturn(aResponse().withStatus(429).withBody(error429Response)));

        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                createUserObject()::getAvatar);
        checkErrorInfo429(e);
    }

    private MatrixHttpUser createUserObject() throws URISyntaxException {
        MatrixClientContext context = createClientContext();
        return new MatrixHttpUser(context, context.getUser());
    }
}
