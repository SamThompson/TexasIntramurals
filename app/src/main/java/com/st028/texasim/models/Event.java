package com.st028.texasim.models;

import java.util.Comparator;

/**
 * Created by st028 on 9/22/14.
 */
public class Event {
    public int id;
    public String name;

    public boolean equals(Object other) {
        if (other instanceof Event) {
            Event oe = (Event) other;
            return oe.id == this.id && oe.name.equals(this.name);
        }

        return false;
    }

    public static class EventComparator implements Comparator<Event> {

        @Override
        public int compare(Event event, Event event2) {
            return event.name.compareTo(event2.name);
        }

    }
}
