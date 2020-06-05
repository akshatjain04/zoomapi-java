package com.github.dbchar.zoomapi.components;

import com.github.dbchar.zoomapi.components.queries.PageConfiguration;
import com.github.dbchar.zoomapi.models.User;
import com.github.dbchar.zoomapi.models.responses.ContactListResponse;
import com.github.dbchar.zoomapi.network.ApiRequest;
import com.github.dbchar.zoomapi.network.HttpMethod;
import com.github.dbchar.zoomapi.utils.ListResult;
import com.google.gson.Gson;

/**
 * Created by Junxian Chen on 2020-04-14.
 *
 * @see <a href="https://marketplace.zoom.us/docs/api-reference/zoom-api/contacts/">zoom-api/contacts</a>
 */
public class ContactsComponent extends BaseComponent {
    public enum Type {
        COMPANY("company"),
        EXTERNAL("external");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public ListResult<User> list(Type type, PageConfiguration pageConfiguration) {
        var configuration = new ApiRequest(BASE_URL, "/chat/users/me/contacts", HttpMethod.GET);

        // configure queries
        configuration.addQuery("type", type.getName());

        // configure result
        var response = request(configuration);
        var contacts = response.isSuccess()
                ? new Gson().fromJson(response.getJson(), ContactListResponse.class).getContacts()
                : null;
        return new ListResult<>(contacts, retrieveErrorMessage(response));
    }

    public ListResult<User> list(Type type) {
        return this.list(type, null);
    }

    public ListResult<User> listExternal() {
        return this.list(Type.EXTERNAL);
    }
}
