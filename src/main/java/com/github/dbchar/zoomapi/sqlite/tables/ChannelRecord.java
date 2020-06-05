package com.github.dbchar.zoomapi.sqlite.tables;

import com.github.dbchar.zoomapi.models.Channel;
import com.github.dbchar.zoomapi.sqlite.annotations.Column;
import com.github.dbchar.zoomapi.sqlite.annotations.Entity;
import com.github.dbchar.zoomapi.sqlite.annotations.Id;
import com.github.dbchar.zoomapi.sqlite.annotations.Table;

/**
 * Created by Wen-Chia, Yang on 2020-05-27.
 */
@Entity
@Table(name = "channel")
public class ChannelRecord {
    @Id
    private int id;
    @Column(name = "client_id")
    private String clientId;
    @Column(name = "channel_id")
    private String channelId;
    @Column(name = "name")
    private String name;
    @Column(name = "type")
    private int type;

    public ChannelRecord() {
    }

    public ChannelRecord(String clientId, String channelId, String name, int type) {
        this.clientId = clientId;
        this.channelId = channelId;
        this.name = name;
        this.type = type;
    }

    public ChannelRecord(String clientId, String channelId) {
        this.clientId = clientId;
        this.channelId = channelId;
    }

    public ChannelRecord(String clientId, Channel channel) {
        this(clientId, channel.getId(), channel.getName(), channel.getType());
    }

    public Channel toChannel() {
        return new Channel(channelId, name, type);
    }

    public int getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ChannelRecord{" +
                "id=" + id +
                ", channelId='" + channelId + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
