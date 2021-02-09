package com.codecool.shop.utilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Log {
    private final List<HashMap<String, Object>> events = new ArrayList<>();

    public void addEvent(Date date, String description) {
        HashMap<String, Object> event = new HashMap<>();
        event.put("date", date);
        event.put("description", description);
        events.add(event);
    }

    public List<HashMap<String, Object>> getEvents() {
        return events;
    }
}
