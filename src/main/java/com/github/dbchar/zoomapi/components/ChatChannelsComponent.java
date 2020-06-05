package com.github.dbchar.zoomapi.components;

import com.github.dbchar.zoomapi.components.queries.PageConfiguration;
import com.github.dbchar.zoomapi.models.Channel;
import com.github.dbchar.zoomapi.models.User;
import com.github.dbchar.zoomapi.models.payloads.CreateChannelPayload;
import com.github.dbchar.zoomapi.models.payloads.InviteMemberPayload;
import com.github.dbchar.zoomapi.models.responses.ChannelListResponse;
import com.github.dbchar.zoomapi.models.responses.IdAddedDateResponse;
import com.github.dbchar.zoomapi.models.responses.MemberListResponse;
import com.github.dbchar.zoomapi.network.ApiRequest;
import com.github.dbchar.zoomapi.network.HttpMethod;
import com.github.dbchar.zoomapi.sqlite.tables.ChannelMemberRecord;
import com.github.dbchar.zoomapi.sqlite.tables.ChannelRecord;
import com.github.dbchar.zoomapi.utils.ListResult;
import com.github.dbchar.zoomapi.utils.Result;
import com.github.dbchar.zoomapi.utils.Validator;
import com.github.dbchar.zoomapi.utils.services.DatabaseService;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Junxian Chen on 2020-04-14.
 *
 * @see <a href="https://marketplace.zoom.us/docs/api-reference/zoom-api/chat-channels/">zoom-api/chat-channels</a>
 */
public class ChatChannelsComponent extends BaseComponent {
  // region Private Constant

  private static final String PATH = "chat";

  // endregion

  // region Public APIs

  public ListResult<Channel> list(PageConfiguration pageConfiguration, boolean useCache) {
    if (!useCache) return list(pageConfiguration);

    // get channels from cache
    try {
      var channels = DatabaseService.INSTANCE.findChannelRecordsByClientId(getClientId())
              .stream()
              .map(ChannelRecord::toChannel)
              .collect(Collectors.toList());
      return new ListResult<>(channels, null);
    } catch (Exception e) {
      return new ListResult<>(null, e.getMessage());
    }
  }

  public ListResult<Channel> list(PageConfiguration pageConfiguration) {
    // configure end point
    var path = PATH + "/users/me/channels";
    var request = new ApiRequest(BASE_URL, path, HttpMethod.GET);

    // configure queries
    if (pageConfiguration != null) {
      request.setQueries(makePageQueries(pageConfiguration));
    }

    // configure result
    var response = request(request);
    var latestChannels = response.isSuccess()
            ? new Gson().fromJson(response.getJson(), ChannelListResponse.class).getChannels()
            : null;

    // try to save channels to cache
    // delete all then insert all (reliable)
    if (latestChannels != null) {
      try {
        var channelRecordsCached = DatabaseService.INSTANCE.findChannelRecordsByClientId(getClientId());
        for (var channel : channelRecordsCached) {
          DatabaseService.INSTANCE.delete(channel);
        }
        for (var channel : latestChannels) {
          DatabaseService.INSTANCE.save(new ChannelRecord(getClientId(), channel));
        }
      } catch (Exception ignored) {
      }
    }

    return new ListResult<>(latestChannels, retrieveErrorMessage(response));
  }

  public Result<String> getChannelId(String channelName, boolean useCache) {
    var result = list(null, useCache);

    if (result.isSuccess()) {
      var channel = result.getItems().stream()
              .filter(c -> c.getName().equals(channelName))
              .findAny()
              .orElse(null);

      if (channel == null) {
        return new Result<>(null, "Failed to find channel " + channelName);
      }

      return new Result<>(channel.getId(), null);
    } else {
      return new Result<>(null, "Failed to get channels from server: " + result.getErrorMessage());
    }
  }

  public Result<Channel> create(String name, List<String> emails) {
    // check inputs
    if (Validator.stringIsNullOrEmpty(name) || emails == null) {
      return new Result<>(null, "You must enter a name and emails");
    }

    // configure queries
    var path = PATH + "/users/me/channels";
    var request = new ApiRequest(BASE_URL, path, HttpMethod.POST);

    // build payloads
    var payload = new CreateChannelPayload(name, 1, emails);
    var jsonPayload = new Gson().toJson(payload, CreateChannelPayload.class);
    request.setPayload(jsonPayload);

    // configure result
    var result = requestAndResponseChannelResult(request);

    // cache when create successfully
    if (result.isSuccess()) {
      try {
        DatabaseService.INSTANCE.save(new ChannelRecord(getClientId(), result.getItem()));
      } catch (Exception ignored) {
      }
    }

    return result;
  }

  public Result<Boolean> update(String channelId, String name) {
    // check inputs
    if (Validator.stringIsNullOrEmpty(channelId, name)) {
      return new Result<>(null, "You must enter a channel id and name");
    }

    // configure queries
    var path = PATH + "/channels/" + channelId;
    var request = new ApiRequest(BASE_URL, path, HttpMethod.PATCH);

    // build payloads
    var jsonPayload = new Gson().toJson(Map.of("name", name));
    request.setPayload(jsonPayload);

    // configure result
    var result = requestAndResponseBooleanResult(request);

    // cache when update successfully
    if (result.isSuccess()) {
      try {
        var channelRecord = DatabaseService.INSTANCE.findChannelRecordByClientIdAndChannelId(getClientId(), channelId);
        channelRecord.setName(name);
        DatabaseService.INSTANCE.save(channelRecord);
      } catch (Exception ignored) {
      }
    }

    return result;
  }

  public Result<Boolean> delete(String channelId) {
    // check inputs
    if (Validator.stringIsNullOrEmpty(channelId)) {
      return new Result<>(null, "You must enter a channel id");
    }

    // configure queries
    var path = PATH + "/channels/" + channelId;
    var request = new ApiRequest(BASE_URL, path, HttpMethod.DELETE);

    // configure result
    var result = requestAndResponseBooleanResult(request);

    // cache when delete successfully
    updateDatabaseAfterDeleteOrLeave(result, getClientId(), channelId);

    return result;
  }

  public Result<Channel> get(String channelId) {
    // check inputs
    if (Validator.stringIsNullOrEmpty(channelId)) {
      return new Result<>(null, "You must enter an channel id!");
    }

    // configure end point
    var path = PATH + "/channels/" + channelId;
    var request = new ApiRequest(BASE_URL, path, HttpMethod.GET);

    // configure result
    var result = requestAndResponseChannelResult(request);

    // cache when get successfully
    if (result.isSuccess()) {
      try {
        // find one, update
        var channelRecord = DatabaseService.INSTANCE.findChannelRecordByClientIdAndChannelId(getClientId(), channelId);
        channelRecord.setName(result.getItem().getName());
        channelRecord.setType(result.getItem().getType());
        DatabaseService.INSTANCE.save(channelRecord);
      } catch (Exception ignored) {
        // not find in the db, insert a new one
        try {
          DatabaseService.INSTANCE.save(new ChannelRecord(getClientId(), channelId, result.getItem().getName(), result.getItem().getType()));
        } catch (Exception ignored1) {
        }
      }
    }

    return result;
  }

  public ListResult<User> listMembers(String channelId, PageConfiguration pageConfiguration, boolean useCache) {
    if (!useCache) return listMembers(channelId, pageConfiguration);

    try {
      var ChannelMemberRecords = DatabaseService.INSTANCE.findChannelMemberRecordsByClientIdAndChannelId(getClientId(), channelId);
      var members = ChannelMemberRecords
              .stream()
              .map(ChannelMemberRecord::toUser)
              .collect(Collectors.toUnmodifiableList());
      return new ListResult<>(members, null);
    } catch (Exception e) {
      return new ListResult<>(null, e.getMessage());
    }
  }

  public ListResult<User> listMembers(String channelId, PageConfiguration pageConfiguration) {
    // check inputs
    if (Validator.stringIsNullOrEmpty(channelId)) {
      return new ListResult<>(null, "You must enter an channel id!");
    }

    // configure end point
    var path = PATH + "/channels/" + channelId + "/members";
    var request = new ApiRequest(BASE_URL, path, HttpMethod.GET);

    // configure queries
    if (pageConfiguration != null) {
      request.setQueries(makePageQueries(pageConfiguration));
    }

    // configure result
    var response = request(request);
    var latestMembers = response.isSuccess()
            ? new Gson().fromJson(response.getJson(), MemberListResponse.class).getMembers()
            : null;

    // try to save members to cache
    // delete all then insert all (reliable)
    if (latestMembers != null) {
      try {
        var memberRecordsCached = DatabaseService.INSTANCE.findChannelMemberRecordsByClientIdAndChannelId(getClientId(), channelId);
        for (var member : memberRecordsCached) {
          DatabaseService.INSTANCE.delete(member);
        }
        for (var member : latestMembers) {
          DatabaseService.INSTANCE.save(new ChannelMemberRecord(getClientId(), channelId, member));
        }
      } catch (Exception ignored) {
      }
    }

    return new ListResult<>(latestMembers, retrieveErrorMessage(response));
  }

  public Result<IdAddedDateResponse> inviteMembers(String channelId, List<String> emails) {
    // check inputs
    if (Validator.stringIsNullOrEmpty(channelId) || emails == null) {
      return new Result<>(null, "You must enter a name and emails");
    }

    // configure end point
    var path = PATH + "/channels/" + channelId + "/members";
    var request = new ApiRequest(BASE_URL, path, HttpMethod.POST);

    // build payloads
    var payload = new InviteMemberPayload(emails);
    var jsonPayload = new Gson().toJson(payload, InviteMemberPayload.class);
    request.setPayload(jsonPayload);

    // configure result
    var result = requestAndResponseIdAddedDateResult(request);

    // save to cache if invite successfully
    if (result.isSuccess()) {
      Arrays.stream(result.getItem().getIds().split(",")).forEach(userId -> {
        try {
          DatabaseService.INSTANCE.save(new ChannelMemberRecord(getClientId(), channelId, userId));
        } catch (Exception ignored) {
        }
      });
    }

    return result;
  }

  public Result<Boolean> deleteMembers(String channelId, String memberId) {
    // check inputs
    if (Validator.stringIsNullOrEmpty(channelId, memberId)) {
      return new Result<>(null, "You must enter a channel and a member id");
    }

    // configure queries
    String path = PATH + "/channels/" + channelId + "/members/" + memberId;
    ApiRequest request = new ApiRequest(BASE_URL, path, HttpMethod.DELETE);

    // configure result
    var result = requestAndResponseBooleanResult(request);

    // update cache if delete successfully
    if (result.isSuccess()) {
      try {
        var memberRecord = DatabaseService.INSTANCE.findChannelMemberRecordsByClientIdAndChannelIdAndUserId(getClientId(), channelId, memberId);
        DatabaseService.INSTANCE.delete(memberRecord);
      } catch (Exception ignored) {
      }
    }

    return result;
  }

  public Result<IdAddedDateResponse> join(String channelId) {
    // check inputs
    if (Validator.stringIsNullOrEmpty(channelId)) {
      return new Result<>(null, "You must enter a channel id");
    }

    // configure queries
    String path = PATH + "/channels/" + channelId + "/members/me";
    ApiRequest request = new ApiRequest(BASE_URL, path, HttpMethod.POST);

    // configure result
    var result = requestAndResponseIdAddedDateResult(request);

    // cache when join successfully
    if (result.isSuccess()) {
      try {
        DatabaseService.INSTANCE.save(new ChannelRecord(getClientId(), channelId));
        DatabaseService.INSTANCE.save(new ChannelMemberRecord(getClientId(), channelId, result.getItem().getId()));
      } catch (Exception ignored) {
      }
    }

    return result;
  }

  public Result<Boolean> leave(String channelId) {
    // check inputs
    if (Validator.stringIsNullOrEmpty(channelId)) {
      return new Result<>(null, "You must enter a channel id");
    }

    // configure queries
    String path = PATH + "/channels/" + channelId + "/members/me";
    ApiRequest request = new ApiRequest(BASE_URL, path, HttpMethod.DELETE);

    // configure result
    var result = requestAndResponseBooleanResult(request);

    // cache when delete successfully
    updateDatabaseAfterDeleteOrLeave(result, getClientId(), channelId);

    return result;
  }

  // endregion

  // region Private Methods

  private void updateDatabaseAfterDeleteOrLeave(Result<Boolean> result, String clientId, String channelId) {
    if (result.isSuccess()) {
      try {
        // delete channel message table
        DatabaseService.INSTANCE.findChannelMessageRecordsByClientIdAndChannelId(clientId, channelId)
                .forEach(messageRecord -> {
                  try {
                    DatabaseService.INSTANCE.delete(messageRecord);
                  } catch (Exception ignored) {
                  }
                });

        // delete channel member table
        DatabaseService.INSTANCE.findChannelMemberRecordsByClientIdAndChannelId(clientId, channelId)
                .forEach(memberRecord -> {
                  try {
                    DatabaseService.INSTANCE.delete(memberRecord);
                  } catch (Exception ignored) {
                  }
                });

        // delete channel table
        var channel = DatabaseService.INSTANCE.findChannelRecordByClientIdAndChannelId(clientId, channelId);
        DatabaseService.INSTANCE.delete(channel);
      } catch (Exception ignored) {
      }
    }
  }

  private Result<Channel> requestAndResponseChannelResult(ApiRequest request) {
    var response = request(request);
    var channel = response.isSuccess()
            ? new Gson().fromJson(response.getJson(), Channel.class)
            : null;
    return new Result<>(channel, retrieveErrorMessage(response));
  }

  private Result<IdAddedDateResponse> requestAndResponseIdAddedDateResult(ApiRequest request) {
    // configure result
    var response = request(request);
    var idAddedDateResponse = response.isSuccess()
            ? new Gson().fromJson(response.getJson(), IdAddedDateResponse.class)
            : null;
    return new Result<>(idAddedDateResponse, retrieveErrorMessage(response));
  }

  // endregion
}
