package com.github.dbchar.zoomapi.models.responses;

import com.google.gson.annotations.SerializedName;

// https://marketplace.zoom.us/docs/api-reference/zoom-api/chat-channels/invitechannelmembers
// https://marketplace.zoom.us/docs/api-reference/zoom-api/chat-channels/joinchannel
public class IdAddedDateResponse {
    private String ids; // document is wrong
    private String id;
    @SerializedName("added_at")
    private String addedDate;

    public String getIds() {
        return ids;
    }

    public String getId() {
        return id;
    }

    public String getAddedDate() {
        return addedDate;
    }
}
