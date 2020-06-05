package com.github.dbchar.zoomapi.models;

import com.google.gson.annotations.SerializedName;

public class Account {
    private String id;
    private String email;
    @SerializedName("user_name")
    private String userName;
    private int type;
    private String dept;
    private int meetings;
    private int participants;
    @SerializedName("meeting_minutes")
    private int meetingMinutes;
    @SerializedName("last_client_version")
    private String lastClientVersion;
    @SerializedName("last_login_time")
    private String lastLoginTime;
    @SerializedName("create_time")
    private String createTime;

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public int getType() {
        return type;
    }

    public String getDept() {
        return dept;
    }

    public int getMeetings() {
        return meetings;
    }

    public int getParticipants() {
        return participants;
    }

    public int getMeetingMinutes() {
        return meetingMinutes;
    }

    public String getLastClientVersion() {
        return lastClientVersion;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public String getCreateTime() {
        return createTime;
    }
}
