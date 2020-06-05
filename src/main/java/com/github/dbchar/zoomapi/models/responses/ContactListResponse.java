package com.github.dbchar.zoomapi.models.responses;

import com.github.dbchar.zoomapi.models.User;

import java.util.List;

/**
 * Created by Junxian Chen on 2020-04-19.
 */
public class ContactListResponse extends PaginationResponse {
    private List<User> contacts;

    public List<User> getContacts() {
        return contacts;
    }
}
