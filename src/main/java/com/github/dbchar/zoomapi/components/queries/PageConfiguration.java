package com.github.dbchar.zoomapi.components.queries;

public class PageConfiguration {
    public static final int MAX_PAGE_SIZE = 100;

    private final int pageSize;
    private final String nextPageToken;

    public PageConfiguration(int pageSize, String nextPageToken) {
        this.pageSize = pageSize;
        this.nextPageToken = nextPageToken;
    }

    public PageConfiguration(int pageSize) {
        this(pageSize, null);
    }

    public int getPageSize() {
        return pageSize;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }
}
