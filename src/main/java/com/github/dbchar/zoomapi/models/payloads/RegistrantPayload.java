package com.github.dbchar.zoomapi.models.payloads;

import java.util.List;

/**
 * Created by Junxian Chen on 2020-04-20.
 */
public class RegistrantPayload {
    String email;
    String first_name;
    String last_name;
    String address;
    String city;
    String country;
    String zip;
    String state;
    String phone;
    String industry;
    String org;
    String job_title;
    String purchasing_time_frame;
    String role_in_purchase_process;
    String no_of_employees;
    String comments;

    class CustomQuestions {
        String title;
        String value;
    }

    List<CustomQuestions> custom_questions;

    public RegistrantPayload(String email, String first_name, String last_name) {
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
    }
}
