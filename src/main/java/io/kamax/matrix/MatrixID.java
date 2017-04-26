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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatrixID implements _MatrixID {

    // TODO fix to exactly match http://matrix.org/docs/spec/intro.html#user-identifiers
    private static Pattern pattern = Pattern.compile("@(.+?):(.+)");

    private String id;
    private String localpart;
    private String domain;


    public MatrixID(String mxId) {
        Matcher m = pattern.matcher(mxId);
        if (!m.matches()) {
            throw new IllegalArgumentException(mxId + " is not a valid Matrix ID");
        }

        this.id = mxId;
        this.localpart = m.group(1);
        this.domain = m.group(2);
    }

    public MatrixID(String localpart, String domain) {
        this.id = "@" + localpart + ":" + domain;
        this.localpart = localpart;
        this.domain = domain;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLocalPart() {
        return localpart;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatrixID)) return false;

        MatrixID matrixID = (MatrixID) o;

        return id.equals(matrixID.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
