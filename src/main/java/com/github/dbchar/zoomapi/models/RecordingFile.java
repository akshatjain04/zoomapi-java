package com.github.dbchar.zoomapi.models;

import com.google.gson.annotations.SerializedName;

public class RecordingFile {
    private int id;
    @SerializedName("meeting_id")
    private String meeting_id;
    @SerializedName("recording_start")
    private String recordingStart;
    @SerializedName("recording_end")
    private String recordingEnd;
    @SerializedName("file_type")
    private String fileType;
    @SerializedName("file_size")
    private int fileSize;
    @SerializedName("play_url")
    private String playUrl;
    @SerializedName("download_url")
    private String downloadUrl;
    private String status;
    @SerializedName("recording_type")
    private String recordingType;

    public int getId() {
        return id;
    }

    public String getMeeting_id() {
        return meeting_id;
    }

    public String getRecordingStart() {
        return recordingStart;
    }

    public String getRecordingEnd() {
        return recordingEnd;
    }

    public String getFileType() {
        return fileType;
    }

    public int getFileSize() {
        return fileSize;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getStatus() {
        return status;
    }

    public String getRecordingType() {
        return recordingType;
    }
}
