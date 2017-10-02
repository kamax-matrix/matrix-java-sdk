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

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class MatrixHttpContentTest extends MatrixHttpTest {
    private URI address = new URI("mxc://localhost/testAddress.txt");

    public MatrixHttpContentTest() throws URISyntaxException {
    }
    // TODO getType, getData, getFilename
    // TODO test error handling

    @Test
    public void isValid() throws URISyntaxException {
        String url = createDownloadUrl();
        String bodyFile = "textfile.txt";

        ResponseBuilder responseBuilder = new ResponseBuilder(200).setBodyFile(bodyFile)//
                .setContentType("text/plain");

        new SuccessTestRunner<Boolean, Void>(new RequestBuilder(url), responseBuilder)
                .runGetTest(createContentObject()::isValid, true);

    }

    public void isValidMissingContentType() throws URISyntaxException {
        String url = createDownloadUrl();
        String bodyFile = "textfile.txt";
        ResponseBuilder responseBuilder = new ResponseBuilder(200).setBodyFile(bodyFile);
        new SuccessTestRunner<Boolean, Void>(new RequestBuilder(url), responseBuilder)
                .runGetTest(createContentObject()::isValid, false);
    }

    @Test
    public void getType() throws URISyntaxException, IOException {
        String url = createDownloadUrl();
        String bodyFile = "textfile.txt";
        String contentType = "text/plain";

        ResponseBuilder responseBuilder = new ResponseBuilder(200).setBodyFile(bodyFile)//
                .setContentType("text/plain");//

        new SuccessTestRunner<String, Void>(new RequestBuilder(url), responseBuilder)
                .runGetTest(createContentObject()::getType, contentType);

    }

    @Test
    public void getData() throws URISyntaxException, IOException {
        String url = createDownloadUrl();
        String bodyFile = "textfile.txt";
        byte[] expectedResult = Files.readAllBytes(Paths.get(ClassLoader
                .getSystemResource("wiremock" + File.separator + "__files" + File.separator + bodyFile).toURI()));

        ResponseBuilder responseBuilder = new ResponseBuilder(200).setBodyFile(bodyFile)//
                .setContentType("text/plain");

        new SuccessTestRunner<byte[], Void>(new RequestBuilder(url), responseBuilder)
                .runGetTest(createContentObject()::getData, expectedResult);
    }

    @Test
    public void getFilename() throws URISyntaxException, IOException {
        String url = createDownloadUrl();
        String bodyFile = "textfile.txt";

        ResponseBuilder responseBuilder = new ResponseBuilder(200).setBodyFile(bodyFile)//
                .setContentType("text/plain")//
                .putHeader("Content-Disposition", "filename=" + bodyFile + ";");

        new SuccessTestRunner<Optional<String>, Void>(new RequestBuilder(url), responseBuilder)
                .runGetTest(createContentObject()::getFilename, Optional.of(bodyFile));

        responseBuilder.putHeader("Content-Disposition", ("filename=`" + bodyFile + "`;").replace('`', '"'));
        new SuccessTestRunner<Optional<String>, Void>(new RequestBuilder(url), responseBuilder)
                .runGetTest(createContentObject()::getFilename, Optional.of(bodyFile));

        responseBuilder.putHeader("Content-Disposition", ("filename=`" + bodyFile + "`").replace('`', '"'));
        new SuccessTestRunner<Optional<String>, Void>(new RequestBuilder(url), responseBuilder)
                .runGetTest(createContentObject()::getFilename, Optional.of(bodyFile));

        responseBuilder.putHeader("Content-Disposition", "filename=" + bodyFile);
        new SuccessTestRunner<Optional<String>, Void>(new RequestBuilder(url), responseBuilder)
                .runGetTest(createContentObject()::getFilename, Optional.of(bodyFile));
    }

    private MatrixHttpContent createContentObject() throws URISyntaxException {
        MatrixClientContext context = createClientContext();
        return new MatrixHttpContent(context, address);
    }

    private String createDownloadUrl() {
        return "/_matrix/media/v1/download/" + address.getHost() + address.getPath() + getAcessTokenParameter();
    }

}
