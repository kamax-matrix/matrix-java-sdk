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

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class MatrixHttpContentWiremockTest extends AMatrixHttpContentTest {
    private String downloadUrl = "/_matrix/media/v1/download/" + address.getHost() + address.getPath() + tokenParameter;

    public MatrixHttpContentWiremockTest() throws URISyntaxException {
    }

    @Override
    public void login() throws URISyntaxException {
    }

    @Override
    public void logout() {
    }

    @Test
    public void isValid() throws URISyntaxException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(
                aResponse().withStatus(200).withBodyFile(bodyFilename).withHeader("Content-Type", "text/plain")));
        super.isValid();
    }

    @Test
    public void isValidMissingContentType() throws URISyntaxException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(aResponse().withStatus(200).withBodyFile(bodyFilename)));
        super.isValidMissingContentType();
    }

    @Test
    public void isValidContentNotFound() throws URISyntaxException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(aResponse().withStatus(404).withBody(errorNotFoundResponse)));
        super.isValidContentNotFound();
    }

    @Test
    public void isValidErrorAccessDenied() throws URISyntaxException {
        stubFor(get(urlEqualTo(downloadUrl))
                .willReturn(aResponse().withStatus(403).withBody(errorAccessDeniedResponse)));
        super.isValidErrorAccessDenied();
    }

    @Test
    public void getType() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(
                aResponse().withStatus(200).withBodyFile(bodyFilename).withHeader("Content-Type", "text/plain")));
        super.getType();
    }

    @Test
    public void getTypeMissingContentType() throws URISyntaxException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(aResponse().withStatus(200).withBodyFile(bodyFilename)));
        super.getTypeMissingContentType();
    }

    @Test
    public void getTypeErrorContentNotFound() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(aResponse().withStatus(404).withBody(errorNotFoundResponse)));
        super.getTypeErrorContentNotFound();
    }

    @Test
    public void getTypeErrorAccessDenied() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl))
                .willReturn(aResponse().withStatus(403).withBody(errorAccessDeniedResponse)));
        super.getTypeErrorAccessDenied();
    }

    @Test
    public void getData() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(
                aResponse().withStatus(200).withBodyFile(bodyFilename).withHeader("Content-Type", "text/plain")));
        super.getData();
    }

    @Test
    public void getDataMissingContentType() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(aResponse().withStatus(200).withBodyFile(bodyFilename)));
        super.getDataMissingContentType();
    }

    @Test
    public void getDataErrorContentNotFound() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(aResponse().withStatus(404).withBody(errorNotFoundResponse)));
        super.getDataErrorContentNotFound();
    }

    @Test
    public void getDataErrorAccessDenied() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl))
                .willReturn(aResponse().withStatus(403).withBody(errorAccessDeniedResponse)));
        super.getDataErrorAccessDenied();
    }

    @Test
    public void getFilename() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(
                aResponse().withStatus(200).withBodyFile(bodyFilename).withHeader("Content-Type", "text/plain")
                        .withHeader("Content-Disposition", String.format("filename=%s;", bodyFilename))));
        super.getFilename();
    }

    @Test
    public void getFilename2() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(
                aResponse().withStatus(200).withBodyFile(bodyFilename).withHeader("Content-Type", "text/plain")
                        .withHeader("Content-Disposition", String.format("filename=\"%s\";", bodyFilename))));
        super.getFilename();
    }

    @Test
    public void getFilename3() throws URISyntaxException, IOException {

        stubFor(get(urlEqualTo(downloadUrl)).willReturn(
                aResponse().withStatus(200).withBodyFile(bodyFilename).withHeader("Content-Type", "text/plain")
                        .withHeader("Content-Disposition", String.format("filename=\"%s\"", bodyFilename))));
        super.getFilename();
    }

    @Test
    public void getFilename4() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(
                aResponse().withStatus(200).withBodyFile(bodyFilename).withHeader("Content-Type", "text/plain")
                        .withHeader("Content-Disposition", String.format("filename=%s", bodyFilename))));
        super.getFilename();
    }

    @Test
    public void getFilenameMissingContentType() throws URISyntaxException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(
                aResponse().withStatus(200).withBodyFile(bodyFilename).withHeader("Content-Type", "text/plain")));
        super.getFilenameMissingContentType();
    }

    @Test
    public void getFilenameErrorContentNotFound() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl)).willReturn(aResponse().withStatus(404).withBody(errorNotFoundResponse)));
        super.getFilenameErrorContentNotFound();
    }

    @Test
    public void getFilenameErrorAccessDenied() throws URISyntaxException, IOException {
        stubFor(get(urlEqualTo(downloadUrl))
                .willReturn(aResponse().withStatus(403).withBody(errorAccessDeniedResponse)));
        super.getFilenameErrorAccessDenied();
    }

}
