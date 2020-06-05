package com.github.dbchar.zoomapi.models.payloads;

import com.google.gson.annotations.SerializedName;

public class UpdateUserPayload {
    @SerializedName("first_name")
    private final String firstName;
    private final String type;
    private final String pmi;
    private final String timezone;
    private final String dept;
    @SerializedName("vanity_name")
    private final String vanityName;
    @SerializedName("host_key")
    private final String hostKey;
    @SerializedName("cms_user_id")
    private final String cmsUserId;
    @SerializedName("job_title")
    private final String jobTitle;
    private final String company;
    private final String location;
    @SerializedName("phone_number")
    private final String phoneNumber;
    @SerializedName("phone_country")
    private final String phoneCountry;

    public UpdateUserPayload(String firstName, String type, String pmi, String timezone, String dept, String vanityName, String hostKey, String cmsUserId, String jobTitle, String company, String location, String phoneNumber, String phoneCountry) {
        this.firstName = firstName;
        this.type = type;
        this.pmi = pmi;
        this.timezone = timezone;
        this.dept = dept;
        this.vanityName = vanityName;
        this.hostKey = hostKey;
        this.cmsUserId = cmsUserId;
        this.jobTitle = jobTitle;
        this.company = company;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.phoneCountry = phoneCountry;
    }
}
