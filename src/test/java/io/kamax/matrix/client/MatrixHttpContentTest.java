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

/*
 * TODO As the spec is outdated, I'm not sure if the error 403 can really happen in these test cases. This class has
 * to be checked for correctness, when the matrix's spec is updated.
 */
public class MatrixHttpContentTest extends MatrixHttpTest {
    private URI address = new URI("mxc://localhost/testAddress.txt");

    public MatrixHttpContentTest() throws URISyntaxException {
    }

    @Test
    public void isValid() throws URISyntaxException {
        isValidSuccessful(true, 200);
    }

    @Test
    public void isValidMissingContentType() throws URISyntaxException {
        String url = createDownloadUrl();
        String bodyFile = "textfile.txt";
        TestResponseBuilder responseBuilder = new TestResponseBuilder(200).setBodyFile(bodyFile);
        new TestRunnerGet<Boolean>(new TestRequestBuilder(url), responseBuilder).runTest(createContentObject()::isValid,
                false);
    }

    @Test
    public void isValidError404() throws URISyntaxException {
        isValidSuccessful(false, 404);
    }

    @Test
    public void isValidError403() throws URISyntaxException {
        isValidSuccessful(false, 403);
    }

    private void isValidSuccessful(boolean expectedResult, int responseStatus) throws URISyntaxException {
        String url = createDownloadUrl();
        String bodyFile = "textfile.txt";

        TestResponseBuilder responseBuilder = new TestResponseBuilder(responseStatus).setBodyFile(bodyFile)//
                .setContentType("text/plain");

        new TestRunnerGet<Boolean>(new TestRequestBuilder(url), responseBuilder).runTest(createContentObject()::isValid,
                expectedResult);
    }

    @Test
    public void getType() throws URISyntaxException, IOException {
        getTypeSuccessful(200, "text/plain");
    }

    @Test
    public void getTypeMissingContentType() throws URISyntaxException {
        String url = createDownloadUrl();
        String bodyFile = "textfile.txt";
        TestResponseBuilder responseBuilder = new TestResponseBuilder(200).setBodyFile(bodyFile);
        new TestRunnerGet<String>(new TestRequestBuilder(url), responseBuilder).runTest(createContentObject()::getType,
                null);
    }

    @Test
    public void getTypeError404() throws URISyntaxException, IOException {
        getTypeSuccessful(404, null);
    }

    @Test
    public void getTypeError403() throws URISyntaxException, IOException {
        getTypeSuccessful(403, null);
    }

    private void getTypeSuccessful(int responseStatus, String contentType) throws URISyntaxException {
        String url = createDownloadUrl();
        String bodyFile = "textfile.txt";

        TestResponseBuilder responseBuilder = new TestResponseBuilder(responseStatus).setBodyFile(bodyFile)
                .setContentType("text/plain");//

        new TestRunnerGet<String>(new TestRequestBuilder(url), responseBuilder).runTest(createContentObject()::getType,
                contentType);
    }

    @Test
    public void getData() throws URISyntaxException, IOException {
        byte[] expectedResult = Files.readAllBytes(Paths.get(ClassLoader
                .getSystemResource("wiremock" + File.separator + "__files" + File.separator + "textfile.txt").toURI()));
        getDataSuccessful(200, expectedResult, "textfile.txt");
    }

    @Test
    public void getDataMissingContentType() throws URISyntaxException {
        String url = createDownloadUrl();
        String bodyFile = "textfile.txt";
        TestResponseBuilder responseBuilder = new TestResponseBuilder(200).setBodyFile(bodyFile);
        new TestRunnerGet<byte[]>(new TestRequestBuilder(url), responseBuilder).runTest(createContentObject()::getData,
                null);
    }

    @Test
    public void getDataError404() throws URISyntaxException, IOException {
        getDataSuccessful(404, null, "textfile.txt");
    }

    @Test
    public void getDataError403() throws URISyntaxException, IOException {
        getDataSuccessful(403, null, "textfile.txt");
    }

    private void getDataSuccessful(int responseStatus, byte[] expectedResult, String bodyFile)
            throws URISyntaxException {
        String url = createDownloadUrl();

        TestResponseBuilder responseBuilder = new TestResponseBuilder(responseStatus).setBodyFile(bodyFile)//
                .setContentType("text/plain");

        new TestRunnerGet<byte[]>(new TestRequestBuilder(url), responseBuilder).runTest(createContentObject()::getData,
                expectedResult);
    }

    @Test
    public void getFilename() throws URISyntaxException, IOException {
        String url = createDownloadUrl();
        String bodyFile = "textfile.txt";

        TestResponseBuilder responseBuilder = new TestResponseBuilder(200).setBodyFile(bodyFile)//
                .setContentType("text/plain")//
                .putHeader("Content-Disposition", String.format("filename=" + bodyFile + ";"));

        new TestRunnerGet<Optional<String>>(new TestRequestBuilder(url), responseBuilder)
                .runTest(createContentObject()::getFilename, Optional.of(bodyFile));

        responseBuilder.putHeader("Content-Disposition", String.format("filename=\"%s\";", bodyFile));
        new TestRunnerGet<Optional<String>>(new TestRequestBuilder(url), responseBuilder)
                .runTest(createContentObject()::getFilename, Optional.of(bodyFile));

        responseBuilder.putHeader("Content-Disposition", String.format("filename=\"%s\"", bodyFile));
        new TestRunnerGet<Optional<String>>(new TestRequestBuilder(url), responseBuilder)
                .runTest(createContentObject()::getFilename, Optional.of(bodyFile));

        responseBuilder.putHeader("Content-Disposition", String.format("filename=%s", bodyFile));
        new TestRunnerGet<Optional<String>>(new TestRequestBuilder(url), responseBuilder)
                .runTest(createContentObject()::getFilename, Optional.of(bodyFile));
    }

    @Test
    public void getFilenameMissingContentType() throws URISyntaxException {
        String url = createDownloadUrl();
        String bodyFile = "textfile.txt";
        TestResponseBuilder responseBuilder = new TestResponseBuilder(200).setBodyFile(bodyFile);
        new TestRunnerGet<Optional<String>>(new TestRequestBuilder(url), responseBuilder)
                .runTest(createContentObject()::getFilename, Optional.empty());
    }

    @Test
    public void getFilenameError404() throws URISyntaxException, IOException {
        getFilenameSuccessful(404, "textfile.txt", Optional.empty());
    }

    @Test
    public void getFilenameError403() throws URISyntaxException, IOException {
        getFilenameSuccessful(404, "textfile.txt", Optional.empty());
    }

    private void getFilenameSuccessful(int responseStatus, String bodyFile, Optional<String> expectedResult)
            throws URISyntaxException {
        String url = createDownloadUrl();

        TestResponseBuilder responseBuilder = new TestResponseBuilder(responseStatus).setBodyFile(bodyFile)//
                .setContentType("text/plain")//
                .putHeader("Content-Disposition", String.format("filename=%s;", bodyFile));

        new TestRunnerGet<Optional<String>>(new TestRequestBuilder(url), responseBuilder)
                .runTest(createContentObject()::getFilename, expectedResult);
    }

    private MatrixHttpContent createContentObject() throws URISyntaxException {
        MatrixClientContext context = createClientContext();
        return new MatrixHttpContent(context, address);
    }

    private String createDownloadUrl() {
        return "/_matrix/media/v1/download/" + address.getHost() + address.getPath() + getAcessTokenParameter();
    }

}
