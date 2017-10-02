package io.kamax.matrix.client;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;

import org.hamcrest.core.IsEqual;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.junit.Assert.assertThat;

public class SuccessTestRunner<R, P> {
    private RequestBuilder requestBuilder;
    private ResponseBuilder responseBuilder;

    public SuccessTestRunner(RequestBuilder requestBuilder, ResponseBuilder responseBuilder) {
        this.requestBuilder = requestBuilder;
        this.responseBuilder = responseBuilder;
    }

    public void runGetTest(Supplier<R> method, R expectedResult) {
        stubFor(get(urlEqualTo(requestBuilder.getUrl())).willReturn(createResponse()));

        assertThat(method.get(), IsEqual.equalTo(expectedResult));
    }

    public void runPostTest(Consumer<P> method, P parameter, String verifyBody) {
        String url = requestBuilder.getUrl();
        stubFor(post(urlEqualTo(url)).willReturn(createResponse()));
        method.accept(parameter);
        verify(postRequestedFor(urlEqualTo(url)).withRequestBody(containing(verifyBody)));
    }

    public void runPutTest(Consumer<P> method, P parameter, String verifyBody) {
        String url = requestBuilder.getUrl();
        stubFor(put(urlEqualTo(url)).willReturn(createResponse()));
        method.accept(parameter);
        verify(putRequestedFor(urlEqualTo(url)).withRequestBody(containing(verifyBody)));
    }

    private ResponseDefinitionBuilder createResponse() {
        ResponseDefinitionBuilder response = aResponse().withStatus(responseBuilder.getStatus());
        Optional<String> body = responseBuilder.getBody();
        if (body.isPresent()) {
            response.withBody(body.get());
        } else {
            response.withBodyFile(responseBuilder.getBodyFile().get());
        }

        Optional<String> contentType = responseBuilder.getContentType();
        if (contentType.isPresent()) {
            response.withHeader("Content-Type", contentType.get());
        }

        Map<String, String> headers = responseBuilder.getHeaders();
        for (String header : headers.keySet()) {
            response.withHeader(header, headers.get(header));
        }
        return response;
    }
}
