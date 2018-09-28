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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatrixID implements _MatrixID {

    public static class Builder {

        private MatrixID mxId;

        public Builder(String id) {
            mxId = MatrixID.parse(id);
        }

        public MatrixID valid() {
            if (!mxId.isValid()) {
                throw new IllegalArgumentException(mxId + " is not a valid Matrix ID");
            }
            return mxId;
        }

        public MatrixID acceptable() {
            if (!mxId.isAcceptable()) {
                throw new IllegalArgumentException(mxId + " is not an acceptable Matrix ID");
            }
            return mxId;
        }

    }

    public static final String ALLOWED_CHARS = "0-9a-z-.=_/";
    public static final Pattern LAX_PATTERN = Pattern.compile("@(.*?):(.+)");
    public static final Pattern STRICT_PATTERN = Pattern.compile("@([" + ALLOWED_CHARS + "]+):(.+)");

    private String id;

    private String localpart;
    private String domain;

    private static String buildRaw(String localpart, String domain) {
        return "@" + localpart + ":" + domain;
    }

    private static MatrixID parse(String id) {
        Matcher m = LAX_PATTERN.matcher(id);
        if (!m.matches()) {
            throw new IllegalArgumentException(id + " is not a Matrix ID");
        }

        MatrixID mxId = new MatrixID();
        mxId.id = id;
        mxId.localpart = m.group(1);
        mxId.domain = m.group(2);
        return mxId;
    }

    public static Builder from(String id) {
        return new Builder(id);
    }

    public static Builder from(String local, String domain) {
        return from(buildRaw(local, domain));
    }

    public static MatrixID asValid(String id) {
        return new Builder(id).valid();
    }

    public static MatrixID asAcceptable(String local, String domain) {
        return from(local, domain).acceptable();
    }

    public static MatrixID asAcceptable(String id) {
        return from(id).acceptable();
    }

    private MatrixID() {
        // not for public consumption
    }

    private MatrixID(MatrixID mxId) {
        this.id = mxId.id;
        this.localpart = mxId.localpart;
        this.domain = mxId.domain;
    }

    @Deprecated
    public MatrixID(String mxId) {
        this(parse(mxId));
    }

    @Deprecated
    public MatrixID(String localpart, String domain) {
        this(parse(buildRaw(localpart, domain)));
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
    public MatrixID canonicalize() {
        return parse(getId().toLowerCase());
    }

    @Override
    public boolean isValid() {
        return isAcceptable() && STRICT_PATTERN.matcher(id).matches();
    }

    @Override
    public boolean isAcceptable() {
        // TODO properly implement

        return id.length() <= 255;
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

    @Override
    public String toString() {
        return getId();
    }

}
