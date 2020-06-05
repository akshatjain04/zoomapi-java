package com.github.dbchar.zoomapi.components;

import com.github.dbchar.zoomapi.components.queries.ChatMessageQueryConfiguration;
import com.github.dbchar.zoomapi.models.Message;
import com.github.dbchar.zoomapi.sqlite.tables.ChannelMessageRecord;
import com.github.dbchar.zoomapi.utils.ListResult;
import com.github.dbchar.zoomapi.utils.Result;
import com.github.dbchar.zoomapi.utils.Validator;
import com.github.dbchar.zoomapi.utils.services.DatabaseService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.github.dbchar.zoomapi.utils.DateUtil.dateToString;
import static com.github.dbchar.zoomapi.utils.DateUtil.stringToDate;

/**
 * Created by Junxian Chen on 2020-04-24.
 */
public class ChatComponent {
  private final ChatMessagesComponent chatMessagesComponent;
  private final ChatChannelsComponent chatChannelsComponent;

  public ChatComponent(ChatMessagesComponent chatMessagesComponent, ChatChannelsComponent chatChannelsComponent) {
    this.chatMessagesComponent = chatMessagesComponent;
    this.chatChannelsComponent = chatChannelsComponent;
  }

  public Result<String> sendMessage(String channelName, String message) {
    try {
      // First request
      var stringResult = chatChannelsComponent.getChannelId(channelName, false);
      if (!stringResult.isSuccess()) {
        return new Result<>(null, "Failed to find channel " + channelName);
      }
      var channelId = stringResult.getItem();

      // Second request
      stringResult = chatMessagesComponent.send(message, null, channelId);
      if (!stringResult.isSuccess()) {
        return new Result<>(null, "Failed to send message:" + stringResult.getErrorMessage());
      }
      return new Result<>(stringResult.getItem(), null);
    } catch (Exception exception) {
      return new Result<>(null, exception.getMessage());
    }
  }

  public ListResult<Message> search(String channelName,
                                    String fromDate,
                                    String toDate,
                                    Predicate<Message> predicate,
                                    boolean useCache) {
    if (!useCache) return search(channelName, fromDate, toDate, predicate);

    var result = history(channelName, fromDate, toDate, true);
    return searchFilter(result, predicate);
  }


  public ListResult<Message> search(String channelName,
                                    String fromDate,
                                    String toDate,
                                    Predicate<Message> predicate) {
    var result = history(channelName, fromDate, toDate);
    return searchFilter(result, predicate);
  }

  private ListResult<Message> searchFilter(ListResult<Message> result,
                                           Predicate<Message> predicate) {
    if (result.isSuccess()) {
      var messages = result.getItems().stream()
              .filter(predicate)
              .collect(Collectors.toList());
      return new ListResult<>(messages, null);
    } else {
      return new ListResult<>(null, result.getErrorMessage());
    }
  }

  public ListResult<Message> history(String channelName,
                                     String fromDate,
                                     String toDate,
                                     boolean useCache) {
    if (!useCache) return history(channelName, fromDate, toDate);

    var stringResult = chatChannelsComponent.getChannelId(channelName, useCache);
    if (!stringResult.isSuccess()) {
      return new ListResult<>(null, stringResult.getErrorMessage());
    }
    var channelId = stringResult.getItem();

    try {
      var messageRecords = DatabaseService.INSTANCE.findChannelMessageRecordsByClientIdAndChannelId(chatMessagesComponent.getClientId(), channelId);
      var messages = messageRecords
              .stream()
              .map(ChannelMessageRecord::toMessage)
              .sorted(Comparator.comparingLong(Message::getTimestamp))
              .collect(Collectors.toUnmodifiableList());
      return new ListResult<>(messages, null);
    } catch (Exception e) {
      e.printStackTrace();
      return new ListResult<>(null, e.getMessage());
    }
  }

  /**
   * @param channelName channel name
   * @param fromDate    date string. Like "2020-01-01"
   * @param toDate      date string. Like "2020-01-01"
   * @return a message list result sorted by ascending order
   */
  public ListResult<Message> history(String channelName, String fromDate, String toDate) {
    // check inputs
    if (Validator.stringIsNullOrEmpty(fromDate, toDate)) {
      return new ListResult<>(null, "You must enter a from date and a to date");
    }

    try {
      var from = stringToDate(fromDate);
      var to = stringToDate(toDate);
      var interval = (int) TimeUnit.DAYS.convert(Math.abs(to.getTime() - from.getTime()), TimeUnit.MILLISECONDS);

      // time zone converter
      // because Zoom uses GMT time so we have to decide if we have to adjust the number of requests
      // for example, in PST = GMT - 7, during 17:00 - 24:00 we need to add an extra day to the request
      // PST: 2020-05-05
      // GMT: 2020-05-05 or 2020-05-06
      var moment = Instant.now();
      var local = moment.atZone(TimeZone.getDefault().toZoneId()).toLocalDate();
      var gmt = moment.atZone(TimeZone.getTimeZone("GMT").toZoneId()).toLocalDate();
      if (local.compareTo(gmt) < 0) {
        interval++;
      } else if (local.compareTo(gmt) > 0) {
        interval--;
      }

      var calendar = Calendar.getInstance();
      calendar.setTime(from);

      List<Message> messages = new ArrayList<>();

      var stringResult = chatChannelsComponent.getChannelId(channelName, false);
      if (!stringResult.isSuccess()) {
        return new ListResult<>(null, stringResult.getErrorMessage());
      }
      var channelId = stringResult.getItem();

      for (int i = 0; i <= interval; i++) {
        // send requests
        var date = dateToString(calendar.getTime());
        var result = historyByChannelId(channelId, date);

        if (!result.isSuccess()) {
          return new ListResult<>(null, result.getErrorMessage());
        }

        messages.addAll(result.getItems());
        // we may have extra messages that are before from date
        // this happens only in the first request (for from date)
        if (i == 0) {
          // filter out messages that are before from date
          messages = messages.stream()
                  .filter(message -> {
                    var localDate = Instant.parse(message.getDateTime())// GMT
                            .atZone(ZoneId.systemDefault()) // local
                            .toLocalDate();
                    var inputDate = LocalDate.parse(fromDate);
                    return localDate.isEqual(inputDate)
                            || localDate.isAfter(inputDate);
                  })
                  .collect(Collectors.toList());
        }
        calendar.add(Calendar.DATE, 1);
      }

      return new ListResult<>(messages, null);
    } catch (Exception e) {
      return new ListResult<>(null, e.getMessage());
    }
  }

  public ListResult<Message> historyByChannelId(String channelId,
                                                String fromDate,
                                                String toDate,
                                                boolean useCache) {
    if (!useCache) return historyByChannelId(channelId, fromDate, toDate);

    try {
      final var from = stringToDate(fromDate);
      final var to = stringToDate(toDate);
      var messageRecords = DatabaseService.INSTANCE.findChannelMessageRecordsByClientIdAndChannelIdAndBetweenDate(chatMessagesComponent.getClientId(), channelId, from, to);
      var messages = messageRecords
              .stream()
              .map(ChannelMessageRecord::toMessage)
              .collect(Collectors.toUnmodifiableList());
      return new ListResult<>(messages, null);
    } catch (Exception e) {
      e.printStackTrace();
      return new ListResult<>(null, e.getMessage());
    }
  }

  /**
   * @param channelId channel ID
   * @param fromDate  date string. Like "2020-01-01"
   * @param toDate    date string. Like "2020-01-01"
   * @return a message list result sorted by ascending order
   */
  public ListResult<Message> historyByChannelId(String channelId,
                                                String fromDate,
                                                String toDate) {
    // check inputs
    if (Validator.stringIsNullOrEmpty(fromDate, toDate)) {
      return new ListResult<>(null, "You must enter a from date and a to date");
    }

    try {
      var from = stringToDate(fromDate);
      var to = stringToDate(toDate);
      int interval = (int) TimeUnit.DAYS.convert(Math.abs(to.getTime() - from.getTime()), TimeUnit.MILLISECONDS);

      // time zone converter
      // because Zoom uses GMT time so we have to decide if we have to adjust the number of requests
      // for example, in PST = GMT - 7, during 17:00 - 24:00 we need to add an extra day to the request
      // PST: 2020-05-05
      // GMT: 2020-05-05 or 2020-05-06
      var moment = Instant.now();
      var local = moment.atZone(TimeZone.getDefault().toZoneId()).toLocalDate();
      var gmt = moment.atZone(TimeZone.getTimeZone("GMT").toZoneId()).toLocalDate();
      if (local.compareTo(gmt) < 0) {
        interval++;
      } else if (local.compareTo(gmt) > 0) {
        interval--;
      }

      var calendar = Calendar.getInstance();
      calendar.setTime(from);

      var messages = new ArrayList<Message>();
      for (int i = 0; i <= interval; i++) {
        // send requests
        var date = dateToString(calendar.getTime());
        var result = historyByChannelId(channelId, date);

        if (!result.isSuccess()) {
          return new ListResult<>(null, result.getErrorMessage());
        }

        messages.addAll(result.getItems());
        calendar.add(Calendar.DATE, 1);
      }

      return new ListResult<>(messages, null);
    } catch (Exception e) {
      return new ListResult<>(null, e.getMessage());
    }
  }

  private ListResult<Message> historyByChannelId(String channelId, String date) {
    // check inputs
    if (Validator.stringIsNullOrEmpty(channelId)) {
      return new ListResult<>(null, "You must enter a channel name or user id");
    }

    try {
      var result = chatMessagesComponent.list("me", new ChatMessageQueryConfiguration(
              ChatMessageQueryConfiguration.Type.CHANNEL, channelId, date, ChatMessageQueryConfiguration.PAGE_SIZE_DEFAULT));

      if (result.isSuccess()) {
        var messages = result.getItems().stream()
                .sorted(Comparator.comparingLong(Message::getTimestamp))
                .collect(Collectors.toList());

        return new ListResult<>(messages, null);
      } else {
        return new ListResult<>(null, result.getErrorMessage());
      }
    } catch (Exception exception) {
      return new ListResult<>(null, exception.getMessage());
    }
  }
}
