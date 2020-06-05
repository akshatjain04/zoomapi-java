package com.github.dbchar.zoomapi.models;

import com.github.dbchar.zoomapi.utils.DateUtil;
import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Junxian Chen on 2020-04-19.
 */
public class Message {
    private final String id;
    private final String message;
    private final String sender;
    @SerializedName("date_time")
    private final String dateTime;
    private final long timestamp; // int64

    public Message(String id, String message, String sender, String dateTime, long timestamp) {
        this.id = id;
        this.message = message;
        this.sender = sender;
        this.dateTime = dateTime;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
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

    public String getLocalDateTime() {
        return DateUtil.getLocalDateTime(dateTime);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Date getDate() {
        try {
            return DateUtil.stringToDate(dateTime);
        } catch (ParseException e) {
            return new Date();
        }
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", sender='" + sender + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Message message1 = (Message) object;
        return id.equals(message1.id) &&
                message.equals(message1.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message);
    }
}
