/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Maxime Dor
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.matrix;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MatrixIDTest {

    private static String validMxId1 = "@john.doe:example.org";
    private static String validMxId2 = "@john.doe:example.com";
    private static String validMxId3 = "@JoHn.dOe:ExamPLE.ORG";

    private static String invalidMxId1 = "john.doe:example.org";
    private static String invalidMxId2 = "@john.doeexample.org";
    private static String invalidMxId3 = "john.doe";
    private static String invalidMxId4 = "@:";
    private static String invalidMxId5 = "@john.doe:";
    private static String invalidMxId6 = "@:example.org";


    @Test
    public void validMatrixIDs() {
        _MatrixID mxId1 = new MatrixID(validMxId1);
        _MatrixID mxId3 = new MatrixID(validMxId3);
        assertTrue(validMxId1.contentEquals(mxId1.getId()));
        assertTrue("john.doe".contentEquals(mxId1.getLocalPart()));
        assertTrue("example.org".contentEquals(mxId1.getDomain()));

        assertTrue(validMxId1.contentEquals(mxId3.getId()));
        assertTrue("john.doe".contentEquals(mxId3.getLocalPart()));
        assertTrue("example.org".contentEquals(mxId3.getDomain()));
    }

    @Test
    public void validateEqual() {
        _MatrixID mxId1 = new MatrixID(validMxId1);
        _MatrixID mxId2 = new MatrixID(validMxId1);
        _MatrixID mxId3 = new MatrixID(validMxId2);

        assertTrue(mxId1.equals(mxId2));
        assertTrue(mxId2.equals(mxId1));
        assertTrue(!mxId1.equals(mxId3));
        assertTrue(!mxId2.equals(mxId3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidMatrixIDs1() {
        new MatrixID(invalidMxId1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidMatrixIDs2() {
        new MatrixID(invalidMxId2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidMatrixIDs3() {
        new MatrixID(invalidMxId3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidMatrixIDs4() {
        new MatrixID(invalidMxId4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidMatrixIDs5() {
        new MatrixID(invalidMxId5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidMatrixIDs6() {
        new MatrixID(invalidMxId6);
    }

}
