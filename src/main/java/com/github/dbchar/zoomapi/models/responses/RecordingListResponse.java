package com.github.dbchar.zoomapi.models.responses;

import com.github.dbchar.zoomapi.models.MeetingRecording;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Junxian Chen on 2020-04-19.
 */
public class RecordingListResponse extends PaginationResponse {
    String from;
    String to;
    @SerializedName("meetings")
    List<MeetingRecording> recordings;

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public List<MeetingRecording> getRecordings() {
        return recordings;
    }
}
