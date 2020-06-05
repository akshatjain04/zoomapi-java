package com.github.dbchar.zoomapi.components;

import com.github.dbchar.zoomapi.components.queries.PageConfiguration;
import com.github.dbchar.zoomapi.models.Webinar;
import com.github.dbchar.zoomapi.models.payloads.CreateWebinarPayload;
import com.github.dbchar.zoomapi.models.payloads.RegistrantPayload;
import com.github.dbchar.zoomapi.models.responses.WebinarListResponse;
import com.github.dbchar.zoomapi.network.ApiRequest;
import com.github.dbchar.zoomapi.network.HttpMethod;
import com.github.dbchar.zoomapi.utils.ListResult;
import com.github.dbchar.zoomapi.utils.Result;
import com.github.dbchar.zoomapi.utils.Validator;
import com.google.gson.Gson;

/**
 * Created by Junxian Chen on 2020-04-14.
 *
 * @see <a href="https://marketplace.zoom.us/docs/api-reference/zoom-api/webinars/">zoom-api/webinars</a>
 */
public class WebinarsComponent extends BaseComponent {

    public ListResult<Webinar> list(String userId, PageConfiguration pageConfiguration) {
        // check inputs
        if (Validator.stringIsNullOrEmpty(userId)) {
            return new ListResult<>(null, "You must enter a userId");
        }

        // configure end point
        var path = "/users/" + userId + "/webinars";
        var request = new ApiRequest(BASE_URL, path, HttpMethod.GET);

        // configure queries
        if (pageConfiguration != null) {
            request.addQuery("page_size", pageConfiguration.getPageSize());
        } else {
            request.addQuery("page_size", 300);
        }

        // configure result
        var response = request(request);
        var webinars = response.isSuccess()
                ? new Gson().fromJson(response.getJson(), WebinarListResponse.class).getWebinars()
                : null;
        return new ListResult<>(webinars, retrieveErrorMessage(response));
    }

    public Result<Webinar> create(String userId, CreateWebinarPayload payload) {
        // check inputs
        if (Validator.stringIsNullOrEmpty(userId) || payload == null) {
            return new Result<>(null, "You must enter a userId and payload");
        }

        // configure end point
        var path = "/users/" + userId + "/webinars";
        var request = new ApiRequest(BASE_URL, path, HttpMethod.GET);

        // build payloads
        var jsonPayload = new Gson().toJson(payload, CreateWebinarPayload.class);
        request.setPayload(jsonPayload);

        // configure result
        var response = request(request);
        var webinar = response.isSuccess()
                ? new Gson().fromJson(response.getJson(), Webinar.class)
                : null;
        return new Result<>(webinar, retrieveErrorMessage(response));
    }

    public Result<Webinar> get(long webinarId, String occurrenceId) {
        // check inputs
        if (Validator.stringIsNullOrEmpty(occurrenceId)) {
            return new Result<>(null, "You must enter a webinar id or occurrence id");
        }

        // configure end point
        var path = "/webinars/" + webinarId;
        var configuration = new ApiRequest(BASE_URL, path, HttpMethod.GET);

        // configure queries
        configuration.addQuery("occurrence_id", occurrenceId);

        // configure result
        var response = request(configuration);
        var webinar = response.isSuccess()
                ? new Gson().fromJson(response.getJson(), Webinar.class)
                : null;
        return new Result<>(webinar, retrieveErrorMessage(response));
    }

    public Result<Boolean> update(String webinarId, String occurrenceId, Webinar webinar) {
        // check inputs
        if (Validator.stringIsNullOrEmpty(webinarId, occurrenceId) || webinar == null) {
            return new Result<>(null, "You must enter a webinar id or occurrence id");
        }

        // configure end point
        var path = "/webinars/" + webinarId;
        var configuration = new ApiRequest(BASE_URL, path, HttpMethod.PATCH);

        // configure queries
        configuration.addQuery("occurrence_id", occurrenceId);

        // build payloads
        var jsonPayload = new Gson().toJson(webinar, Webinar.class);
        configuration.setPayload(jsonPayload);

        return requestAndResponseBooleanResult(configuration);
    }

    public Result<Boolean> delete(String webinarId, String occurrenceId) {
        // check inputs
        if (Validator.stringIsNullOrEmpty(webinarId, occurrenceId)) {
            return new Result<>(null, "You must enter a webinar id or occurrence id");
        }

        // configure end point
        var path = "/webinars/" + webinarId;
        var configuration = new ApiRequest(BASE_URL, path, HttpMethod.DELETE);

        // configure queries
        configuration.addQuery("occurrence_id", occurrenceId);

        return requestAndResponseBooleanResult(configuration);
    }

    enum ACTION {
        END
    }

    public Result<Boolean> updateStatus(String webinarId, ACTION action) {
        // check inputs
        if (Validator.stringIsNullOrEmpty(webinarId) || action == null) {
            return new Result<>(null, "You must enter a webinar id and action");
        }

        // configure end point
        var path = "/webinars/" + webinarId + "/status";
        var configuration = new ApiRequest(BASE_URL, path, HttpMethod.PUT);

        // build payloads
        var jsonPayload = new Gson().toJson(action, ACTION.class);
        configuration.setPayload(jsonPayload);

        return requestAndResponseBooleanResult(configuration);
    }

    public Result<Boolean> end(String webinarId) {
        return updateStatus(webinarId, ACTION.END);
    }

    public Result<Boolean> addRegistrant(String webinarId, String occurrenceId, RegistrantPayload payload) {
        // check inputs
        if (Validator.stringIsNullOrEmpty(webinarId, occurrenceId) || payload == null) {
            return new Result<>(null, "You must enter a webinar id or occurrence id, payload");
        }

        // configure end point
        var path = "/webinars/" + webinarId + "/registrants";
        var configuration = new ApiRequest(BASE_URL, path, HttpMethod.POST);

        // configure queries
        configuration.addQuery("occurrence_ids", occurrenceId);

        // build payloads
        var jsonPayload = new Gson().toJson(payload, RegistrantPayload.class);
        configuration.setPayload(jsonPayload);

        return requestAndResponseBooleanResult(configuration);
    }

    public Result<Boolean> register(String webinarId, String email, String firstName, String lastName) {
        return addRegistrant(webinarId, null, new RegistrantPayload(email, firstName, lastName));
    }
}
