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

import static org.junit.Assert.fail;

public class MatrixHttpGetTesterUnsuccessful extends AMatrixHttpGetTester {

    private final String errcode;
    private final String error;

    public MatrixHttpGetTesterUnsuccessful(Supplier<Optional<String>> getMethod, String url, int returnStatus,
            String errcode, String error) {
        super(getMethod);
        this.errcode = errcode;
        this.error = error;
        setupWiremock(url, returnStatus, getReturnBody());
    }

    @Override
    public void runTest() {
        try {
            getMethod.get();
        } catch (MatrixClientRequestException e) {
            /*
             * TODO refactor error handling, so that the error is returned. Afterwards, the error
             * values can be checked here by using e.getError().
             */
            // assertThat(e.getMessage(), containsString(errcode));
            // assertThat(e.getMessage(), containsString(error));
            return;
        }
        fail("In this case, an exception has to be thrown.");
    }

    private String getReturnBody() {
        return ("{'errcode': `" + errcode + "`, " + "error: `" + error + "`}").replace('`', '"');
    }
}
