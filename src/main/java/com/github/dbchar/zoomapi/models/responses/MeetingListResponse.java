package com.github.dbchar.zoomapi.models.responses;

import com.github.dbchar.zoomapi.models.Meeting;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MeetingListResponse {
    @SerializedName("page_count")
    private String pageCount;
    @SerializedName("page_number")
    private String pageNumber;
    @SerializedName("page_size")
    private String pageSize;
    @SerializedName("total_records")
    private String totalRecords;
    private List<Meeting> meetings;

    public String getPageCount() {
        return pageCount;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public String getPageSize() {
        return pageSize;
    }

    public String getTotalRecords() {
        return totalRecords;
    }

    public List<Meeting> getMeetings() {
        return meetings;
    }
}
