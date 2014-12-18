package com.ecci.Hamers;

public class Quote {

    private String user;
    private String body;
    private String date;

    public Quote(String user, String body, String date) {
        super();
        this.user = user;
        this.body = body;
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public String getDate() {
        return date;
    }

    public String getUser() {
        return user;
    }
}