package nl.ecci.hamers.events;

import org.json.JSONArray;

import java.util.Date;

public class Event {

    private final int id;
    private final String title;
    private final String description;
    private final String location;
    private final Date date;
    private final Date end_time;
    private final Date deadline;
    private final JSONArray signups;

    public Event(int id, String title, String description, String location, Date date, Date end_time, Date deadline, JSONArray signups) {
        super();
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
        this.end_time = end_time;
        this.deadline = deadline;
        this.signups = signups;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public Date getDate() {
        return date;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public Date getDeadline() {
        return deadline;
    }

    public JSONArray getSignups() {
        return signups;
    }

}
