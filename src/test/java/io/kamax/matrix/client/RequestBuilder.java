package io.kamax.matrix.client;

public class RequestBuilder {
    private final String url;

    public RequestBuilder(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

}
