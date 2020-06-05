package com.github.dbchar.zoomapi.utils.services;

import com.github.dbchar.zoomapi.clients.OAuthZoomClient;
import com.github.dbchar.zoomapi.models.Message;
import com.github.dbchar.zoomapi.models.User;
import com.github.dbchar.zoomapi.sqlite.manager.SQLiteManager;
import com.github.dbchar.zoomapi.utils.Logger;
import com.github.dbchar.zoomapi.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;

import static com.github.dbchar.zoomapi.utils.DateUtil.DATE_FORMAT;

public class MonitorTask {

    private List<Message> messageCache;
    private List<User> memberCache;
    private final String channelName;
    private String channelId;
    private final String fromDate;
    private final String toDate;
    private final OAuthZoomClient client;
    private boolean initialized = false;
    private final SQLiteManager db = SQLiteManager.init();

    // We make them final here to ensure they are mutable list
    // So it is possible to remove a single listener
    private final List<BiConsumer<String, Message>> onMessageReceivedListeners = new ArrayList<>();
    private final List<BiConsumer<String, Message>> onMessageUpdatedListeners = new ArrayList<>();
    private final List<BiConsumer<String, User>> onMemberAddedListeners = new ArrayList<>();

    private Runnable onInitFailureListener = () -> {
    };

    public MonitorTask(String channelName,
                       String fromDate,
                       String toDate,
                       OAuthZoomClient client) {
        this.channelName = channelName;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.client = client;
    }

    public void initCaches() {
        try {
            this.channelId = fetchChannelId(channelName);
            this.messageCache = downloadHistory(fromDate, toDate);
            this.memberCache = fetchChannelMembers(channelId);
            this.initialized = true;
        } catch (Exception e) {
            new Thread(() -> onInitFailureListener.run()).start();
        }
    }

    public void updateState() {
        if (initialized) {
            monitorMessages();
            monitorMembers();
        }
    }

    /**
     * Set multiple message received events
     *
     * @param listeners
     */
    public void setOnMessageReceivedListeners(Collection<BiConsumer<String, Message>> listeners) {
        if (listeners == null) return;
        this.onMessageReceivedListeners.clear();
        this.onMessageReceivedListeners.addAll(listeners);
    }

    /**
     * Set single message received event
     *
     * @param listener
     */
    public void addOnMessageReceivedListener(BiConsumer<String, Message> listener) {
        if (listener == null) return;
        this.onMessageReceivedListeners.add(listener);
    }

    /**
     * Set multiple message update events
     *
     * @param listeners
     */
    public void setOnMessageUpdatedListeners(Collection<BiConsumer<String, Message>> listeners) {
        if (listeners == null) return;
        this.onMessageUpdatedListeners.clear();
        this.onMessageUpdatedListeners.addAll(listeners);
    }

    /**
     * Set single message update event
     *
     * @param listener
     */
    public void addOnMessageUpdatedListener(BiConsumer<String, Message> listener) {
        if (listener == null) return;
        this.onMessageUpdatedListeners.add(listener);
    }

    /**
     * Set multiple member added events
     *
     * @param listeners
     */
    public void setOnMemberAddedListeners(Collection<BiConsumer<String, User>> listeners) {
        if (listeners == null) return;
        this.onMemberAddedListeners.clear();
        this.onMemberAddedListeners.addAll(listeners);
    }

    /**
     * Set single member added event
     *
     * @param listener
     */
    public void addOnMemberAddedListener(BiConsumer<String, User> listener) {
        if (listener == null) return;
        this.onMemberAddedListeners.add(listener);
    }

    /**
     * Set init cache failure event
     *
     * @param listener
     */
    public void setOnInitFailureListener(Runnable listener) {
        if (listener == null) return;
        this.onInitFailureListener = listener;
    }

    public String getChannelName() {
        return channelName;
    }

    private String fetchChannelId(String channelName) throws Exception {
        var result = client.getChatChannelsComponent().getChannelId(channelName, false);
        if (!result.isSuccessOrRefreshToken(client)) {
            throw new Exception("Cannot find channel " + channelName);
        }
        return result.getItem();
    }

    private List<Message> downloadHistory(String fromDate, String toDate) throws Exception {
        var result = client.getChatComponent().historyByChannelId(channelId, fromDate, toDate);
        if (!result.isSuccessOrRefreshToken(client)) {
            throw new Exception(result.getErrorMessage());
        }
        return result.getItems();
    }

    private List<User> fetchChannelMembers(String channelId) throws Exception {
        var result = client.getChatChannelsComponent().listMembers(channelId, null);
        if (!result.isSuccessOrRefreshToken(client)) {
            throw new Exception("Failed to refresh member cache.\nReason: " + result.getErrorMessage());
        }
        return result.getItems();
    }

    private void monitorMessages() {
        try {
            var today = new SimpleDateFormat(DATE_FORMAT).format(new Date());
            var newMessages = downloadHistory(fromDate, today);

            for (var newMessage : newMessages) {
                var isNew = true;
                // check if updated
                for (var oldMessage : messageCache) {
                    if (newMessage.getId().equals(oldMessage.getId())) {
                        isNew = false;
                        if (!newMessage.getMessage().equals(oldMessage.getMessage())) {
                            // start a new thread so main thread will not be blocked
                            onMessageUpdatedListeners.forEach(listener ->
                                    new Thread(() -> listener.accept(channelName, newMessage)).start());
                        }
                        break;
                    }
                }
                // check if new
                if (isNew) {
                    onMessageReceivedListeners.forEach(listener ->
                            new Thread(() -> listener.accept(channelName, newMessage)).start());
                }
            }

            messageCache = newMessages;

        } catch (Exception e) {
            Logger.loge("Failed to refresh message cache.\nReason: " + e.getMessage());
        }
    }

    private void monitorMembers() {
        try {
            var newMemberCache = fetchChannelMembers(channelId);

            // member added
            Utils.diff(newMemberCache, memberCache).forEach(member -> {
                // emit new member added event
                onMemberAddedListeners.forEach(listener ->
                        new Thread(() -> listener.accept(channelName, member)).start());
            });

            memberCache = newMemberCache;
        } catch (Exception e) {
            Logger.loge("Failed to refresh member cache.\nReason: " + e.getMessage());
        }
    }
}