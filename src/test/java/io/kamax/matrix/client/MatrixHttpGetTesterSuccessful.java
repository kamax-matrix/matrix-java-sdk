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

import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public class MatrixHttpGetTesterSuccessful extends AMatrixHttpGetTester {
    private final Optional<String> expectedResult;

    public MatrixHttpGetTesterSuccessful(Supplier<Optional<String>> getMethod, String url, String returnBody,
            Optional<String> expectedResult) {
        super(getMethod);
        this.expectedResult = expectedResult;
        setupWiremock(url, 200, returnBody);
    }

    public MatrixHttpGetTesterSuccessful(Supplier<Optional<String>> getMethod, String url, int resultStatus,
            String returnBody, Optional<String> expectedResult) {
        super(getMethod);
        this.expectedResult = expectedResult;
        setupWiremock(url, resultStatus, returnBody);
    }

    @Override
    public void runTest() {
        assertEquals(getMethod.get(), expectedResult);
    }
}
