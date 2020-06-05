package com.github.dbchar.zoomapi.network;

import com.google.gson.Gson;

public class ApiResponse {
    private final int statusCode;
    private final boolean isSuccess;
    private final String json;

    public ApiResponse(int statusCode, boolean isSuccess, String json) {
        this.statusCode = statusCode;
        this.isSuccess = isSuccess;
        this.json = json;
    }

    public String getJson() {
        return json;
    }

    public String getErrorMessage() {
        try {
            var response = new Gson().fromJson(json, ErrorResponse.class);
            return "Status Code: " + statusCode + ", Error: " + response.getMessage();
        } catch (Exception e) {
            return "Status Code: " + statusCode;
        }
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    private static class ErrorResponse {
        private String code;
        private String message;

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}