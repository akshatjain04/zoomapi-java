package com.github.dbchar.zoomapi.components;

import com.github.dbchar.zoomapi.components.queries.QueriesConfiguration;
import com.github.dbchar.zoomapi.models.responses.AccountReportResponse;
import com.github.dbchar.zoomapi.models.responses.RecordingListResponse;
import com.github.dbchar.zoomapi.network.ApiRequest;
import com.github.dbchar.zoomapi.network.HttpMethod;
import com.github.dbchar.zoomapi.utils.Result;
import com.github.dbchar.zoomapi.utils.Validator;
import com.google.gson.Gson;

/**
 * Created by Junxian Chen on 2020-04-14.
 *
 * @see <a href="https://marketplace.zoom.us/docs/api-reference/zoom-api/reports/">zoom-api/reports</a>
 */
public class ReportsComponent extends BaseComponent {
    private static final String PATH = "report";

    public enum UserReportType {
        PAST("past"), PAST_ONE("pastOne");

        private final String name;

        UserReportType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum AccountReportType {
        ACTIVE("active"), INACTIVE("inactive");

        private final String name;

        AccountReportType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public Result<RecordingListResponse> getUserReport(String userId, UserReportType type, QueriesConfiguration queriesConfiguration) {
        // check inputs
        if (Validator.stringIsNullOrEmpty(userId)) {
            return new Result<>(null, "You must enter a user id");
        }

        // configure end point
        var path = PATH + "/users/" + userId + "/meetings";
        var configuration = new ApiRequest(BASE_URL, path, HttpMethod.GET);

        // configure queries
        if (queriesConfiguration != null) {
            configuration.addQuery("page_size", queriesConfiguration.getPageSize());
            configuration.addQuery("next_page_token", queriesConfiguration.getNextPageToken());
            configuration.addQuery("from", queriesConfiguration.getFrom());
            configuration.addQuery("to", queriesConfiguration.getTo());
            configuration.addQuery("type", type.getName());
        }

        // configure result
        var response = request(configuration);
        var recordingListResponse = response.isSuccess()
                ? new Gson().fromJson(response.getJson(), RecordingListResponse.class)
                : null;
        return new Result<>(recordingListResponse, retrieveErrorMessage(response));
    }

    public Result<AccountReportResponse> getAccountReport(AccountReportType type, QueriesConfiguration queriesConfiguration) {
        // configure end point
        var path = PATH + "/users";
        var configuration = new ApiRequest(BASE_URL, path, HttpMethod.GET);

        // configure queries
        if (queriesConfiguration != null) {
            configuration.addQuery("page_size", queriesConfiguration.getPageSize());
            configuration.addQuery("next_page_token", queriesConfiguration.getNextPageToken());
            configuration.addQuery("from", queriesConfiguration.getFrom());
            configuration.addQuery("to", queriesConfiguration.getTo());
            configuration.addQuery("type", type.getName());
        }

        // configure result
        var response = request(configuration);
        var accountReportResponse = response.isSuccess()
                ? new Gson().fromJson(response.getJson(), AccountReportResponse.class)
                : null;
        return new Result<>(accountReportResponse, retrieveErrorMessage(response));
    }
}
