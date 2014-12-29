package nl.ecci.Hamers.Users;

public class User {

    private String username;
    private int userID;
    private int quotecount;
    private int reviewcount;

    public User(String username, int userID, int quotecount, int reviewcount) {
        super();
        this.username = username;
        this.userID = userID;
        this.quotecount = quotecount;
        this.reviewcount = reviewcount;
    }

    public String getUsername() {
        return username;
    }

    public int getUserID() {
        return userID;
    }

    public int getQuotecount() {
        return quotecount;
    }

    public int getReviewcount() { return reviewcount; }
}