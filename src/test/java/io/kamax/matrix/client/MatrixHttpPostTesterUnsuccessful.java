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

import com.github.tomakehurst.wiremock.client.MappingBuilder;

import io.kamax.matrix.MatrixErrorInfo;

import java.util.Optional;
import java.util.function.Consumer;

import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

import static org.junit.Assert.*;

public class MatrixHttpPostTesterUnsuccessful extends AMatrixHttpConsumeTester {
    private final String errcode;
    private final String error;

    public MatrixHttpPostTesterUnsuccessful(Consumer<String> postMethod, String valueToConsume, String url,
            int returnStatus, String errcode, String error) {
        super(postMethod, valueToConsume);
        this.errcode = errcode;
        this.error = error;
        setupWiremock(url, returnStatus, getReturnBody());
    }

    @Override
    public void runTest() {
        try {
            consumeMethod.accept(valueToConsume);
        } catch (MatrixClientRequestException e) {
            Optional<MatrixErrorInfo> errorOptional = e.getError();
            assertTrue(errorOptional.isPresent());
            assertEquals(errorOptional.get().getErrcode(), errcode);
            assertEquals(errorOptional.get().getError(), error);
            return;
        }
        fail("In this case, an exception has to be thrown.");
    }

    private String getReturnBody() {
        return ("{'errcode': `" + errcode + "`, " + "error: `" + error + "`}").replace('`', '"');
    }

    @Override
    protected MappingBuilder createUrlMappingBuilder(String url) {
        return post(urlEqualTo(url));
    }

}
