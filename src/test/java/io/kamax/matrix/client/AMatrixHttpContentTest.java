/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Arne Augenstein
 *
 * https://www.kamax.io/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.matrix.client;

import org.hamcrest.core.IsEqual;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/*
 * TODO As the spec is outdated, I'm not sure if the error 403 can really happen in these test cases. This class has
 * to be checked for correctness, when matrix's spec is updated.
 */
public abstract class AMatrixHttpContentTest extends MatrixHttpTest {
    protected String bodyFilename = "textfile.txt";
    protected URI address = new URI("mxc://localhost/testpath/" + bodyFilename);

    public AMatrixHttpContentTest() throws URISyntaxException {
    }

    @Test
    public void isValid() throws URISyntaxException {
        assertTrue(createContentObject().isValid());
    }

    @Test
    public void isValidMissingContentType() throws URISyntaxException {
        assertTrue(createContentObject().isValid());
    }

    @Test
    public void isValidContentNotFound() throws URISyntaxException {
        assertFalse(createContentObject().isValid());
    }

    @Test
    public void isValidErrorAccessDenied() throws URISyntaxException {
        assertFalse(createContentObject().isValid());
    }

    @Test
    public void getType() throws URISyntaxException, IOException {
        assertEquals(Optional.of("text/plain"), createContentObject().getType());
    }

    @Test
    public void getTypeMissingContentType() throws URISyntaxException {
        assertEquals(Optional.empty(), createContentObject().getType());
    }

    @Test
    public void getTypeErrorContentNotFound() throws URISyntaxException, IOException {
        MatrixHttpContent contentObject = createContentObject();
        assertFalse(contentObject.isValid());
        assertThrows(IllegalStateException.class, contentObject::getType);
    }

    @Test
    public void getTypeErrorAccessDenied() throws URISyntaxException, IOException {
        MatrixHttpContent contentObject = createContentObject();
        assertFalse(contentObject.isValid());
        assertThrows(IllegalStateException.class, createContentObject()::getType);
    }

    @Test
    public void getData() throws URISyntaxException, IOException {
        byte[] expectedResult = Files.readAllBytes(Paths.get(ClassLoader
                .getSystemResource("wiremock" + File.separator + "__files" + File.separator + bodyFilename).toURI()));
        assertThat(createContentObject().getData(), IsEqual.equalTo(expectedResult));
    }

    @Test
    public void getDataMissingContentType() throws URISyntaxException, IOException {
        byte[] expectedResult = Files.readAllBytes(Paths.get(ClassLoader
                .getSystemResource("wiremock" + File.separator + "__files" + File.separator + bodyFilename).toURI()));
        assertThat(createContentObject().getData(), IsEqual.equalTo(expectedResult));
    }

    @Test
    public void getDataErrorContentNotFound() throws URISyntaxException, IOException {
        MatrixHttpContent contentObject = createContentObject();
        assertFalse(contentObject.isValid());
        assertThrows(IllegalStateException.class, contentObject::getData);
    }

    @Test
    public void getDataErrorAccessDenied() throws URISyntaxException, IOException {
        MatrixHttpContent contentObject = createContentObject();
        assertFalse(contentObject.isValid());
        assertThrows(IllegalStateException.class, contentObject::getData);
    }

    @Test
    public void getFilename() throws URISyntaxException, IOException {
        assertEquals(Optional.of(bodyFilename), createContentObject().getFilename());
    }

    @Test
    public void getFilenameMissingContentType() throws URISyntaxException {
        assertEquals(Optional.empty(), createContentObject().getFilename());
    }

    @Test
    public void getFilenameErrorContentNotFound() throws URISyntaxException, IOException {
        MatrixHttpContent contentObject = createContentObject();
        assertFalse(contentObject.isValid());
        assertThrows(IllegalStateException.class, contentObject::getFilename);
    }

    @Test
    public void getFilenameErrorAccessDenied() throws URISyntaxException, IOException {
        MatrixHttpContent contentObject = createContentObject();
        assertFalse(contentObject.isValid());
        assertThrows(IllegalStateException.class, contentObject::getFilename);
    }

    private MatrixHttpContent createContentObject() throws URISyntaxException {
        MatrixClientContext context = getOrCreateClientContext();
        return new MatrixHttpContent(context, address);
    }

}
