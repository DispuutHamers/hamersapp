package nl.ecci.Hamers.Quotes;

public class Quote {
    private String user;
    private String body;
    private String date;
    private int userID;

    public Quote(String user, String body, String date, int userID) {
        super();
        this.user = user;
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

    public String getUser() {
        return user;
    }

    public int getUserID() {
        return userID;
    }
}