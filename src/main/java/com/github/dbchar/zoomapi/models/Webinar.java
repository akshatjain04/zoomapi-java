package com.github.dbchar.zoomapi.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Junxian Chen on 2020-04-18.
 */
public class Webinar {
    private String uuid;
    private int id;
    @SerializedName("host_id")
    private String hostId;
    private String topic;
    private int type; // 5, 6, 9
    private int duration;
    private String timezone;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("join_url")
    private String joinUrl;
    private String agenda;
    @SerializedName("start_time")
    private String startTime;

    public String getUuid() {
        return uuid;
    }

    public int getId() {
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

    public int getDuration() {
        return duration;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getJoinUrl() {
        return joinUrl;
    }

    public String getAgenda() {
        return agenda;
    }

    public String getStartTime() {
        return startTime;
    }
}
