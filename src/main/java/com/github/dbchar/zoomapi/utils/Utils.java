package com.github.dbchar.zoomapi.utils;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Junxian Chen on 2020-05-04.
 */
public class Utils {
    public static <T> List<T> diff(final Collection<T> minuend, final Collection<T> subtrahend) {
        // minuend - subtrahend = difference
        return minuend.stream()
                .filter(n -> !subtrahend.contains(n))
                .collect(Collectors.toList());
    }

    public static void openBrowser(String url) throws IOException {
        var osName = System.getProperty("os.name").toLowerCase();
        var command = "";
        if (osName.contains("windows")) {
            command = "rundll32 url.dll,FileProtocolHandler ";
        } else if (osName.contains("mac")) {
            command = "open ";
        } else if (osName.contains("nix") || osName.contains("nux")) {
            command = "xdg-open ";
        }
        if (!command.isEmpty()) {
            Runtime.getRuntime().exec(command + url);
        } else {
            System.out.println("Please open this URL with your browser: " + url);
        }
    }
}
