package com.github.dbchar.zoomapi.models;

import com.google.gson.annotations.SerializedName;

public class Meeting {
    private String uuid;
    private String id;
    @SerializedName("host_id")
    private String hostId;
    private String topic;
    private int type;
    @SerializedName("start_time")
    private String startTime;
    private String duration;
    @SerializedName("created_at")
    private String createdDate;
    @SerializedName("join_url")
    private String joinUrl;

    public String getUuid() {
        return uuid;
    }

    public String getId() {
        return id;
    }

    public String getHostId() {
        return hostId;
    }

    public String getTopic() {
        return topic;
    }

    public int getType() {
        return type;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getDuration() {
        return duration;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getJoinUrl() {
        return joinUrl;
    }
}
