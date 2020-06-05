package com.github.dbchar.zoomapi.clients;

import com.github.dbchar.zoomapi.components.*;

public abstract class ZoomClient {
    private final UsersComponent usersComponent;
    private final MeetingsComponent meetingsComponent;
    private final ReportsComponent reportsComponent;
    private final WebinarsComponent webinarsComponent;
    private final RecordingsComponent recordingsComponent;

    public ZoomClient() {
        this.usersComponent = new UsersComponent();
        this.meetingsComponent = new MeetingsComponent();
        this.reportsComponent = new ReportsComponent();
        this.webinarsComponent = new WebinarsComponent();
        this.recordingsComponent = new RecordingsComponent();
    }

    public UsersComponent getUsersComponent() {
        return usersComponent;
    }

    public MeetingsComponent getMeetingsComponent() {
        return meetingsComponent;
    }

    public ReportsComponent getReportsComponent() {
        return reportsComponent;
    }

    public WebinarsComponent getWebinarsComponent() {
        return webinarsComponent;
    }

    public RecordingsComponent getRecordingsComponent() {
        return recordingsComponent;
    }

    public abstract void refreshAccessToken() throws Exception;

    protected abstract void setAccessToken() throws Exception;
}
