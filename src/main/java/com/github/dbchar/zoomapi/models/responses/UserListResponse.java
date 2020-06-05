package com.github.dbchar.zoomapi.models.responses;

import com.github.dbchar.zoomapi.models.User;

import java.util.List;

public class UserListResponse extends PaginationResponse {
    private List<User> users;

    public List<User> getUsers() {
        return users;
    }
}
