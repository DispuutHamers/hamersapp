package nl.ecci.Hamers.Users;

public class User {

    private String username;
    private int userID;
    private String email;
    private int quotecount;
    private int reviewcount;

    public User(String username, int userID, String email, int quotecount, int reviewcount) {
        super();
        this.username = username;
        this.userID = userID;
        this.email = email;
        this.quotecount = quotecount;
        this.reviewcount = reviewcount;
    }

    public String getUsername() {
        return username;
    }

    public int getUserID() {
        return userID;
    }

    public String getEmail() {
        return email;
    }

    public int getQuotecount() {
        return quotecount;
    }

    public int getReviewcount() {
        return reviewcount;
    }
}