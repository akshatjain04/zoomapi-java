package com.github.dbchar.zoomapi.models.responses;

import com.google.gson.annotations.SerializedName;

public class PaginationResponse {
    @SerializedName("total_records")
    private int totalRecords;
    @SerializedName("page_size")
    private int pageSize;
    @SerializedName("next_page_token")
    private String nextPageToken;
    @SerializedName("page_count")
    private int pageCount;
    @SerializedName("page_number")
    private int pageNumber;

    public int getTotalRecords() {
        return totalRecords;
    }

    public int getPageSize() {
        return pageSize;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public int getPageCount() {
        return pageCount;
    }

    public int getPageNumber() {
        return pageNumber;
    }
}
