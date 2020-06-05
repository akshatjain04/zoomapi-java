package com.github.dbchar.zoomapi.models.responses;

import com.github.dbchar.zoomapi.models.Channel;

import java.util.List;

public class ChannelListResponse extends PaginationResponse {
    private List<Channel> channels;

    public List<Channel> getChannels() {
        return channels;
    }
}
