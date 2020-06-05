package com.github.dbchar.zoomapi.models.responses;

import com.github.dbchar.zoomapi.models.User;

import java.util.List;

public class MemberListResponse extends PaginationResponse {
    private List<User> members;

    public List<User> getMembers() {
        return members;
    }
}