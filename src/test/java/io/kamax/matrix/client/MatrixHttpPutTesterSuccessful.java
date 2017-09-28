package io.kamax.matrix.client;

import java.util.function.Consumer;

public class MatrixHttpPutTesterSuccessful extends AMatrixHttpPutTester {
    public MatrixHttpPutTesterSuccessful(Consumer<String> putMethod, String valueToConsume, String url,
            String returnBody) {
        super(putMethod, valueToConsume);
        setupWiremock(url, 200, returnBody);
    }

    @Override
    public void runTest() {
        putMethod.accept(valueToConsume);
    }
}
