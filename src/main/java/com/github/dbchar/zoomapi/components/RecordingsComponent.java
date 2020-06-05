package com.github.dbchar.zoomapi.components;

import com.github.dbchar.zoomapi.components.queries.QueriesConfiguration;
import com.github.dbchar.zoomapi.models.MeetingRecording;
import com.github.dbchar.zoomapi.models.responses.RecordingListResponse;
import com.github.dbchar.zoomapi.network.ApiRequest;
import com.github.dbchar.zoomapi.network.HttpMethod;
import com.github.dbchar.zoomapi.utils.ListResult;
import com.github.dbchar.zoomapi.utils.Result;
import com.github.dbchar.zoomapi.utils.Validator;
import com.google.gson.Gson;

/**
 * Created by Junxian Chen on 2020-04-14.
 *
 * @see <a href="https://marketplace.zoom.us/docs/api-reference/zoom-api/recordings/">zoom-api/recordings</a>
 */
public class RecordingsComponent extends BaseComponent {
    public enum DeleteRecordingAction {
        TRASH("trash"), DELETE("delete");

        private final String name;

        DeleteRecordingAction(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public ListResult<MeetingRecording> list(String userId, QueriesConfiguration queriesConfiguration) {
        // check inputs
        if (Validator.stringIsNullOrEmpty(userId)) {
            return new ListResult<>(null, "You must enter a user id");
        }

        // configure end point
        var path = "/users/" + userId + "/recordings";
        var configuration = new ApiRequest(BASE_URL, path, HttpMethod.GET);

        // configure queries
        if (queriesConfiguration != null) {
            configuration.addQuery("page_size", queriesConfiguration.getPageSize());
            configuration.addQuery("next_page_token", queriesConfiguration.getNextPageToken());
            configuration.addQuery("mc", queriesConfiguration.getMc());
            configuration.addQuery("trash", queriesConfiguration.isTrash());
            configuration.addQuery("from", queriesConfiguration.getFrom());
            configuration.addQuery("to", queriesConfiguration.getTo());
            configuration.addQuery("trash_type", queriesConfiguration.getTrashType());
        }

        // configure result
        var response = request(configuration);
        var meetingRecordings = response.isSuccess()
                ? new Gson().fromJson(response.getJson(), RecordingListResponse.class).getRecordings()
                : null;
        return new ListResult<>(meetingRecordings, retrieveErrorMessage(response));
    }

    public Result<MeetingRecording> get(String meetingId) {
        // check inputs
        if (Validator.stringIsNullOrEmpty(meetingId)) {
            return new Result<>(null, "You must enter a meetingId or occurrence id");
        }

        // configure end point
        var path = "meetings/" + meetingId + "/recordings";
        var configuration = new ApiRequest(BASE_URL, path, HttpMethod.GET);

        // configure result
        var response = request(configuration);
        var recording = response.isSuccess()
                ? new Gson().fromJson(response.getJson(), MeetingRecording.class)
                : null;
        return new Result<>(recording, retrieveErrorMessage(response));
    }

    public Result<Boolean> delete(String meetingId, DeleteRecordingAction action) {
        // check inputs
        if (Validator.stringIsNullOrEmpty(meetingId)) {
            return new Result<>(null, "You must enter a meetingId or occurrence id");
        }

        // configure end point
        var path = "meetings/" + meetingId + "/recordings";
        var configuration = new ApiRequest(BASE_URL, path, HttpMethod.DELETE);

        // configure queries
        configuration.addQuery("action", action.getName());

        return requestAndResponseBooleanResult(configuration);
    }
}
