package io.kamax.matrix.client;

import com.github.tomakehurst.wiremock.client.MappingBuilder;

import java.util.function.Consumer;

import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

public class MatrixHttpPostTesterSuccessful extends AMatrixHttpConsumeTester {
    public MatrixHttpPostTesterSuccessful(Consumer<String> postMethod, String valueToConsume, String url,
            String returnBody) {
        super(postMethod, valueToConsume);
        setupWiremock(url, 200, returnBody);
    }

    @Override
    public void runTest() {
        consumeMethod.accept(valueToConsume);
    }

    @Override
    protected MappingBuilder createUrlMappingBuilder(String url) {
        return post(urlEqualTo(url));
    }
}
