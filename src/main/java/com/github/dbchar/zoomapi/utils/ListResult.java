package com.github.dbchar.zoomapi.utils;

import com.github.dbchar.zoomapi.clients.ZoomClient;

import java.util.List;

public class ListResult<T> {
    private final List<T> items;
    private final String errorMessage;

    public ListResult(List<T> items, String errorMessage) {
        this.items = items;
        this.errorMessage = errorMessage;
    }

    public List<T> getItems() {
        return items;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccess() {
        return errorMessage == null;
    }

    public boolean isSuccessOrRefreshToken(ZoomClient client) {
        if (errorMessage == null) return true;

        if (errorMessage.contains("401")) {
            try {
                System.out.println("Token expired. Trying to get access token from server...");
                client.refreshAccessToken();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
