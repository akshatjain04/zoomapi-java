package com.github.dbchar.zoomapi.sqlite.tables;

import com.github.dbchar.zoomapi.models.Message;
import com.github.dbchar.zoomapi.sqlite.annotations.Column;
import com.github.dbchar.zoomapi.sqlite.annotations.Entity;
import com.github.dbchar.zoomapi.sqlite.annotations.Id;
import com.github.dbchar.zoomapi.sqlite.annotations.Table;

/**
 * Created by Wen-Chia, Yang on 2020-05-27.
 */
@Entity
@Table(name = "message")
public class ChannelMessageRecord {
    @Id
    private int id;
    @Column(name = "client_id")
    private String clientId;
    @Column(name = "message_id")
    private String messageId;
    @Column(name = "channel_id")
    private String channelId;
    @Column(name = "message")
    private String message;
    @Column(name = "sender")
    private String sender;
    @Column(name = "date_time")
    private String dateTime;
    @Column(name = "timestamp")
    private long timestamp; // int64

    public ChannelMessageRecord() {
    }

    public ChannelMessageRecord(String clientId,
                                String channelId,
                                String messageId,
                                String message,
                                String sender,
                                String dateTime,
                                long timestamp) {
        this.clientId = clientId;
        this.channelId = channelId;
        this.messageId = messageId;
        this.message = message == null ? "" : message;
        this.sender = sender == null ? "" : sender;
        this.dateTime = dateTime == null ? "" : dateTime;
        this.timestamp = timestamp;
    }

    public ChannelMessageRecord(String clientId, String channelId, Message message) {
        this(clientId,
                channelId,
                message.getId(),
                message.getMessage(),
                message.getSender(),
                message.getDateTime(),
                message.getTimestamp());
    }

    public Message toMessage() {
        return new Message(messageId, message, sender, dateTime, timestamp);
    }

    public int getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getDateTime() {
        return dateTime;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "MessageRecord{" +
                "id=" + id +
                ", messageId='" + messageId + '\'' +
                ", channelId='" + channelId + '\'' +
                ", sender='" + sender + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
