package com.github.dbchar.zoomapi.utils;

import com.google.gson.Gson;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

/**
 * Created by Junxian Chen on 2020-04-18.
 */
public class Logger {
    public static boolean DISABLED = true;

    public static String dateTime() {
        // yyyy-mm-ddTHH:mm:ss
        return LocalDateTime.now().toString().split("[.]")[0];
    }

    public static void logi(String s) {
        if (DISABLED) return;
        System.out.println(dateTime() + " INFO: " + s);
    }

    public static void logit(String s) {
        if (DISABLED) return;
        System.out.println(dateTime() + " INFO: " + Thread.currentThread().getName() + ": " + s);
    }

    public static void loge(String s) {
        if (DISABLED) return;
        System.err.println(dateTime() + " ERROR: " + s);
    }

    public static void loget(String s) {
        if (DISABLED) return;
        System.err.println(dateTime() + " ERROR: " + Thread.currentThread().getName() + ": " + s);
    }

    public static void printRequestAndResponse(HttpRequest request, HttpResponse response) {
        if (DISABLED) return;
        System.out.println("\n----------------------------");
        System.out.println("====== Http request ======");
        System.out.println("Request url: " + request.uri().toString());
        System.out.println("Request method: " + request.method());
        System.out.println("Request headers: " + request.headers().toString());
        System.out.println("====== Http response ======");
        System.out.println("Response Status code:" + response.statusCode());
        System.out.println("Response body:" + new Gson().toJson(response.body()));
        System.out.println("----------------------------\n");
    }

    public static void printSql(String sql) {
        if (DISABLED) return;
        System.out.println("\n----------------------------");
        System.out.println("[DB] Execute SQL: " + sql);
        System.out.println("----------------------------\n");
    }
}
