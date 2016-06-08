package nl.ecci.hamers.users;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class User {

    public enum Member {
        @SerializedName("lid") LID,
        @SerializedName("alid") ALID,
        @SerializedName("olid") OLID
    }

    private final String name;
    @SerializedName("id")
    private final int userID;
    private final String email;
    @SerializedName("quotes")
    private final int quoteCount;
    @SerializedName("reviews")
    private final int reviewCount;
    @SerializedName("lid")
    private final Member member;
    private final int batch;
    private final ArrayList<Nickname> nicknames;
    @SerializedName("created_at")
    private final Date createdAt;

    public static final String USER_ID = "USER_ID";

    @SuppressWarnings("SameParameterValue")
    public User(String name, int userID, String email, int quoteCount, int reviewCount, Member member, int batch, ArrayList<Nickname> nicknames, Date createdAt) {
        super();
        this.name = name;
        this.userID = userID;
        this.email = email;
        this.quoteCount = quoteCount;
        this.reviewCount = reviewCount;
        this.member = member;
        this.batch = batch;
        this.nicknames = nicknames;
        this.createdAt = createdAt;
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

    public int getQuoteCount() {
        return quoteCount;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public Member getMember() {
        return member;
    }

    public int getBatch() {
        return batch;
    }

    public ArrayList<Nickname> getNicknames() {
        return nicknames;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public class Nickname {
        private final String nickname;

        public Nickname(String nickname) {
            super();
            this.nickname = nickname;
        }

        public String getNickname() {
            return nickname;
        }
    }
}