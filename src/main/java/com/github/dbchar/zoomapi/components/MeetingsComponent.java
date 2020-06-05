package com.github.dbchar.zoomapi.components;

import com.github.dbchar.zoomapi.components.queries.PageConfiguration;
import com.github.dbchar.zoomapi.models.Meeting;
import com.github.dbchar.zoomapi.models.payloads.CreateMeetingPayload;
import com.github.dbchar.zoomapi.models.responses.MeetingListResponse;
import com.github.dbchar.zoomapi.network.ApiRequest;
import com.github.dbchar.zoomapi.network.HttpMethod;
import com.github.dbchar.zoomapi.utils.ListResult;
import com.github.dbchar.zoomapi.utils.Result;
import com.github.dbchar.zoomapi.utils.Validator;
import com.google.gson.Gson;

/**
 * Created by Junxian Chen on 2020-04-14.
 *
 * @see <a href="https://marketplace.zoom.us/docs/api-reference/zoom-api/meetings/">zoom-api/meetings</a>
 */
public class MeetingsComponent extends BaseComponent {
    public enum MeetingType {
        SCHEDULED("scheduled"), LIVE("live"), UPCOMING("upcoming");

        private final String name;

        MeetingType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private static final String PATH = "meetings";

    public ListResult<Meeting> list(String userId, MeetingType type, PageConfiguration pageConfiguration) {
        // check inputs
        if (Validator.stringIsNullOrEmpty(userId)) {
            return new ListResult<>(null, "You must enter a user id");
        }

        // configure end point
        var path = "/users/" + userId + "/meetings";
        var configuration = new ApiRequest(BASE_URL, path, HttpMethod.GET);

        // configure queries
        configuration.addQuery("type", type.getName());

        if (pageConfiguration != null) {
            configuration.setQueries(makePageQueries(pageConfiguration));
        }

        // configure result
        var response = request(configuration);
        var meetings = response.isSuccess()
                ? new Gson().fromJson(response.getJson(), MeetingListResponse.class).getMeetings()
                : null;
        return new ListResult<>(meetings, retrieveErrorMessage(response));
    }

    public Result<Meeting> create(String userId, CreateMeetingPayload payload) {
        // check inputs
        if (Validator.stringIsNullOrEmpty(userId)) {
            return new Result<>(null, "You must enter a user id");
        }

        if (payload == null) {
            return new Result<>(null, "You must enter a payload");
        }

        // configure queries
        var path = "/users/" + userId + "/meetings";
        var configuration = new ApiRequest(BASE_URL, path, HttpMethod.POST);

        // build payloads
        var jsonPayload = new Gson().toJson(payload, CreateMeetingPayload.class);
        configuration.setPayload(jsonPayload);

        // configure result
        var response = request(configuration);
        var meeting = response.isSuccess()
                ? new Gson().fromJson(response.getJson(), Meeting.class)
                : null;
        return new Result<>(meeting, retrieveErrorMessage(response));
    }

    public Result<Meeting> get(String meetingId, String occurrenceId) {
        // check inputs
        if (Validator.stringIsNullOrEmpty(meetingId, occurrenceId)) {
            return new Result<>(null, "You must enter a meetingId or occurrence id");
        }

        // configure end point
        var path = PATH + "/" + meetingId;
        var configuration = new ApiRequest(BASE_URL, path, HttpMethod.GET);

        // configure queries
        configuration.addQuery("occurrence_id", occurrenceId);

        // configure result
        var response = request(configuration);
        var meeting = response.isSuccess()
                ? new Gson().fromJson(response.getJson(), Meeting.class)
                : null;
        return new Result<>(meeting, retrieveErrorMessage(response));
    }

    public Result<Boolean> update(String meetingId, String occurrenceId) {
        // check inputs
        if (Validator.stringIsNullOrEmpty(meetingId, occurrenceId)) {
            return new Result<>(null, "You must enter a meetingId or occurrence id");
        }

        // configure end point
        var path = PATH + "/" + meetingId;
        var configuration = new ApiRequest(BASE_URL, path, HttpMethod.PATCH);

        // configure queries
        configuration.addQuery("occurrence_id", occurrenceId);

        return requestAndResponseBooleanResult(configuration);
    }

    public Result<Boolean> delete(String meetingId, String occurrenceId, boolean scheduleForReminder) {
        // check inputs
        if (Validator.stringIsNullOrEmpty(meetingId, occurrenceId)) {
            return new Result<>(null, "You must enter a meetingId or occurrence id");
        }

        // configure end point
        var path = PATH + "/" + meetingId;
        var configuration = new ApiRequest(BASE_URL, path, HttpMethod.DELETE);

        // configure queries
        configuration.addQuery("occurrence_id", occurrenceId);
        configuration.addQuery("schedule_for_reminder", scheduleForReminder);

        return requestAndResponseBooleanResult(configuration);
    }
}
