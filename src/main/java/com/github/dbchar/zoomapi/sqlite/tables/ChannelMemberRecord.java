package com.github.dbchar.zoomapi.sqlite.tables;

import com.github.dbchar.zoomapi.models.User;
import com.github.dbchar.zoomapi.sqlite.annotations.Column;
import com.github.dbchar.zoomapi.sqlite.annotations.Entity;
import com.github.dbchar.zoomapi.sqlite.annotations.Id;
import com.github.dbchar.zoomapi.sqlite.annotations.Table;

/**
 * Created by Wen-Chia, Yang on 2020-05-27.
 */
@Entity
@Table(name = "channel_member")
public class ChannelMemberRecord {
    @Id
    private int id;
    @Column(name = "client_id")
    private String clientId;
    @Column(name = "channel_id")
    private String channelId;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email")
    private String email;
    @Column(name = "role_name")
    private String roleName;
    @Column(name = "role")
    private String role;
    @Column(name = "created_at")
    private String createdDate;

    public ChannelMemberRecord() {
    }

    public ChannelMemberRecord(String clientId,
                               String channelId,
                               String userId,
                               String firstName,
                               String lastName,
                               String email,
                               String roleName,
                               String role,
                               String createDate) {
        this.clientId = clientId;
        this.channelId = channelId;
        this.userId = userId;
        this.firstName = firstName == null ? "" : firstName;
        this.lastName = lastName == null ? "" : lastName;
        this.email = email == null ? "" : email;
        this.roleName = roleName == null ? "" : roleName;
        this.role = role == null ? "" : role;
        this.createdDate = createDate == null ? "" : roleName;
    }

    public ChannelMemberRecord(String clientId,
                               String channelId,
                               String userId) {
        this(clientId,
                channelId,
                userId,
                "",
                "",
                "",
                "",
                "",
                "");
    }

    public ChannelMemberRecord(String clientId, String channelId, User user) {
        this(clientId,
                channelId,
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRoleName(),
                user.getRole(),
                user.getCreateDate());
    }

    public User toUser() {
        return new User(userId,
                firstName,
                lastName,
                email,
                roleName,
                role,
                createdDate);
    }

    public int getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getUserId() {
        return userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "[ChannelMemberTable] id: " + id + ", channelId: '" + channelId + "', userId: '" + userId + "', firstName: '" + firstName + "', lastName: '" + lastName + "', role: " + role + ", createdDate: '" + createdDate + "'";
    }
}
