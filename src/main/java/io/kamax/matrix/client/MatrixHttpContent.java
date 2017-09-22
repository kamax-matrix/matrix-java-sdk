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

import io.kamax.matrix.MatrixErrorInfo;
import io.kamax.matrix._MatrixContent;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatrixHttpContent extends AMatrixHttpClient implements _MatrixContent {

    private Logger log = LoggerFactory.getLogger(MatrixHttpContent.class);

    private final Pattern filenamePattern = Pattern.compile("filename=\"?(?<filename>.+)\"?;?");

    private URI address;
    private String type;
    private byte[] data;
    private String filename;

    private boolean isLoaded = false;
    private boolean isValid = false;

    public MatrixHttpContent(MatrixClientContext context, URI address) {
        super(context);
        this.address = address;
    }

    // TODO switch a HTTP HEAD to fetch initial data, instead of loading in memory directly
    private synchronized void load() {
        if (isLoaded) {
            return;
        }

        try {
            if (!StringUtils.equalsIgnoreCase("mxc", address.getScheme())) {
                log.error("{} is not a supported protocol for avatars, ignoring",
                        address.getScheme());
            } else {
                URI path = getMediaPath("/download/" + address.getHost() + address.getPath());
                try (CloseableHttpResponse res = client.execute(log(new HttpGet(path)))) {
                    if (res.getStatusLine().getStatusCode() != 200) {
                        if (res.getStatusLine().getStatusCode() == 404) {
                            log.info("Media {} does not exist on the HS {}", address.toString(),
                                    getContext().getHs().getDomain());
                        } else {
                            Charset charset = ContentType.getOrDefault(res.getEntity())
                                    .getCharset();
                            String body = IOUtils.toString(res.getEntity().getContent(), charset);

                            MatrixErrorInfo info = gson.fromJson(body, MatrixErrorInfo.class);
                            log.error("Couldn't get content data for {}: {} - {}",
                                    address.toString(), info.getErrcode(), info.getError());
                        }
                    } else {
                        HttpEntity entity = res.getEntity();
                        if (entity == null) {
                            log.info("No data for content {}", address.toString());
                        } else {
                            Header contentType = entity.getContentType();
                            if (contentType == null) {
                                log.info(
                                        "No content type was given, unable to process avatar data");
                            } else {
                                type = contentType.getValue();
                                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                                entity.writeTo(outStream);
                                data = outStream.toByteArray();
                                isValid = true;

                                Header contentDisposition = res
                                        .getFirstHeader("Content-Disposition");
                                if (contentDisposition != null) {
                                    Matcher m = filenamePattern
                                            .matcher(contentDisposition.getValue());
                                    if (m.find()) {
                                        filename = m.group("filename");
                                    }
                                }
                            }
                        }
                    }
                }
            }

            isLoaded = true;
        } catch (IOException e) {
            throw new MatrixClientRequestException(e);
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

        return isValid ? type : null;
    }

    @Override
    public byte[] getData() {
        load();

        return isValid ? data : null;
    }

    @Override
    public Optional<String> getFilename() {
        load();

        return Optional.ofNullable(filename);
    }

}
