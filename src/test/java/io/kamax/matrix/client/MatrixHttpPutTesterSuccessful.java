package io.kamax.matrix.client;

import com.github.tomakehurst.wiremock.client.MappingBuilder;

import java.util.function.Consumer;

import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class MatrixHttpPutTesterSuccessful extends AMatrixHttpConsumeTester {
    public MatrixHttpPutTesterSuccessful(Consumer<String> putMethod, String valueToConsume, String url,
            String returnBody) {
        super(putMethod, valueToConsume);
        setupWiremock(url, 200, returnBody);
    }

    @Override
    public void runTest() {
        consumeMethod.accept(valueToConsume);
    }

    @Override
    protected MappingBuilder createUrlMappingBuilder(String url) {
        return put(urlEqualTo(url));
    }
}
