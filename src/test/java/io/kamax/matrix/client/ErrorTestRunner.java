package io.kamax.matrix.client;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;

import io.kamax.matrix.MatrixErrorInfo;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

import static org.junit.Assert.*;

public class ErrorTestRunner<T, P> {
    private final int responseStatus;
    private final String url;

    private Optional<String> customErrcode = Optional.empty();
    private Optional<String> customError = Optional.empty();

    public ErrorTestRunner(String url, int responseStatus) {
        this.responseStatus = responseStatus;
        this.url = url;
    }

    public ResponseDefinitionBuilder getResponse() {
        return aResponse().withStatus(responseStatus).withBody(getErrorReturnBody());
    }

    public void runGetTest(Supplier<T> method) {
        stubFor(get(urlEqualTo(url)).willReturn(getResponse()));

        try {
            method.get();
        } catch (MatrixClientRequestException e) {
            /*
             * TODO refactor error handling, so that the error is returned. Afterwards, the error
             * values can be checked here by using e.getError().
             */
            // checkErrorInfo( e);
            return;
        }
        fail("In this case, an exception has to be thrown.");
    }

    public void runPostTest(Consumer<P> method, P parameter) {
        stubFor(post(urlEqualTo(url)).willReturn(getResponse()));
        runErrorTest(method, parameter);
    }

    public void runPutTest(Consumer<P> method, P parameter) {
        stubFor(put(urlEqualTo(url)).willReturn(getResponse()));
        runErrorTest(method, parameter);
    }

    private void runErrorTest(Consumer<P> method, P parameter) {
        try {
            method.accept(parameter);
        } catch (MatrixClientRequestException e) {
            checkErrorInfo(e);
            return;
        }
        fail("In this case, an exception has to be thrown.");
    }

    private void checkErrorInfo(MatrixClientRequestException e) {
        Optional<MatrixErrorInfo> errorOptional = e.getError();
        assertTrue(errorOptional.isPresent());
        assertEquals(errorOptional.get().getErrcode(), getErrcode());
        assertEquals(errorOptional.get().getError(), getError());
    }

    private String getErrcode() {
        if (customErrcode.isPresent()) {
            return customErrcode.get();
        }

        switch (responseStatus) {
        case 403:
            return "M_FORBIDDEN";
        case 429:
            return "M_LIMIT_EXCEEDED";
        default:
            return "";
        }
    }

    private String getError() {
        if (customError.isPresent()) {
            return customError.get();
        }

        switch (responseStatus) {
        case 403:
            return "You aren't a member of the room and weren't previously a member of the room.";
        case 429:
            return "Too many requests have been sent in a short period of time. Wait a while then try again.";
        default:
            return "";
        }
    }

    private String getErrorReturnBody() {
        return ("{'errcode': `" + getErrcode() + "`, " + "error: `" + getError() + "`}").replace('`', '"');
    }

}
