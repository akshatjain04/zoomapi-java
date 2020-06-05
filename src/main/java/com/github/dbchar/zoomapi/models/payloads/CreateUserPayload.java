package com.github.dbchar.zoomapi.models.payloads;

import com.google.gson.annotations.SerializedName;

public class CreateUserPayload {
    public enum Action {
        CUST_CREATE("custCreate"), SSO_CREATE("ssoCreate");

        private final String name;

        Action(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public enum Type {
        BASIC(1), LICENSED(2), ON_PREM(3);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public class UserInfo {
        private final String email;
        private final int type;
        @SerializedName("first_name")
        private final String firstName;
        @SerializedName("last_name")
        private final String lastName;

        public UserInfo(String email, Type type, String firstName, String lastName) {
            this.email = email;
            this.type = type.getValue();
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    private final String action;
    @SerializedName("user_info")
    private final UserInfo userInfo;

    public CreateUserPayload(Action action, UserInfo userInfo) {
        this.action = action.getName();
        this.userInfo = userInfo;
    }
}
