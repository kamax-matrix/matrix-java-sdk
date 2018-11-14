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

package io.kamax.matrix.client;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import io.kamax.matrix.MatrixErrorInfo;
import io.kamax.matrix._MatrixID;
import io.kamax.matrix.hs._MatrixHomeserver;
import io.kamax.matrix.json.GsonUtil;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java8.util.Optional;


import okhttp3.*;

public abstract class AMatrixHttpClient implements _MatrixClientRaw {

    private Logger log = LoggerFactory.getLogger(AMatrixHttpClient.class);

    protected MatrixClientContext context;

    protected Gson gson = GsonUtil.get();
    protected JsonParser jsonParser = new JsonParser();
    private OkHttpClient client;

    private Pattern accessTokenUrlPattern = Pattern.compile("\\?access_token=(?<token>[^&]*)");

    public AMatrixHttpClient(String domain) {
        this(new MatrixClientContext().setDomain(domain));
    }

    public AMatrixHttpClient(URL hsBaseUrl) {
        this(new MatrixClientContext().setHsBaseUrl(hsBaseUrl));
    }

    protected AMatrixHttpClient(MatrixClientContext context) {
        this(context, new OkHttpClient.Builder(), new MatrixClientDefaults());
    }

    protected AMatrixHttpClient(MatrixClientContext context, OkHttpClient.Builder client) {
        this(context, client, new MatrixClientDefaults());
    }

    protected AMatrixHttpClient(MatrixClientContext context, OkHttpClient.Builder client,
            MatrixClientDefaults defaults) {
        this(context, client.connectTimeout(defaults.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(5, TimeUnit.MINUTES).followRedirects(false).build());
    }

    protected AMatrixHttpClient(MatrixClientContext context, OkHttpClient client) {
        this.context = context;
        this.client = client;
    }

    @Override
    public Optional<_AutoDiscoverySettings> discoverSettings() {
        if (StringUtils.isBlank(context.getDomain())) {
            throw new IllegalStateException("A non-empty Matrix domain must be set to discover the client settings");
        }

        String hostname = context.getDomain().split(":")[0];
        log.info("Performing .well-known auto-discovery for {}", hostname);

        URL url = new HttpUrl.Builder().scheme("https").host(hostname).addPathSegments(".well-known/matrix/client")
                .build().url();
        String body = execute(new MatrixHttpRequest(new Request.Builder().get().url(url)).addIgnoredErrorCode(404));
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
        String body = execute(new Request.Builder().get().url(getPath("client", "", "versions")));
        return GsonUtil.asList(GsonUtil.parseObj(body), "versions", String.class);
    }

    @Override
    public boolean validateIsBaseUrl() {
        String body = execute(new Request.Builder().get().url(getIdentityPath("identity", "api", "/v1")));
        return "{}".equals(body);
    }

    protected String getUserId() {
        return getUser().orElseThrow(IllegalStateException::new).getId();
    }

    @Override
    public Optional<_MatrixID> getUser() {
        return context.getUser();
    }

    protected Request.Builder addAuthHeader(Request.Builder builder) {
        builder.addHeader("Authorization", "Bearer " + getAccessTokenOrThrow());
        return builder;
    }

    protected String executeAuthenticated(Request.Builder builder) {
        return execute(addAuthHeader(builder));
    }

    protected String executeAuthenticated(MatrixHttpRequest matrixRequest) {
        addAuthHeader(matrixRequest.getHttpRequest());
        return execute(matrixRequest);
    }

    protected String execute(Request.Builder builder) {
        return execute(new MatrixHttpRequest(builder));
    }

    protected String execute(MatrixHttpRequest matrixRequest) {
        log(matrixRequest.getHttpRequest());
        try (Response response = client.newCall(matrixRequest.getHttpRequest().build()).execute()) {
            String body = response.body().string();
            int responseStatus = response.code();

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

    protected MatrixHttpContentResult executeContentRequest(MatrixHttpRequest matrixRequest) {
        log(matrixRequest.getHttpRequest());
        try (Response response = client.newCall(matrixRequest.getHttpRequest().build()).execute()) {
            int responseStatus = response.code();

            MatrixHttpContentResult result;

            if (responseStatus == 200 || matrixRequest.getIgnoredErrorCodes().contains(responseStatus)) {
                log.debug("Request successfully executed.");
                result = new MatrixHttpContentResult(response);
            } else {
                String body = response.body().string();
                MatrixErrorInfo info = createErrorInfo(body, responseStatus);

                result = handleErrorContentRequest(matrixRequest, responseStatus, info);
            }
            return result;
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
        String message = String.format("Request failed: %s", responseStatus);

        if (Objects.nonNull(info)) {
            message = String.format("%s - %s - %s", message, info.getErrcode(), info.getError());
        }

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
        if (StringUtils.isEmpty(body)) {
            return Optional.empty();
        }

        return GsonUtil.findString(jsonParser.parse(body).getAsJsonObject(), jsonObjectName);
    }

    private MatrixErrorInfo createErrorInfo(String body, int responseStatus) {
        try {
            MatrixErrorInfo info = gson.fromJson(body, MatrixErrorInfo.class);
            log.debug("Request returned with an error. Status code: {}, errcode: {}, error: {}", responseStatus,
                    info.getErrcode(), info.getError());
            return info;
        } catch (JsonSyntaxException e) {
            log.debug("Unable to parse Matrix error info. Content was:\n{}", body);
            return null;
        }
    }

    private void log(Request.Builder req) {
        String reqUrl = req.toString();
        Matcher m = accessTokenUrlPattern.matcher(reqUrl);
        if (m.find()) {
            StringBuilder b = new StringBuilder();
            b.append(reqUrl, 0, m.start("token"));
            b.append("<redacted>");
            b.append(reqUrl.substring(m.end("token")));
            reqUrl = b.toString();
        }

        log.debug("Doing {} {}", req, reqUrl);
    }

    protected HttpUrl.Builder getHsBaseUrl() {
        return HttpUrl.get(context.getHsBaseUrl()).newBuilder();
    }

    protected HttpUrl.Builder getIsBaseUrl() {
        return HttpUrl.get(context.getIsBaseUrl()).newBuilder();
    }

    protected HttpUrl.Builder getPathBuilder(HttpUrl.Builder base, String... segments) {
        base.addPathSegment("_matrix");
        for (String segment : segments) {
            base.addPathSegment(segment);
        }

        if (context.isVirtual()) {
            context.getUser().ifPresent(user -> base.addQueryParameter("user_id", user.getId()));
        }
        return base;
    }

    protected HttpUrl.Builder getPathBuilder(String... segments) {
        return getPathBuilder(getHsBaseUrl(), segments);
    }

    protected HttpUrl.Builder getIdentityPathBuilder(String... segments) {
        return getPathBuilder(getIsBaseUrl(), segments);
    }

    protected URL getPath(String... segments) {
        return getPathBuilder(segments).build().url();
    }

    protected URL getIdentityPath(String... segments) {
        return getIdentityPathBuilder(segments).build().url();
    }

    protected HttpUrl.Builder getClientPathBuilder(String... segments) {
        String[] base = { "client", "r0" };
        segments = ArrayUtils.addAll(base, segments);
        return getPathBuilder(segments);
    }

    protected HttpUrl.Builder getMediaPathBuilder(String... segments) {
        String[] base = { "media", "r0" };
        segments = ArrayUtils.addAll(base, segments);
        return getPathBuilder(segments);
    }

    protected URL getClientPath(String... segments) {
        return getClientPathBuilder(segments).build().url();
    }

    protected URL getMediaPath(String... segments) {
        return getMediaPathBuilder(segments).build().url();
    }

    protected RequestBody getJsonBody(Object o) {
        return RequestBody.create(MediaType.parse("application/json"), GsonUtil.get().toJson(o));
    }

    protected Request.Builder request(URL url) {
        return new Request.Builder().url(url);
    }

    protected Request.Builder getRequest(URL url) {
        return request(url).get();
    }

}
