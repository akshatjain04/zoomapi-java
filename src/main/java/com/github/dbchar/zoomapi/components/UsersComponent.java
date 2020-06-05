package com.github.dbchar.zoomapi.components;

import com.github.dbchar.zoomapi.components.queries.DeleteUserQueriesConfiguration;
import com.github.dbchar.zoomapi.components.queries.UsersQueriesConfiguration;
import com.github.dbchar.zoomapi.models.User;
import com.github.dbchar.zoomapi.models.payloads.CreateUserPayload;
import com.github.dbchar.zoomapi.models.payloads.UpdateUserPayload;
import com.github.dbchar.zoomapi.models.responses.UserListResponse;
import com.github.dbchar.zoomapi.network.ApiRequest;
import com.github.dbchar.zoomapi.network.HttpMethod;
import com.github.dbchar.zoomapi.utils.ListResult;
import com.github.dbchar.zoomapi.utils.Result;
import com.github.dbchar.zoomapi.utils.Validator;
import com.google.gson.Gson;

/**
 * Created by Junxian Chen on 2020-04-14.
 *
 * @see <a href="https://marketplace.zoom.us/docs/api-reference/zoom-api/users/">zoom-api/users</a>
 */
public class UsersComponent extends BaseComponent {
    private static final String COMPONENT_PATH = "users";

    public enum LoginType {
        FACEBOOK(0), GOOGLE(1), API(99), ZOOM(100), SSO(101);

        private final int value;

        LoginType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public ListResult<User> list(UsersQueriesConfiguration queriesConfiguration) {
        // configure end point
        var request = new ApiRequest(BASE_URL, COMPONENT_PATH, HttpMethod.GET);

        // configure queries
        if (queriesConfiguration != null) {
            request.addQuery("page_size", queriesConfiguration.getPageSize());
            request.addQuery("page_number", queriesConfiguration.getPageNumber());
            request.addQuery("role_id", queriesConfiguration.getRoleId());
            request.addQuery("status", queriesConfiguration.getStatus().getName());
        }

        // configure result
        var response = request(request);
        var users = response.isSuccess()
                ? new Gson().fromJson(response.getJson(), UserListResponse.class).getUsers()
                : null;
        return new ListResult<>(users, retrieveErrorMessage(response));
    }

    public Result<User> create(CreateUserPayload payload) {
        // configure end point
        var request = new ApiRequest(BASE_URL, COMPONENT_PATH, HttpMethod.POST);

        // build payloads
        var jsonPayload = new Gson().toJson(payload, CreateUserPayload.class);
        request.setPayload(jsonPayload);

        // configure result
        var response = request(request);
        var user = response.isSuccess()
                ? new Gson().fromJson(response.getJson(), User.class)
                : null;
        return new Result<>(user, retrieveErrorMessage(response));
    }

    public Result<Boolean> update(String userId, UpdateUserPayload payload) {
        // check inputs
        if (Validator.stringIsNullOrEmpty(userId)) {
            return new Result<>(null, "You must enter an user id!");
        }

        // configure end point
        var path = COMPONENT_PATH + "/" + userId;
        var request = new ApiRequest(BASE_URL, path, HttpMethod.PATCH);

        // build payloads
        var jsonPayload = new Gson().toJson(payload, UpdateUserPayload.class);
        request.setPayload(jsonPayload);

        // configure result
        return requestAndResponseBooleanResult(request);
    }

    public Result<Boolean> delete(String userId, DeleteUserQueriesConfiguration queriesConfiguration) {
        // check inputs
        if (Validator.stringIsNullOrEmpty(userId)) {
            return new Result<>(null, "You must enter an user id!");
        }

        // configure end point
        var path = COMPONENT_PATH + "/" + userId;
        var request = new ApiRequest(BASE_URL, path, HttpMethod.DELETE);

        // configure queries
        if (queriesConfiguration != null) {
            request.addQuery("action", queriesConfiguration.getAction().getName());
            request.addQuery("transfer_email", queriesConfiguration.getTransferEmail());
            request.addQuery("transfer_meeting", queriesConfiguration.isTransferMeeting());
            request.addQuery("transfer_webinar", queriesConfiguration.isTransferWebinar());
            request.addQuery("transfer_recording", queriesConfiguration.isTransferRecording());
        }

        // configure result
        return requestAndResponseBooleanResult(request);
    }

    /***
     * https://marketplace.zoom.us/docs/api-reference/zoom-api/users/user
     * @param id The user id, pass 'me' here for user-level apps
     * @return null if request failed or user is request success
     */
    public Result<User> get(String id) {
        // check inputs
        if (Validator.stringIsNullOrEmpty(id)) {
            return new Result<>(null, "You must enter an user id!");
        }

        // configure end point
        var path = COMPONENT_PATH + "/" + id;
        var request = new ApiRequest(BASE_URL, path, HttpMethod.GET);

        return requestAndResponseUserResult(request);
    }

    private Result<User> requestAndResponseUserResult(ApiRequest request) {
        var response = request(request);
        var user = response.isSuccess()
                ? new Gson().fromJson(response.getJson(), User.class)
                : null;
        return new Result<>(user, retrieveErrorMessage(response));
    }
}
