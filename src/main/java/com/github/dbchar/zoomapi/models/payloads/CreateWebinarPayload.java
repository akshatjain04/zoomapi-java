package com.github.dbchar.zoomapi.models.payloads;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Junxian Chen on 2020-04-20.
 */
public class CreateWebinarPayload {
    private String topic;
    private int type;
    @SerializedName("start_time")
    private String startTime;
    private String duration;
    private String timezone;
    private String password;
    private String agenda;

    class Recurrence {
        int type;
        @SerializedName("start_time")
        int repeatInterval;
        @SerializedName("start_time")
        String endDateTime;
    }

    class Settings {
        @SerializedName("host_video")
        String hostVideo;
        @SerializedName("panelists_video")
        String panelistsVideo;
        @SerializedName("practice_session")
        String practiceSession;
        @SerializedName("hd_video")
        String hdVideo;
        @SerializedName("approval_type")
        int approvalType;
        @SerializedName("registration_type")
        int registrationType;
        String audio;
        @SerializedName("auto_recording")
        String autoRecording;
        @SerializedName("enforce_login")
        String enforceLogin;
        @SerializedName("close_registration")
        String closeRegistration;
        @SerializedName("show_share_button")
        String showShareButton;
        @SerializedName("allow_multiple_devices")
        String allowMultipleDevices;
        @SerializedName("registrants_email_notification")
        String registrantsEmailNotification;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }
}
