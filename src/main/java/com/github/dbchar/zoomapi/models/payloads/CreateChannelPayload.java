package com.github.dbchar.zoomapi.models.payloads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateChannelPayload {
    private final int type;
    private final List<Map<String, String>> members;
    private final String name;

    public CreateChannelPayload(String name, int type, List<String> emails) {
        this.type = type;
        this.name = name;

        var maps = new ArrayList<Map<String, String>>();

        for (var email : emails) {
            var map = new HashMap<String, String>();
            map.put("email", email);
            maps.add(map);
        }

        this.members = maps;
    }
}