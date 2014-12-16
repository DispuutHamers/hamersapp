package com.ecci.Hamers;

public class Quote {

    private String body;
    private String date;
    private int userID;

    public Quote(String body, String date, int userID) {
        super();
        this.body = body;
        this.date = date;
        this.userID = userID;
    }

    public String getBody() {
        return body;
    }

    public String getDate() {
        return date;
    }

    public int getUserID() {
        return userID;
    }
}