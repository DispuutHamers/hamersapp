package nl.ecci.hamers.users;

class User {

    private final String username;
    private final int userID;
    private final String email;
    private final int quotecount;
    private final int reviewcount;

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