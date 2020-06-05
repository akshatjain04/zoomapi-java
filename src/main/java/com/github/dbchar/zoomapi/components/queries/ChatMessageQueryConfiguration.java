package com.github.dbchar.zoomapi.components.queries;

import java.util.HashMap;
import java.util.Map;

public class ChatMessageQueryConfiguration {
    public enum Type {
        CONTACT, CHANNEL
    }

    public static final int PAGE_SIZE_MAXIMUM = 50;
    public static final int PAGE_SIZE_DEFAULT = PAGE_SIZE_MAXIMUM;
    private String contactId;
    private String channelId;
    private final String date;
    private final int pageSize;
    private final String nextPageToken;

    public ChatMessageQueryConfiguration(Type type, String id, String date, int pageSize, String nextPageToken) {
        if (type.equals(Type.CHANNEL)) {
            this.channelId = id;
        } else {
            this.contactId = id;
        }

        this.date = date;
        this.pageSize = Math.min(Math.max(pageSize, 1), PAGE_SIZE_MAXIMUM);
        this.nextPageToken = nextPageToken;
    }

    public ChatMessageQueryConfiguration(Type type, String id) {
        this(type, id, null, PAGE_SIZE_DEFAULT, null);
    }

    public ChatMessageQueryConfiguration(Type type, String id, int pageSize) {
        this(type, id, null, pageSize, null);
    }

    public ChatMessageQueryConfiguration(Type type, String id, String date) {
        this(type, id, date, PAGE_SIZE_DEFAULT, null);
    }

    public ChatMessageQueryConfiguration(Type type, String id, String date, int pageSize) {
        this(type, id, date, pageSize, null);
    }

    public Map<String, Object> asMap() {
        Map<String, Object> map = new HashMap<>();
        if (channelId != null) {
            map.put("to_channel", channelId);
        }
        if (contactId != null) {
            map.put("to_contact", contactId);
        }
        if (date != null) {
            map.put("date", date);
        }
        if (nextPageToken != null) {
            map.put("next_page_token", nextPageToken);
        }
        map.put("page_size", pageSize);
        return map;
    }

    public int getPageSize() {
        return pageSize;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getDate() {
        return date;
    }

    public boolean isInvalidId() {
        return (contactId != null && channelId != null)
                || (contactId == null && channelId == null);
    }
}