package com.github.dbchar.zoomapi.components.queries;

import com.google.gson.annotations.SerializedName;

public class QueriesConfiguration {
    @SerializedName("page_size")
    private int pageSize;
    @SerializedName("next_page_token")
    private String nextPageToken;
    private String mc;
    private boolean trash;
    private String from;
    private String to;
    @SerializedName("trash_type")
    private String trashType;

    public int getPageSize() {
        return pageSize;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public String getMc() {
        return mc;
    }

    public boolean isTrash() {
        return trash;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getTrashType() {
        return trashType;
    }
}
