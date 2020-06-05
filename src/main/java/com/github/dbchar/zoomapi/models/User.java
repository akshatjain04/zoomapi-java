package com.github.dbchar.zoomapi.models;


import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class User {
    private final String id;
    @SerializedName("first_name")
    private final String firstName;
    @SerializedName("last_name")
    private final String lastName;
    private final String email;
    @SerializedName("role_name")
    private final String roleName;
    @SerializedName("role")
    private final String role;
    @SerializedName("created_at")
    private final String createDate;

    public User(String id, String firstName, String lastName, String email, String roleName, String role, String createDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roleName = roleName;
        this.role = role;
        this.createDate = createDate;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public String getRole() {
        return role;
    }

    public String getInfo() {
        return "ID: " + id + "\nName: " + getName() + "\nEmail: " + email;
    }

    public String getCreateDate() {
        return createDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", roleName='" + roleName + '\'' +
                ", role='" + role + '\'' +
                ", createDate='" + createDate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        User user = (User) object;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
