package com.github.dbchar.zoomapi.utils.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Thread.sleep;

public enum MonitorService {
    INSTANCE;

    private ConcurrentHashMap<String, MonitorTask> tasks = new ConcurrentHashMap<>();

    private final int INTERVAL_MS = 10 * 1000;

    private boolean running = true;

    private final Thread monitorThread = new Thread(() -> {
        while (running) {
            tasks.forEach((channelName, task) -> {
                if (task != null) {
                    task.updateState();
                }
            });

            // continue running after a specific period of time
            try {
                sleep(INTERVAL_MS);
            } catch (InterruptedException e) {
                running = false;
            }
        }
    });

    MonitorService() {
        monitorThread.start();
    }

    public List<String> getMonitoringChannelNames() {
        return new ArrayList<>(tasks.keySet());
    }

    public void startTask(MonitorTask task) {
        var channelName = task.getChannelName();

        // check if is monitoring
        if (tasks.containsKey(channelName)) {
            System.out.println("Already monitoring '" + channelName + "'");
            return;
        }

        // when task starts it may fail
        task.setOnInitFailureListener(() -> {
            System.err.println("Failed to initialize the monitor for channel " + channelName);
            tasks.remove(channelName);
        });

        try {
            tasks.put(channelName, task);
            System.out.println("Start monitoring '" + channelName + "', please wait...");
            task.initCaches();
        } catch (Exception e) {
            System.err.println("Error finding channel '" + channelName + "'\n");
        }
    }

    public void stopTask(String channelName) {
        var task = tasks.get(channelName);
        var message = (String) null;

        if (task != null) {
            tasks.remove(channelName);
            message = "Stopped monitoring '" + channelName + "'.";
        } else {
            message = "Channel '" + channelName + "' is not being monitored.";
        }

        System.out.println(message);
    }

    public void stopAllTasks() {
        if (tasks.size() == 0) {
            System.out.println("Not monitoring any channels.");
            return;
        }

        var message = new StringBuilder("Trying to stop monitoring all channels:");
        tasks.forEach((channelName, task) -> message.append(" '").append(channelName).append("'"));
        tasks = new ConcurrentHashMap<>();
        System.out.println(message);
    }

    public void stopService() {
        stopAllTasks();
        running = false;
        monitorThread.interrupt();
    }
}