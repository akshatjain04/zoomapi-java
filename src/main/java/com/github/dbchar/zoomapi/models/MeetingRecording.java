package com.github.dbchar.zoomapi.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MeetingRecording {
    private String uuid;
    private int id;
    @SerializedName("account_id")
    private String accountId;
    @SerializedName("host_id")
    private String hostId;
    private String topic;
    private int type;
    @SerializedName("start_time")
    private String startTime;
    private String timezone;
    private int duration;
    @SerializedName("total_size")
    private int totalSize;
    @SerializedName("recording_count")
    private int recording_count;
    @SerializedName("share_url")
    private String shareUrl;
    @SerializedName("recording_files")
    private List<RecordingFile> recordingFiles;

    public String getUuid() {
        return uuid;
    }

    public int getId() {
        return id;
    }

    public String getAccountId() {
        return accountId;
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

    public String getTimezone() {
        return timezone;
    }

    public int getDuration() {
        return duration;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public int getRecording_count() {
        return recording_count;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public List<RecordingFile> getRecordingFiles() {
        return recordingFiles;
    }
}
