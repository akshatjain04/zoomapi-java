package com.github.dbchar.zoomapi.utils.services;

import com.github.dbchar.zoomapi.sqlite.manager.SQLiteManager;
import com.github.dbchar.zoomapi.sqlite.tables.ChannelMemberRecord;
import com.github.dbchar.zoomapi.sqlite.tables.ChannelMessageRecord;
import com.github.dbchar.zoomapi.sqlite.tables.ChannelRecord;
import com.github.dbchar.zoomapi.sqlite.tables.CredentialRecord;
import com.github.dbchar.zoomapi.utils.DateUtil;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Wen-Chia, Yang on 2020-05-31.
 */
public enum DatabaseService {
    INSTANCE;

    private final SQLiteManager db = SQLiteManager.init();

    DatabaseService() {
    }

    // region Generic Operations

    public void save(Object entity) throws Exception {
        db.save(entity);
    }

    public void delete(Object entity) throws Exception {
        db.delete(entity);
    }

    // endregion

    // region Filter Channel Table

    public List<ChannelRecord> findChannelRecordsByClientId(String clientId) throws Exception {
        return db.findAll(ChannelRecord.class)
                .stream()
                .filter(record -> record.getClientId().equals(clientId))
                .collect(Collectors.toUnmodifiableList());
    }

    public ChannelRecord findChannelRecordByClientIdAndChannelId(String clientId, String channelId) throws Exception {
        return db.findAll(ChannelRecord.class)
                .stream()
                .filter(record -> record.getClientId().equals(clientId))
                .filter(record -> record.getChannelId().equals(channelId))
                .findFirst()
                .orElseThrow(() -> new Exception("Cannot find channel with clientId: " + clientId + " and channelId: " + channelId));
    }

    // endregion

    // region Filter Channel Message Table

    public List<ChannelMessageRecord> findChannelMessageRecordsByClientIdAndChannelId(String clientId, String channelId) throws Exception {
        return db.findAll(ChannelMessageRecord.class)
                .stream()
                .filter(record -> record.getClientId().equals(clientId))
                .filter(record -> record.getChannelId().equals(channelId))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<ChannelMessageRecord> findChannelMessageRecordsByClientIdAndChannelIdAndDate(String clientId, String channelId, String date) throws Exception {
        return db.findAll(ChannelMessageRecord.class)
                .stream()
                .filter(record -> record.getClientId().equals(clientId))
                .filter(record -> record.getChannelId().equals(channelId))
                .filter(record -> record.getDateTime().contains(date))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<ChannelMessageRecord> findChannelMessageRecordsByClientIdAndChannelIdAndBetweenDate(String clientId, String channelId, Date fromDate, Date toDate) throws Exception {
        return db.findAll(ChannelMessageRecord.class)
                .stream()
                .filter(record -> record.getClientId().equals(clientId))
                .filter(record -> record.getChannelId().equals(channelId))
                .filter(messageRecord -> {
                    try {
                        var date = DateUtil.stringToDate(messageRecord.getDateTime());
                        return (date.equals(fromDate) || date.after(fromDate)) && (date.before(toDate) || date.equals(toDate));
                    } catch (ParseException e) {
                        return true;
                    }
                })
                .collect(Collectors.toUnmodifiableList());
    }

    public ChannelMessageRecord findChannelMessageRecordsByClientIdAndMessageId(String clientId, String messageId) throws Exception {
        return db.findAll(ChannelMessageRecord.class)
                .stream()
                .filter(record -> record.getClientId().equals(clientId))
                .filter(record -> record.getMessageId().equals(messageId))
                .findFirst()
                .orElseThrow(() -> new Exception("Cannot find channel with clientId: " + clientId + " and messageId: " + messageId));
    }

    // endregion

    // region Filter Channel Member Table

    public List<ChannelMemberRecord> findChannelMemberRecordsByClientIdAndChannelId(String clientId, String channelId) throws Exception {
        return db.findAll(ChannelMemberRecord.class)
                .stream()
                .filter(record -> record.getClientId().equals(clientId))
                .filter(record -> record.getChannelId().equals(channelId))
                .collect(Collectors.toUnmodifiableList());
    }

    public ChannelMemberRecord findChannelMemberRecordsByClientIdAndChannelIdAndUserId(String clientId, String channelId, String userId) throws Exception {
        return db.findAll(ChannelMemberRecord.class)
                .stream()
                .filter(record -> record.getClientId().equals(clientId))
                .filter(record -> record.getChannelId().equals(channelId))
                .filter(record -> record.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new Exception("Cannot find channel member with clientId: " + clientId + ", channelId: " + channelId + ", and userId: " + userId));
    }

    // endregion

    // region Filter Credential Table

    public List<CredentialRecord> findCredentialRecords(String clientId, String clientSecret) throws Exception {
        return db.findAll(CredentialRecord.class)
                .stream()
                .filter(record -> record.getClientId().equals(clientId))
                .filter(record -> record.getClientSecret().equals(clientSecret))
                .collect(Collectors.toUnmodifiableList());
    }

    // endregion
}
