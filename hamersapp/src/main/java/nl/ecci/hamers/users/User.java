package nl.ecci.hamers.users;

public class User {
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_ID = "USER_ID";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_QUOTECOUNT = "USER_QUOTECOUNT";
    public static final String USER_REVIEWCOUNT = "USER_REVIEWCOUNT";
    public static final String USER_IMAGE_URL = "USER_IMAGE_URL";
    private final String name;
    private final int userID;
    private final String email;
    private final int quotecount;
    private final int reviewcount;

    public User(String name, int userID, String email, int quotecount, int reviewcount) {
        super();
        this.name = name;
        this.userID = userID;
        this.email = email;
        this.quotecount = quotecount;
        this.reviewcount = reviewcount;
    }

    public String getName() {
        return name;
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