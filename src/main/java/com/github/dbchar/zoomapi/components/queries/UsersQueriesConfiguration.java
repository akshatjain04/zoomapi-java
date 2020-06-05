package com.github.dbchar.zoomapi.components.queries;

public class UsersQueriesConfiguration {
    public enum UserListStatus {
        ACTIVE("active"), INACTIVE("inactive"), PENDING("pending");

        private final String name;

        UserListStatus(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private final UserListStatus status;
    private final int pageSize;
    private final int pageNumber;
    private final String roleId;

    public UsersQueriesConfiguration(UserListStatus status, int pageSize, int pageNumber, String roleId) {
        this.status = status;
        this.pageSize = pageSize;
        this.pageNumber = pageNumber;
        this.roleId = roleId;
    }

    public UserListStatus getStatus() {
        return status;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public String getRoleId() {
        return roleId;
    }
}
