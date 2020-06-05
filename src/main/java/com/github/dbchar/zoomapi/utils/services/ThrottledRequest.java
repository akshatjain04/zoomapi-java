package com.github.dbchar.zoomapi.utils.services;

import com.github.dbchar.zoomapi.network.ApiRequest;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Consumer;

/**
 * Created by Junxian Chen on 2020-05-07.
 */
@Deprecated
public class ThrottledRequest {
    private final HttpRequest request;
    private final long interval;
    private final Consumer<HttpResponse<String>> responseHandler;

    private HttpResponse<String> response = null;

    public ThrottledRequest(HttpRequest request, long interval, Consumer<HttpResponse<String>> responseHandler) {
        this.request = request;
        this.interval = interval;
        this.responseHandler = responseHandler;
    }

    public ThrottledRequest(HttpRequest request, long interval) {
        this(request, interval, (res) -> {
        });
    }

    public ThrottledRequest(ApiRequest apiRequest, Consumer<HttpResponse<String>> responseHandler) throws Exception {
        this(apiRequest.toHttpRequest(), apiRequest.getIntervalMs(), responseHandler);
    }

    public ThrottledRequest(ApiRequest apiRequest) throws Exception {
        this(apiRequest.toHttpRequest(), apiRequest.getIntervalMs());
    }

    public HttpRequest getRequest() {
        return request;
    }

    public long getInterval() {
        return interval;
    }

    public HttpResponse<String> getResponse() {
        return response;
    }

    public void setResponse(HttpResponse<String> response) {
        this.response = response;
    }

    public void handleResponse(HttpResponse<String> response) {
        responseHandler.accept(response);
    }
}
