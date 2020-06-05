package com.github.dbchar.zoomapi.models.responses;

import com.github.dbchar.zoomapi.models.Message;

import java.util.List;

/**
 * Created by Junxian Chen on 2020-04-19.
 */
public class MessageListResponse extends PaginationResponse {
    private String date;
    private List<Message> messages;

    public String getDate() {
        return date;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
