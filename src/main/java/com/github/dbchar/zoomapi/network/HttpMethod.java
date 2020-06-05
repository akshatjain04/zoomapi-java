package com.github.dbchar.zoomapi.network;

public enum HttpMethod {
    GET("GET"), POST("POST"), PATCH("PATCH"), DELETE("DELETE"), PUT("PUT");

    private final String name;

    HttpMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
