package com.github.dbchar.zoomapi.network;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ApiRequest {
    private final String baseUrl;
    private final String path;
    private final HttpMethod httpMethod;
    private Map<String, Object> headers;
    private Map<String, Object> queries;
    private String payload;
    private final Duration timeout;
    private long intervalMs;

    private static final int TIMEOUT = 15;
    private static final long INTERVAL_MS = 2 * 1000;

    /***
     *
     * @param baseUrl The base url for remote API
     * @param path The path for the remote API
     * @param httpMethod The http method
     * @param headers The additional header fields
     * @param queries The query fields
     * @param payload Json format of the request body
     * @param intervalMs throttling interval in milliseconds
     * @param timeout Timeout of waiting for response
     */
    public ApiRequest(String baseUrl,
                      String path,
                      HttpMethod httpMethod,
                      Map<String, Object> headers,
                      Map<String, Object> queries,
                      String payload,
                      long intervalMs,
                      int timeout) {
        this.baseUrl = baseUrl;
        this.path = path;
        this.httpMethod = httpMethod;
        this.headers = headers;
        this.queries = queries;
        this.payload = payload;
        this.intervalMs = intervalMs;
        this.timeout = Duration.ofSeconds(timeout);
    }

    public ApiRequest(String baseUrl,
                      String path,
                      HttpMethod httpMethod,
                      Map<String, Object> headers,
                      Map<String, Object> queries,
                      String payload) {
        this(baseUrl, path, httpMethod, headers, queries, payload, INTERVAL_MS, TIMEOUT);
    }


    public ApiRequest(String baseUrl, String path, HttpMethod httpMethod) {
        this(baseUrl, path, httpMethod, new HashMap<>(), new HashMap<>(), null);
    }

    public String getUrl() {
        return baseUrl + "/" + path;
    }

    public String getHttpMethodName() {
        return httpMethod.getName();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public Map<String, Object> getQueries() {
        return queries;
    }

    public String getPayload() {
        return payload;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public void setQueries(Map<String, Object> queries) {
        this.queries = queries;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void setHeader(String key, Object value) {
        headers.put(key, value);
    }

    public void addQuery(String key, Object value) {
        queries.put(key, value);
    }

    public long getIntervalMs() {
        return intervalMs;
    }

    public void setIntervalMs(long intervalMs) {
        this.intervalMs = intervalMs;
    }

    public HttpRequest toHttpRequest() throws Exception {
        var requestUrl = this.getUrl();

        // configure queries
        var queries = this.getQueries();

        if (queries != null && queries.size() > 0) {
            requestUrl = configureQueries(requestUrl, queries);
        }

        // configure headers
        var headers = new ArrayList<String>();
        headers.add("Content-Type");
        headers.add("application/json");
        headers.add("Accept");
        headers.add("application/json");

        var additionalHeaders = this.getHeaders();
        if (additionalHeaders != null && additionalHeaders.size() > 0) {
            for (var key : additionalHeaders.keySet()) {
                headers.add(key);
                headers.add(String.valueOf(additionalHeaders.get(key)));
            }
        }

        var uri = URI.create(requestUrl);

        var payload = this.getPayload() == null ? "" : this.getPayload();
        return HttpRequest.newBuilder()
                .uri(uri)
                .timeout(timeout)
                .headers(headers.toArray(String[]::new))
                .method(this.getHttpMethodName(), HttpRequest.BodyPublishers.ofString(payload))
                .build();
    }

    private String encode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    private String configureQueries(String url, Map<String, Object> queries) throws UnsupportedEncodingException {
        var requestUrl = url;
        var queryString = new StringBuilder();

        for (var key : queries.keySet()) {
            queryString.append(encode(key))
                    .append("=")
                    .append(encode(String.valueOf(queries.get(key))))
                    .append("&");
        }

        requestUrl += "?" + queryString.toString();
        // remove last '&'
        requestUrl = requestUrl.substring(0, requestUrl.length() - 1);

        return requestUrl;
    }
}
