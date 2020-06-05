package com.github.dbchar.zoomapi.utils.services;

import com.github.dbchar.zoomapi.utils.Logger;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Junxian Chen on 2020-05-06.
 */
@Deprecated
public enum ThrottlerService {
    INSTANCE;

    private final HttpClient client = HttpClient.newHttpClient();

    private final Queue<ThrottledRequest> requestQueue = new LinkedBlockingQueue<>();

    private boolean running;

    private final Thread requestDispatcher = new Thread(() -> {
        // finish all requests before exiting
        // to ensure monitor can also exit
        while (running || !requestQueue.isEmpty()) {
            if (!requestQueue.isEmpty()) {
                try {
                    var request = requestQueue.remove();

                    var response = client.send(request.getRequest(), HttpResponse.BodyHandlers.ofString());
                    request.setResponse(response);
                    request.handleResponse(response);

                    var sleepTime = request.getInterval();
                    Logger.logi("Slowing down by " + sleepTime + " ms");
                    Thread.sleep(sleepTime);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("Throttler stopped.");
    });

    ThrottlerService() {
//    start();
    }

    public void start() {
        running = true;
        requestDispatcher.start();
    }

    public void stop() {
        running = false;
    }

    public synchronized HttpResponse<String> enqueueBlocking(ThrottledRequest request) {
        requestQueue.add(request);

        while (request.getResponse() == null) Thread.onSpinWait();

        return request.getResponse();
    }

    public void enqueueAsync(ThrottledRequest request) {
        requestQueue.add(request);
    }
}
