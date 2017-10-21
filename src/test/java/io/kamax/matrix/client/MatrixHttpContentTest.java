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

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/*
 * TODO As the spec is outdated, I'm not sure if the error 403 can really happen in these test cases. This class has
 * to be checked for correctness, when matrix's spec is updated.
 */
public class MatrixHttpContentTest extends MatrixHttpTest {
    private String bodyFilename = "textfile.txt";
    private URI address = new URI("mxc://localhost/testpath/" + bodyFilename);
    private String downloadUrl = "/_matrix/media/v1/download/" + address.getHost() + address.getPath() + tokenParameter;

    public MatrixHttpContentTest() throws URISyntaxException {
    }

    @Test
    public void isValid() throws URISyntaxException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(
                aResponse().withStatus(200).withBodyFile(bodyFilename).withHeader("Content-Type", "text/plain")));
        assertTrue(createContentObject().isValid());
    }

    @Test
    public void isValidMissingContentType() throws URISyntaxException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(aResponse().withStatus(200).withBodyFile(bodyFilename)));
        assertTrue(createContentObject().isValid());
    }

    @Test
    public void isValidError404() throws URISyntaxException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(aResponse().withStatus(404).withBody(error404Response)));
        assertFalse(createContentObject().isValid());
    }

    @Test
    public void isValidError403() throws URISyntaxException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(aResponse().withStatus(403).withBody(error403Response)));
        assertFalse(createContentObject().isValid());
    }

    @Test
    public void getType() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(
                aResponse().withStatus(200).withBodyFile(bodyFilename).withHeader("Content-Type", "text/plain")));
        assertEquals(Optional.of("text/plain"), createContentObject().getType());
    }

    @Test
    public void getTypeMissingContentType() throws URISyntaxException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(aResponse().withStatus(200).withBodyFile(bodyFilename)));
        assertEquals(Optional.empty(), createContentObject().getType());
    }

    @Test
    public void getTypeError404() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(aResponse().withStatus(404).withBody(error404Response)));
        MatrixHttpContent contentObject = createContentObject();
        assertFalse(contentObject.isValid());
        assertThrows(IllegalStateException.class, contentObject::getType);
    }

    @Test
    public void getTypeError403() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(aResponse().withStatus(403).withBody(error403Response)));
        MatrixHttpContent contentObject = createContentObject();
        assertFalse(contentObject.isValid());
        assertThrows(IllegalStateException.class, createContentObject()::getType);
    }

    @Test
    public void getData() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(
                aResponse().withStatus(200).withBodyFile(bodyFilename).withHeader("Content-Type", "text/plain")));

        byte[] expectedResult = Files.readAllBytes(Paths.get(ClassLoader
                .getSystemResource("wiremock" + File.separator + "__files" + File.separator + bodyFilename).toURI()));
        assertThat(createContentObject().getData(), IsEqual.equalTo(expectedResult));
    }

    @Test
    public void getDataMissingContentType() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(aResponse().withStatus(200).withBodyFile(bodyFilename)));

        byte[] expectedResult = Files.readAllBytes(Paths.get(ClassLoader
                .getSystemResource("wiremock" + File.separator + "__files" + File.separator + bodyFilename).toURI()));
        assertThat(createContentObject().getData(), IsEqual.equalTo(expectedResult));
    }

    @Test
    public void getDataError404() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(aResponse().withStatus(404).withBody(error404Response)));

        MatrixHttpContent contentObject = createContentObject();
        assertFalse(contentObject.isValid());
        assertThrows(IllegalStateException.class, contentObject::getData);
    }

    @Test
    public void getDataError403() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(aResponse().withStatus(403).withBody(error403Response)));

        MatrixHttpContent contentObject = createContentObject();
        assertFalse(contentObject.isValid());
        assertThrows(IllegalStateException.class, contentObject::getData);
    }

    @Test
    public void getFilename() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(
                aResponse().withStatus(200).withBodyFile(bodyFilename).withHeader("Content-Type", "text/plain")
                        .withHeader("Content-Disposition", String.format("filename=%s;", bodyFilename))));
        assertEquals(Optional.of(bodyFilename), createContentObject().getFilename());

        reset();

        stubFor(get(urlEqualTo(downloadUrl)).willReturn(
                aResponse().withStatus(200).withBodyFile(bodyFilename).withHeader("Content-Type", "text/plain")
                        .withHeader("Content-Disposition", String.format("filename=\"%s\";", bodyFilename))));
        assertEquals(Optional.of(bodyFilename), createContentObject().getFilename());

        reset();

        stubFor(get(urlEqualTo(downloadUrl)).willReturn(
                aResponse().withStatus(200).withBodyFile(bodyFilename).withHeader("Content-Type", "text/plain")
                        .withHeader("Content-Disposition", String.format("filename=\"%s\"", bodyFilename))));
        assertEquals(Optional.of(bodyFilename), createContentObject().getFilename());

        reset();

        stubFor(get(urlEqualTo(downloadUrl)).willReturn(
                aResponse().withStatus(200).withBodyFile(bodyFilename).withHeader("Content-Type", "text/plain")
                        .withHeader("Content-Disposition", String.format("filename=%s", bodyFilename))));
        assertEquals(Optional.of(bodyFilename), createContentObject().getFilename());
    }

    @Test
    public void getFilenameMissingContentType() throws URISyntaxException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(
                aResponse().withStatus(200).withBodyFile(bodyFilename).withHeader("Content-Type", "text/plain")));
        assertEquals(Optional.empty(), createContentObject().getFilename());
    }

    @Test
    public void getFilenameError404() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(aResponse().withStatus(404).withBody(error404Response)));

        MatrixHttpContent contentObject = createContentObject();
        assertFalse(contentObject.isValid());
        assertThrows(IllegalStateException.class, contentObject::getFilename);
    }

    @Test
    public void getFilenameError403() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(aResponse().withStatus(403).withBody(error403Response)));
        MatrixHttpContent contentObject = createContentObject();
        assertFalse(contentObject.isValid());
        assertThrows(IllegalStateException.class, contentObject::getFilename);
    }

    private MatrixHttpContent createContentObject() throws URISyntaxException {
        MatrixClientContext context = createClientContext();
        return new MatrixHttpContent(context, address);
    }

}
