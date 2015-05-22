package nl.ecci.Hamers.Quotes;

class Quote {
    private final String user;
    private final String body;
    private final String date;
    private final int userID;

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