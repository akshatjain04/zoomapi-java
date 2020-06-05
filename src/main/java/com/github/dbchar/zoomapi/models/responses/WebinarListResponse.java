package com.github.dbchar.zoomapi.models.responses;

import com.github.dbchar.zoomapi.models.Webinar;

import java.util.List;

/**
 * Created by Junxian Chen on 2020-04-20.
 */
public class WebinarListResponse extends PaginationResponse {
    private List<Webinar> webinars;

    public List<Webinar> getWebinars() {
        return webinars;
    }
}
