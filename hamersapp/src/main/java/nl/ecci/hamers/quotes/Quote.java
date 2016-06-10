package nl.ecci.hamers.quotes;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

class Quote {
    private final int id;
    private final String text;
    @SerializedName("user_id")
    private final int userID;
    @SerializedName("created_at")
    private final Date date;

    public Quote(int id, String text, int userID, Date date) {
        super();
        this.id = id;
        this.text = text;
        this.userID = userID;
        this.date = date;
    }

    public int getID() {
        return id;
    }

    public String getText() {
        return text;
    }

    @SerializedName("user_id")
    public int getUserID() {
        return userID;
    }

    public Date getDate() {
        return date;
    }
}