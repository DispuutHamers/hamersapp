package nl.ecci.hamers.users;

public class User {
    private final String name;
    private final int userID;
    private final String email;
    private final int quotecount;
    private final int reviewcount;
    private final boolean member;
    private final int batch;
    private final String nickname;

    public static final String USER_ID = "USER_ID";

    public User(String name, int userID, String email, int quotecount, int reviewcount, boolean member, int batch, String nickname) {
        super();
        this.name = name;
        this.userID = userID;
        this.email = email;
        this.quotecount = quotecount;
        this.reviewcount = reviewcount;
        this.member = member;
        this.batch = batch;
        this.nickname = nickname;
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

    public boolean isMember() {
        return member;
    }

    public String getNickname() {
        return nickname;
    }

    public int getBatch() {
        return batch;
    }
}