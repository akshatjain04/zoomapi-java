package com.github.dbchar.zoomapi.models.responses;

import com.github.dbchar.zoomapi.models.Account;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AccountReportResponse extends PaginationResponse {
    private String from;
    private String to;
    @SerializedName("page_count")
    private int pageCount;
    @SerializedName("page_number")
    private int pageNumber;
    @SerializedName("total_meetings")
    private int totalMeetings;
    @SerializedName("total_participants")
    private int totalParticipants;
    @SerializedName("total_meeting_minutes")
    private int totalMeetingMinutes;
    private List<Account> users;

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getPageCount() {
        return pageCount;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getTotalMeetings() {
        return totalMeetings;
    }

    public int getTotalParticipants() {
        return totalParticipants;
    }

    public int getTotalMeetingMinutes() {
        return totalMeetingMinutes;
    }

    public List<Account> getUsers() {
        return users;
    }
}
