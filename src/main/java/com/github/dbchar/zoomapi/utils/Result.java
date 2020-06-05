package com.github.dbchar.zoomapi.utils;

import com.github.dbchar.zoomapi.clients.ZoomClient;

public class Result<T> {
    private final T item;
    private final String errorMessage;

    public Result(T item, String errorMessage) {
        this.item = item;
        this.errorMessage = errorMessage;
    }

    public T getItem() {
        return item;
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
