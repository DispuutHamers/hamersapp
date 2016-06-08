package nl.ecci.hamers.users;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class User {
    private final String name;
    private final int id;
    private final String email;
    @SerializedName("quotes")
    private final int quoteCount;
    @SerializedName("reviews")
    private final int reviewCount;
    private final boolean member;
    private final int batch;
    private final ArrayList<Nickname> nicknames;
    @SerializedName("created_at")
    private final Date createdAt;

    public static final String USER_ID = "USER_ID";

    public User(String name, int id, String email, int quoteCount, int reviewCount, boolean member, int batch, ArrayList<Nickname> nicknames, Date createdAt) {
        super();
        this.name = name;
        this.id = id;
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

    public int getId() {
        return id;
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

    public boolean isMember() {
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