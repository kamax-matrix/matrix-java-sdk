package io.kamax.matrix.client;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.junit.Assert.assertEquals;

public class SuccessTestRunner<R, P> {
    private final int responseStatus;
    private final String url;
    private final String body;

    public SuccessTestRunner(String url, int responseStatus, String body) {
        this.responseStatus = responseStatus;
        this.url = url;
        this.body = body;
    }

    public void runGetTest(Supplier<R> method, R expectedResult) {
        stubFor(get(urlEqualTo(url)).willReturn(createResponse()));

        assertEquals(method.get(), expectedResult);
    }

    public void runPostTest(Consumer<P> method, P parameter, String verifyBody) {
        stubFor(post(urlEqualTo(url)).willReturn(createResponse()));
        method.accept(parameter);
        verify(postRequestedFor(urlEqualTo(url)).withRequestBody(containing(verifyBody)));
    }

    public void runPutTest(Consumer<P> method, P parameter, String verifyBody) {
        stubFor(put(urlEqualTo(url)).willReturn(createResponse()));
        method.accept(parameter);
        verify(putRequestedFor(urlEqualTo(url)).withRequestBody(containing(verifyBody)));
    }

    private ResponseDefinitionBuilder createResponse() {
        return aResponse().withStatus(responseStatus).withBody(body);
    }
}
