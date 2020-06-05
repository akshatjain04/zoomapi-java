package com.github.dbchar.zoomapi.models.payloads;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CreateMeetingPayload {
    private String topic;
    private int type;
    @SerializedName("start_time")
    private String startTime;
    private int duration;
    private String timezone;
    private String password;
    private String agenda;
    private Recurrence reference;
    private Settings settings;

    class Recurrence {
        private int type;
        @SerializedName("repeat_interval")
        private int repeatInterval;
        @SerializedName("weekly_days")
        private String weeklyDays;
        @SerializedName("monthly_day")
        private int monthlyDay;
        @SerializedName("monthly_week")
        private int monthlyWeek;
        @SerializedName("monthly_week_day")
        private int monthlyWeekDay;
        @SerializedName("end_times")
        private int endTimes;
        @SerializedName("end_date_time")
        private String endDateTime;
    }

    class Settings {
        @SerializedName("host_video")
        private boolean hostVideo;
        @SerializedName("participant_video")
        private boolean participantVideo;
        @SerializedName("cn_meeting")
        private boolean cnMeeting;
        @SerializedName("in_meeting")
        private boolean inMeeting;
        @SerializedName("join_before_host")
        private boolean joinBeforeHost;
        @SerializedName("mute_upon_entry")
        private boolean muteUponEntry;
        private boolean watermark;
        @SerializedName("use_pmi")
        private boolean usePmi;
        @SerializedName("approval_type")
        private int approvalType;
        @SerializedName("registration_type")
        private int registrationType;
        private String audio;
        @SerializedName("auto_recording")
        private String autoRecording;
        @SerializedName("enforce_login")
        private boolean enforceLogin;
        @SerializedName("enforce_login_domains")
        private String enforceLoginDomains;
        @SerializedName("alternative_hosts")
        private String alternativeHosts;
        @SerializedName("global_dial_in_countries")
        private List<String> globalDialInCountries;
        @SerializedName("registrants_email_notification")
        private boolean registrantsEmailNotification;
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

    public int getDuration() {
        return duration;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getPassword() {
        return password;
    }

    public String getAgenda() {
        return agenda;
    }

    public Recurrence getReference() {
        return reference;
    }

    public Settings getSettings() {
        return settings;
    }
}
