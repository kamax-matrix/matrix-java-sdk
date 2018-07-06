/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Kamax Sarl
 *
 * https://www.kamax.io/
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

package io.kamax.matrix;

public interface _MatrixID {

    String getId();

    String getLocalPart();

    String getDomain();

    /**
     * Render this Matrix ID strictly valid. In technical term, transform this ID so
     * <code>isValid()</code> returns true.
     *
     * @return A canonical Matrix ID
     */
    _MatrixID canonicalize();

    /**
     * If the Matrix ID is strictly valid in the protocol as per
     * http://matrix.org/docs/spec/intro.html#user-identifiers
     *
     * @return true if strictly valid, false if not
     */
    boolean isValid();

    /**
     * If the Matrix ID is acceptable in the protocol as per
     * http://matrix.org/docs/spec/intro.html#historical-user-ids
     *
     * @return
     */
    boolean isAcceptable();

}
