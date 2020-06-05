package com.github.dbchar.zoomapi.components;

import com.github.dbchar.zoomapi.components.queries.PageConfiguration;
import com.github.dbchar.zoomapi.network.ApiClient;
import com.github.dbchar.zoomapi.network.ApiRequest;
import com.github.dbchar.zoomapi.network.ApiResponse;
import com.github.dbchar.zoomapi.utils.Result;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Junxian Chen on 2020-04-14.
 */
public class BaseComponent {
    public static final String BASE_URL = "https://api.zoom.us/v2";
    public static final String QUERY_PARAMETER_PAGE_SIZE = "page_size";
    public static final String QUERY_PARAMETER_NEXT_PAGE_TOKEN = "next_page_token";

    private String clientId; // for caching
    private String accessToken = "";

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public ApiResponse request(ApiRequest request) {
        request.setHeader("Authorization", "Bearer " + accessToken);
        return ApiClient.INSTANCE.request(request);
    }

    public Map<String, Object> makePageQueries(PageConfiguration pageConfiguration) {
        var queries = new HashMap<String, Object>();
        queries.put(QUERY_PARAMETER_PAGE_SIZE, pageConfiguration.getPageSize());
        if (pageConfiguration.getNextPageToken() != null) {
            queries.put(QUERY_PARAMETER_NEXT_PAGE_TOKEN, pageConfiguration.getNextPageToken());
        }
        return queries;
    }

    public String retrieveErrorMessage(ApiResponse response) {
        return response.isSuccess() ? null : response.getErrorMessage();
    }

    public Result<Boolean> requestAndResponseBooleanResult(ApiRequest request) {
        var response = request(request);
        return new Result<>(response.isSuccess(), retrieveErrorMessage(response));
    }
}
