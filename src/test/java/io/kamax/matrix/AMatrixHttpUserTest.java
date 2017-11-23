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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class AMatrixHttpUserTest extends MatrixHttpTest {
    protected String avatarMediaUrl = "mxc://matrix.org/wefh34uihSDRGhw34";

    @Test
    public void getName() throws URISyntaxException {
        assertThat(createUserObject().getName(), is(equalTo(Optional.of(username))));
    }

    @Test
    public void getName404() throws URISyntaxException {
        assertThat(createUserObject().getName(), is(equalTo(Optional.empty())));
    }

    @Test
    public void getNameError403() throws URISyntaxException {
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createUserObject()::getName);
        checkErrorInfo403(e);
    }

    @Test
    public void getNameError429() throws URISyntaxException {
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class, createUserObject()::getName);
        checkErrorInfo429(e);
    }

    @Test
    public void getAvatar() throws URISyntaxException {
        Optional<_MatrixContent> matrixContent = createUserObject().getAvatar();
        assertTrue(matrixContent.isPresent());
        assertThat(matrixContent.get().getAddress(), is(equalTo(new URI(avatarMediaUrl))));
    }

    @Test
    public void getAvatar404() throws URISyntaxException {
        assertThat(createUserObject().getAvatar(), is(equalTo(Optional.empty())));
    }

    @Test
    public void getAvatarError403() throws URISyntaxException {
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                createUserObject()::getAvatar);
        checkErrorInfo403(e);
    }

    @Test
    public void getAvatarError429() throws URISyntaxException {
        MatrixClientRequestException e = assertThrows(MatrixClientRequestException.class,
                createUserObject()::getAvatar);
        checkErrorInfo429(e);
    }

    private MatrixHttpUser createUserObject() throws URISyntaxException {
        MatrixClientContext context = getOrCreateClientContext();
        return new MatrixHttpUser(context, context.getUser().get());
    }
}
