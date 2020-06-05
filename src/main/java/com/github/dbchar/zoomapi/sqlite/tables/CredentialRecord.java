package com.github.dbchar.zoomapi.sqlite.tables;

import com.github.dbchar.zoomapi.sqlite.annotations.Column;
import com.github.dbchar.zoomapi.sqlite.annotations.Entity;
import com.github.dbchar.zoomapi.sqlite.annotations.Id;
import com.github.dbchar.zoomapi.sqlite.annotations.Table;

/**
 * Created by Wen-Chia, Yang on 2020-05-27.
 */
@Entity
@Table(name = "credential")
public class CredentialRecord {
    @Id
    private int id;
    @Column(name = "client_id")
    private String clientId;
    @Column(name = "client_secret")
    private String clientSecret;
    @Column(name = "token")
    private String token;

    public CredentialRecord() {
    }

    public CredentialRecord(String clientId, String clientSecret, String token) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getToken() {
        return token;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "CredentialRecord{" +
                "id=" + id +
                ", clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
