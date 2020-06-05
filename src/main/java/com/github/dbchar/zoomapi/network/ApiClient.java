package com.github.dbchar.zoomapi.network;

import com.github.dbchar.zoomapi.utils.Logger;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Map;

import static com.github.dbchar.zoomapi.utils.Logger.logi;

public enum ApiClient {
    INSTANCE;

    public static final int RESPONSE_CODE_EXCEPTION = -1;
    public static final int CODE_EXCEPTION = -2;

    private final HttpClient client = HttpClient.newHttpClient();
    private long lastRequestTimestamp = 0;

    public synchronized ApiResponse request(ApiRequest apiRequest) {
        String json;
        int responseCode;
        boolean isSuccess;

        try {
            // We actually have a special class for throttling, which is ThrottlerService
            // But that is optional because we can use throttledSend instead to make it cleaner
            var httpResponse = throttledSend(apiRequest);
            Logger.printRequestAndResponse(httpResponse.request(), httpResponse);

            responseCode = (httpResponse.statusCode());
            json = httpResponse.body();
            isSuccess = responseSuccess(responseCode);
        } catch (IOException | InterruptedException e) {
            responseCode = RESPONSE_CODE_EXCEPTION;
            json = new Gson().toJson(Map.of("code", RESPONSE_CODE_EXCEPTION, "message", e.getLocalizedMessage()));
            isSuccess = false;
        } catch (Exception e) {
            responseCode = CODE_EXCEPTION;
            json = new Gson().toJson(Map.of("code", CODE_EXCEPTION, "message", e.getLocalizedMessage()));
            isSuccess = false;
        }

        return new ApiResponse(responseCode, isSuccess, json);
    }

    private HttpResponse<String> throttledSend(ApiRequest apiRequest) throws Exception {
        var delta = System.currentTimeMillis() - lastRequestTimestamp;
        if (delta < apiRequest.getIntervalMs()) {
            var sleepTime = apiRequest.getIntervalMs() - delta;
            logi("Slowing down by " + sleepTime + " ms");
            Thread.sleep(sleepTime);
        }

        var httpResponse = client.send(apiRequest.toHttpRequest(), HttpResponse.BodyHandlers.ofString());
        lastRequestTimestamp = System.currentTimeMillis();
        return httpResponse;
    }

    private boolean responseSuccess(int responseCode) {
        return responseCode == HttpURLConnection.HTTP_OK
                || responseCode == HttpURLConnection.HTTP_CREATED
                || responseCode == HttpURLConnection.HTTP_ACCEPTED
                || responseCode == HttpURLConnection.HTTP_NOT_AUTHORITATIVE
                || responseCode == HttpURLConnection.HTTP_NO_CONTENT;
    }
}