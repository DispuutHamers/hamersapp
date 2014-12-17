package com.ecci.Hamers;

public class Event {

    private String title;
    private String beschrijving;
    private String date;
    private String end_time;

    public Event(String title, String beschrijving, String date, String end_time) {
        super();
        this.title = title;
        this.beschrijving = beschrijving;
        this.date = date;
        this.end_time = end_time;
    }

    public String getTitle() {
        return title;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public String getDate() {
        return date;
    }

    public String getEnd_time() {
        return end_time;
    }

}
