package com.github.dbchar.zoomapi.models.payloads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InviteMemberPayload {
    private final List<Map<String, String>> members;

    public InviteMemberPayload(List<String> emails) {
        var maps = new ArrayList<Map<String, String>>();

        for (var email : emails) {
            var map = new HashMap<String, String>();
            map.put("email", email);
            maps.add(map);
        }

        this.members = maps;
    }
}
