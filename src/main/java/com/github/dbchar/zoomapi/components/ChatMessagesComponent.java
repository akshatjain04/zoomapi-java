package com.github.dbchar.zoomapi.components;

import com.github.dbchar.zoomapi.components.queries.ChatMessageQueryConfiguration;
import com.github.dbchar.zoomapi.components.queries.PageConfiguration;
import com.github.dbchar.zoomapi.models.Message;
import com.github.dbchar.zoomapi.models.responses.IdAddedDateResponse;
import com.github.dbchar.zoomapi.models.responses.MessageListResponse;
import com.github.dbchar.zoomapi.network.ApiRequest;
import com.github.dbchar.zoomapi.network.ApiResponse;
import com.github.dbchar.zoomapi.network.HttpMethod;
import com.github.dbchar.zoomapi.sqlite.tables.ChannelMessageRecord;
import com.github.dbchar.zoomapi.utils.ListResult;
import com.github.dbchar.zoomapi.utils.Result;
import com.github.dbchar.zoomapi.utils.Validator;
import com.github.dbchar.zoomapi.utils.services.DatabaseService;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.github.dbchar.zoomapi.utils.DateUtil.DATE_FORMAT;

/**
 * Created by Junxian Chen on 2020-04-14.
 *
 * @see <a href="https://marketplace.zoom.us/docs/api-reference/zoom-api/chat-messages/">zoom-api/chat-messages</a>
 */
public class ChatMessagesComponent extends BaseComponent {

  private final String PATH = "chat";

  /**
   * @param userId "me"
   * @param config String contactId;
   *               String channelId;
   *               String date;
   *               int pageSize;
   *               String nextPageToken;
   * @return ListResult<Message>
   */
  public ListResult<Message> list(String userId,
                                  ChatMessageQueryConfiguration config) {
    // check inputs
    if (Validator.stringIsNullOrEmpty(userId) || config == null) {
      return new ListResult<>(null, "You must enter a userId");
    }

    if (config.isInvalidId()) {
      return new ListResult<>(null, "You must enter only a contact id or a channel id");
    }

    // configure end point
    var path = PATH + "/users/" + userId + "/messages";
    var request = new ApiRequest(BASE_URL, path, HttpMethod.GET);

    // configure queries
    request.setQueries(config.asMap());

    // configure result
    try {
      var messages = batchRequestMessages(request, config.getPageSize());
      var result = new ListResult<>(messages, null);

      // refresh cache when success
      cacheAfterList(result, config.getChannelId(), config.getDate());

      return result;
    } catch (Exception exception) {
      return new ListResult<>(null, exception.getMessage());
    }
  }

  /**
   * Single date query for messages
   *
   * @param userId    "me"
   * @param toContact contact ID
   * @param toChannel channel ID
   * @param date      nullable date string. YYYY-MM-DD, e.g. "2020-01-01"
   * @param config    page size and next token
   * @return ListResult<Message>
   */
  public ListResult<Message> list(String userId,
                                  String toContact,
                                  String toChannel,
                                  String date,
                                  PageConfiguration config) {
    // check inputs
    if (Validator.stringIsNullOrEmpty(userId)) {
      return new ListResult<>(null, "You must enter a userId");
    }
    if ((toContact != null && toChannel != null)
            || (toContact == null && toChannel == null)) {
      return new ListResult<>(null, "You must enter only a contact id or a channel id");
    }

    // configure end point
    var path = PATH + "/users/" + userId + "/messages";
    var configuration = new ApiRequest(BASE_URL, path, HttpMethod.GET);

    // configure queries
    if (toContact != null) {
      configuration.addQuery("to_contact", toContact);
    }
    if (toChannel != null) {
      configuration.addQuery("to_channel", toChannel);
    }
    if (date != null) {
      configuration.addQuery("date", date);
    }
    if (config != null) {
      configuration.addQuery("page_size", config.getPageSize());
      configuration.addQuery("next_page_token", config.getNextPageToken());
    } else {
      configuration.addQuery("page_size", 50);
    }

    // configure result
    var response = request(configuration);
    var messages = response.isSuccess()
            ? new Gson().fromJson(response.getJson(), MessageListResponse.class).getMessages()
            : null;
    var result = new ListResult<>(messages, retrieveErrorMessage(response));

    // refresh cache when success
    cacheAfterList(result, toChannel, date);

    return result;
  }

  private void cacheAfterList(ListResult<Message> result, String channelId, String date) {
    if (result.isSuccess()) {
      try {
        var queryDate = date == null ? new SimpleDateFormat(DATE_FORMAT).format(new Date()) : date;
        var messageRecords = DatabaseService.INSTANCE.findChannelMessageRecordsByClientIdAndChannelIdAndDate(getClientId(), channelId, queryDate);
        messageRecords.forEach(messageRecord -> {
          try {
            DatabaseService.INSTANCE.delete(messageRecord);
          } catch (Exception ignored) {
          }
        });

        result.getItems().forEach(message -> {
          try {
            // find one, update it
            var messageRecord = DatabaseService.INSTANCE.findChannelMessageRecordsByClientIdAndMessageId(getClientId(), message.getId());
            messageRecord.setDateTime(message.getDateTime());
            messageRecord.setSender(message.getSender());
            messageRecord.setTimestamp(message.getTimestamp());
            DatabaseService.INSTANCE.save(messageRecord);
          } catch (Exception ignored) {
            // can not find one, insert a new one
            try {
              DatabaseService.INSTANCE.save(new ChannelMessageRecord(
                      getClientId(), channelId, message));
            } catch (Exception ignored1) {
            }
          }
        });
      } catch (Exception ignored) {
      }
    }
  }

  private List<Message> batchRequestMessages(ApiRequest request, int pageSize) throws Exception {
    var hasNextRequest = true;
    var totalMessages = new ArrayList<Message>();
    var response = (ApiResponse) null;
    var messageListResponse = (MessageListResponse) null;

    while (hasNextRequest) {
      response = request(request);

      if (response.isSuccess()) {
        messageListResponse = new Gson().fromJson(response.getJson(), MessageListResponse.class);
        var messages = messageListResponse.getMessages();
        totalMessages.addAll(messages);

        if (!messageListResponse.getNextPageToken().isEmpty()) {
          hasNextRequest = true;
          request.addQuery("next_page_token", messageListResponse.getNextPageToken());
        } else {
          hasNextRequest = false;
        }
      } else {
        throw new Exception(response.getErrorMessage());
      }
    }

    return totalMessages;
  }

  public Result<String> send(String message, String toContact, String toChannel) {
    // check inputs
    if (Validator.stringIsNullOrEmpty(message)) {
      return new Result<>(null, "You must enter a message");
    }
    if ((toContact != null && toChannel != null)
            || (toContact == null && toChannel == null)) {
      return new Result<>(null, "You must enter only a contact id or a channel id");
    }

    // configure end point
    var path = PATH + "/users/me/messages";
    var configuration = new ApiRequest(BASE_URL, path, HttpMethod.POST);

    // configure queries
    if (toContact != null) {
      configuration.setPayload(
              new Gson().toJson(
                      Map.of("message", message,
                              "to_contact", toContact)));
    }
    if (toChannel != null) {
      configuration.setPayload(
              new Gson().toJson(
                      Map.of("message", message,
                              "to_channel", toChannel)));
    }

    // configure result
    var response = request(configuration);
    var idAddedDateResponse = response.isSuccess()
            ? new Gson().fromJson(response.getJson(), IdAddedDateResponse.class)
            : null;
    var id = idAddedDateResponse == null ? "" : idAddedDateResponse.getId();
    var result = new Result<>(id, retrieveErrorMessage(response));

    // insert into cache when success (channel messages only, ignore contact messages)
    if (result.isSuccess()) {
      if (toChannel != null) {
        try {
          DatabaseService.INSTANCE.save(new ChannelMessageRecord(getClientId(), toChannel,
                  new Message(id, message, "", "", 0)));
        } catch (Exception ignored) {
        }
      }
    }

    return result;
  }

  public Result<Boolean> update(String messageId, String message, String toContact, String toChannel) {
    // check inputs
    if (Validator.stringIsNullOrEmpty(messageId, message)) {
      return new Result<>(null, "You must enter an id and a message");
    }
    if ((toContact != null && toChannel != null)
            || (toContact == null && toChannel == null)) {
      return new Result<>(null, "You must enter only a contact id or a channel id");
    }

    // configure end point
    var path = PATH + "/users/me/messages/" + messageId;
    var configuration = new ApiRequest(BASE_URL, path, HttpMethod.PUT);

    // configure queries
    if (toContact != null) {
      configuration.setPayload(
              new Gson().toJson(
                      Map.of("message", message,
                              "to_contact", toContact)));
    }
    if (toChannel != null) {
      configuration.setPayload(
              new Gson().toJson(
                      Map.of("message", message,
                              "to_channel", toChannel)));
    }

    // configure result
    var result = requestAndResponseBooleanResult(configuration);

    // update cache when success (channel messages only, ignore contact messages)
    if (result.isSuccess()) {
      if (toChannel != null) {
        try {
          var messageRecord = DatabaseService.INSTANCE.findChannelMessageRecordsByClientIdAndMessageId(getClientId(), messageId);
          messageRecord.setMessage(message);
          DatabaseService.INSTANCE.save(messageRecord);
        } catch (Exception ignored) {
        }
      }
    }

    return result;
  }

  public Result<Boolean> delete(String messageId, String toContact, String toChannel) {
    // check inputs
    if (Validator.stringIsNullOrEmpty(messageId)) {
      return new Result<>(null, "You must enter an id");
    }
    if ((toContact != null && toChannel != null)
            || (toContact == null && toChannel == null)) {
      return new Result<>(null, "You must enter only a contact id or a channel id");
    }

    // configure end point
    var path = PATH + "/users/me/messages/" + messageId;
    var request = new ApiRequest(BASE_URL, path, HttpMethod.DELETE);

    // configure queries
    if (toContact != null) {
      request.setQueries(Map.of("to_contact", toContact));
    }
    if (toChannel != null) {
      request.setQueries(Map.of("to_channel", toChannel));
    }

    // configure result
    var result = requestAndResponseBooleanResult(request);

    // delete from cache when success
    if (result.isSuccess()) {
      try {
        var messageRecord = DatabaseService.INSTANCE.findChannelMessageRecordsByClientIdAndMessageId(getClientId(), messageId);
        DatabaseService.INSTANCE.delete(messageRecord);
      } catch (Exception ignored) {
      }
    }

    return result;
  }
}
