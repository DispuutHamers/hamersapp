package nl.ecci.hamers.quotes;

import java.util.Date;

class Quote {
    private final String user;
    private final String body;
    private final Date date;
    private final int userID;

    public Quote(String user, String body, Date date, int userID) {
        super();
        this.user = user;
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

    public String getUser() {
        return user;
    }

    public int getUserID() {
        return userID;
    }
}