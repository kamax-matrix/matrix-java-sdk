/*
 * matrix-java-sdk - Matrix Client SDK for Java
 * Copyright (C) 2017 Maxime Dor
 *
 * https://www.kamax.io/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.matrix.client;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import io.kamax.matrix.MatrixErrorInfo;
import io.kamax.matrix._MatrixID;
import io.kamax.matrix.hs._MatrixHomeserver;
import io.kamax.matrix.json.GsonUtil;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AMatrixHttpClient implements _MatrixClientRaw {

    private Logger log = LoggerFactory.getLogger(AMatrixHttpClient.class);

    protected MatrixClientContext context = new MatrixClientContext();

    protected Gson gson = new Gson();
    protected JsonParser jsonParser = new JsonParser();
    private CloseableHttpClient client = HttpClients.createDefault();

    private Pattern accessTokenUrlPattern = Pattern.compile("\\?access_token=(?<token>[^&]*)");

    public AMatrixHttpClient(String domain) {
        context.setDomain(domain);
    }

    public AMatrixHttpClient(URL hsBaseUrl) {
        context.setHsBaseUrl(hsBaseUrl);
    }

    protected AMatrixHttpClient(MatrixClientContext context) {
        this.context = new MatrixClientContext(context);
    }

    @Override
    public Optional<_AutoDiscoverySettings> discoverSettings() {
        if (StringUtils.isBlank(context.getDomain())) {
            throw new IllegalStateException("A non-empty Matrix domain must be set to discover the client settings");
        }

        try {
            String hostname = context.getDomain().split(":")[0];
            log.info("Performing .well-known auto-discovery for {}", hostname);

            URIBuilder builder = new URIBuilder();
            builder.setScheme("https");
            builder.setHost(hostname);
            builder.setPath("/.well-known/matrix/client");
            HttpGet req = new HttpGet(builder.build());
            String body = execute(new MatrixHttpRequest(req).addIgnoredErrorCode(404));
            if (StringUtils.isBlank(body)) {
                if (Objects.isNull(context.getHsBaseUrl())) {
                    throw new IllegalStateException("No valid Homeserver base URL was found");
                }

                // No .well-known data found
                // FIXME improve SDK so we can differentiate between not found and empty.
                // not found = skip
                // empty = failure
                return Optional.empty();
            }

            log.info("Found body: {}", body);

            WellKnownAutoDiscoverySettings settings = new WellKnownAutoDiscoverySettings(body);
            log.info("Found .well-known data");

            // TODO reconsider if and where we should check for an already present HS url in the context
            if (settings.getHsBaseUrls().isEmpty()) {
                throw new IllegalStateException("No valid Homeserver base URL was found");
            }

            for (URL baseUrlCandidate : settings.getHsBaseUrls()) {
                context.setHsBaseUrl(baseUrlCandidate);
                try {
                    if (!getHomeApiVersions().isEmpty()) {
                        log.info("Found a valid HS at {}", getContext().getHsBaseUrl().toString());
                        break;
                    }
                } catch (MatrixClientRequestException e) {
                    log.warn("Error when trying to fetch {}: {}", baseUrlCandidate, e.getMessage());
                }
            }

            for (URL baseUrlCandidate : settings.getIsBaseUrls()) {
                context.setIsBaseUrl(baseUrlCandidate);
                try {
                    if (validateIsBaseUrl()) {
                        log.info("Found a valid IS at {}", getContext().getHsBaseUrl().toString());
                        break;
                    }
                } catch (MatrixClientRequestException e) {
                    log.warn("Error when trying to fetch {}: {}", baseUrlCandidate, e.getMessage());
                }
            }

            return Optional.of(settings);
        } catch (URISyntaxException e) { // The domain was invalid when used in a URL
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public MatrixClientContext getContext() {
        return context;
    }

    @Override
    public _MatrixHomeserver getHomeserver() {
        return context.getHomeserver();
    }

    @Override
    public Optional<String> getAccessToken() {
        return Optional.ofNullable(context.getToken());
    }

    public String getAccessTokenOrThrow() {
        return getAccessToken()
                .orElseThrow(() -> new IllegalStateException("This method can only be used with a valid token."));
    }

    @Override
    public List<String> getHomeApiVersions() {
        String body = execute(new HttpGet(getPath("client", "", "versions")));
        return GsonUtil.asList(GsonUtil.parseObj(body), "versions", String.class);
    }

    @Override
    public boolean validateIsBaseUrl() {
        String body = execute(new HttpGet(getIdentityPath("identity", "api", "/v1")));
        return "{}".equals(body);
    }

    @Override
    public Optional<_MatrixID> getUser() {
        return context.getUser();
    }

    protected String execute(HttpRequestBase request) {
        return execute(new MatrixHttpRequest(request));
    }

    protected String execute(MatrixHttpRequest matrixRequest) {
        log(matrixRequest.getHttpRequest());
        try (CloseableHttpResponse response = client.execute(matrixRequest.getHttpRequest())) {

            String body = getBody(response.getEntity());
            int responseStatus = response.getStatusLine().getStatusCode();

            if (responseStatus == 200) {
                log.debug("Request successfully executed.");
            } else if (matrixRequest.getIgnoredErrorCodes().contains(responseStatus)) {
                log.debug("Error code ignored: " + responseStatus);
                return "";
            } else {
                MatrixErrorInfo info = createErrorInfo(body, responseStatus);

                body = handleError(matrixRequest, responseStatus, info);
            }
            return body;

        } catch (IOException e) {
            throw new MatrixClientRequestException(e);
        }
    }

    /**
     * Default handling of errors. Can be overwritten by a custom implementation in inherited classes.
     *
     * @param matrixRequest
     * @param responseStatus
     * @param info
     * @return body of the response of a repeated call of the request, else this methods throws a
     *         MatrixClientRequestException
     */
    protected String handleError(MatrixHttpRequest matrixRequest, int responseStatus, MatrixErrorInfo info) {
        String message = String.format("Request failed with status code: %s", responseStatus);

        if (responseStatus == 429) {
            return handleRateLimited(matrixRequest, info);
        }

        throw new MatrixClientRequestException(info, message);
    }

    /**
     * Default handling of rate limited calls. Can be overwritten by a custom implementation in inherited classes.
     *
     * @param matrixRequest
     * @param info
     * @return body of the response of a repeated call of the request, else this methods throws a
     *         MatrixClientRequestException
     */
    protected String handleRateLimited(MatrixHttpRequest matrixRequest, MatrixErrorInfo info) {
        throw new MatrixClientRequestException(info, "Request was rate limited.");
        // TODO Add default handling of rate limited call, i.e. repeated call after given time interval.
        // 1. Wait for timeout
        // 2. return execute(request)
    }

    protected MatrixHttpContentResult executeContentRequest(MatrixHttpRequest matrixRequest) {
        log(matrixRequest.getHttpRequest());
        try (CloseableHttpResponse response = client.execute(matrixRequest.getHttpRequest())) {

            HttpEntity entity = response.getEntity();
            int responseStatus = response.getStatusLine().getStatusCode();

            MatrixHttpContentResult result = new MatrixHttpContentResult(response);

            if (responseStatus == 200) {
                log.debug("Request successfully executed.");

                if (entity == null) {
                    log.debug("No data received.");
                } else if (entity.getContentType() == null) {
                    log.debug("No content type was given.");
                }

            } else if (matrixRequest.getIgnoredErrorCodes().contains(responseStatus)) {
                log.debug("Error code ignored: " + responseStatus);
            } else {
                String body = getBody(entity);
                MatrixErrorInfo info = createErrorInfo(body, responseStatus);

                result = handleErrorContentRequest(matrixRequest, responseStatus, info);
            }
            return result;

        } catch (IOException e) {
            throw new MatrixClientRequestException(e);
        }
    }

    protected MatrixHttpContentResult handleErrorContentRequest(MatrixHttpRequest matrixRequest, int responseStatus,
            MatrixErrorInfo info) {
        String message = String.format("Request failed with status code: %s", responseStatus);

        if (responseStatus == 429) {
            return handleRateLimitedContentRequest(matrixRequest, info);
        }

        throw new MatrixClientRequestException(info, message);
    }

    protected MatrixHttpContentResult handleRateLimitedContentRequest(MatrixHttpRequest matrixRequest,
            MatrixErrorInfo info) {
        throw new MatrixClientRequestException(info, "Request was rate limited.");
        // TODO Add default handling of rate limited call, i.e. repeated call after given time interval.
        // 1. Wait for timeout
        // 2. return execute(request)
    }

    protected Optional<String> extractAsStringFromBody(String body, String jsonObjectName) {
        if (StringUtils.isNotEmpty(body)) {
            return Optional.of(new JsonParser().parse(body).getAsJsonObject().get(jsonObjectName).getAsString());
        }
        return Optional.empty();
    }

    private String getBody(HttpEntity entity) throws IOException {
        Charset charset = ContentType.getOrDefault(entity).getCharset();
        return IOUtils.toString(entity.getContent(), charset);
    }

    private MatrixErrorInfo createErrorInfo(String body, int responseStatus) {
        MatrixErrorInfo info = gson.fromJson(body, MatrixErrorInfo.class);
        log.debug("Request returned with an error. Status code: {}, errcode: {}, error: {}", responseStatus,
                info.getErrcode(), info.getError());
        return info;
    }

    private void log(HttpRequestBase req) {
        String reqUrl = req.getURI().toASCIIString();
        Matcher m = accessTokenUrlPattern.matcher(reqUrl);
        if (m.find()) {
            StringBuilder b = new StringBuilder();
            b.append(reqUrl.substring(0, m.start("token")));
            b.append("<redacted>");
            b.append(reqUrl.substring(m.end("token"), reqUrl.length()));
            reqUrl = b.toString();
        }

        log.debug("Doing {} {}", req.getMethod(), reqUrl);
    }

    protected URIBuilder getPathBuilder(URIBuilder base, String module, String version, String action) {
        base.setPath(base.getPath() + "/_matrix/" + module + "/" + version + action);
        if (context.isVirtual()) {
            context.getUser().ifPresent(user -> base.setParameter("user_id", user.getId()));
        }

        return base;
    }

    protected URIBuilder getPathBuilder(String module, String version, String action) {
        return getPathBuilder(context.getHomeserver().getBaseEndpointBuilder(), module, version, action);
    }

    protected URIBuilder getIdentityPathBuilder(String module, String version, String action) {
        return getPathBuilder(new URIBuilder(URI.create(context.getIsBaseUrl().toString())), module, version, action);
    }

    protected URI getPath(String module, String version, String action) {
        try {
            return getPathBuilder(module, version, action).build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected URI getIdentityPath(String module, String version, String action) {
        try {
            return getIdentityPathBuilder(module, version, action).build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected URIBuilder getClientPathBuilder(String action) {
        return getPathBuilder("client", "r0", action);
    }

    protected URIBuilder getMediaPathBuilder(String action) {
        return getPathBuilder("media", "v1", action);
    }

    protected URI getWithAccessToken(URIBuilder builder) {
        try {
            builder.setParameter("access_token", getAccessTokenOrThrow());
            return builder.build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected URI getClientPathWithAccessToken(String action) {
        return getWithAccessToken(getClientPathBuilder(action));
    }

    protected URI getClientPath(String action) {
        try {
            return getClientPathBuilder(action).build();
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected URI getMediaPath(String action) {
        return getWithAccessToken(getMediaPathBuilder(action));
    }

    protected HttpEntity getJsonEntity(Object o) {
        return EntityBuilder.create().setText(gson.toJson(o)).setContentType(ContentType.APPLICATION_JSON).build();
    }
}
