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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.matrix.client;

import io.kamax.matrix._MatrixContent;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatrixHttpContent extends AMatrixHttpClient implements _MatrixContent {

    private Logger log = LoggerFactory.getLogger(MatrixHttpContent.class);

    private final Pattern filenamePattern = Pattern.compile("filename=\"?(?<filename>[^\";]+)");

    private URI address;

    private Optional<MatrixHttpContentResult> result = Optional.empty();

    private boolean isValid = false;

    public MatrixHttpContent(MatrixClientContext context, URI address) {
        super(context);
        this.address = address;
    }

    // TODO switch a HTTP HEAD to fetch initial data, instead of loading in memory directly
    private synchronized void load() {
        if (result.isPresent()) {
            return;
        }

        // TODO do we need this try catch block? verify with test classes
        try {
            if (!StringUtils.equalsIgnoreCase("mxc", address.getScheme())) {
                log.error("{} is not a supported protocol for avatars, ignoring", address.getScheme());
            } else {
                URI path = getMediaPath("/download/" + address.getHost() + address.getPath());

                MatrixHttpRequest request = new MatrixHttpRequest(new HttpGet(path));
                result = Optional.of(executeContentRequest(request));
                if (result.isPresent()) {
                    isValid = result.get().getContentType().isPresent();
                }
            }

        } catch (MatrixClientRequestException e) {
            isValid = false;
        }
    }

    @Override
    public URI getAddress() {
        return address;
    }

    @Override
    public boolean isValid() {
        load();

        return isValid;
    }

    @Override
    public String getType() {
        load();
        if (result.isPresent()) {
            Optional<Header> contentType = result.get().getContentType();
            if (contentType.isPresent()) {
                return contentType.get().getValue();
            }
        }
        // TODO return Optional?
        return null;
    }

    @Override
    public byte[] getData() {
        load();

        // TODO return Optional?
        return isValid && result.isPresent() ? result.get().getData() : null;
    }

    @Override
    public Optional<String> getFilename() {
        load();

        if (isValid && result.isPresent()) {
            Optional<Header> contentDisposition = result.get().getHeader("Content-Disposition");
            if (contentDisposition.isPresent()) {
                Matcher m = filenamePattern.matcher(contentDisposition.get().getValue());
                if (m.find()) {
                    return Optional.of(m.group("filename"));
                }
            }
        }

        return Optional.empty();
    }

}
