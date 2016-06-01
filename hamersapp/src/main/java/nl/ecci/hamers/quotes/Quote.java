package nl.ecci.hamers.quotes;

import java.util.Date;

public class Quote {
    private final String username;
    private final String body;
    private final Date date;
    private final int userID;

    public Quote(String username, String body, Date date, int userID) {
        super();
        this.username = username;
        this.body = body;
        this.date = date;
        this.userID = userID;
    }

    public String getBody() {
        return body;
    }

    public Date getDate() {
        return date;
    }

    public String getUsername() {
        return username;
    }

    public int getUserID() {
        return userID;
    }
}