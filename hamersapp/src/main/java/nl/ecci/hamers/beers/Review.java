package nl.ecci.hamers.beers;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

class Review {

    public static final String REVIEW = "REVIEW";

    private final int id;
    @SerializedName("beer_id")
    private final int beerID;
    @SerializedName("user_id")
    private final int userID;
    private final String description;
    private final int rating;
    private final Date proefdatum;
    @SerializedName("created_at")
    private final Date createdAt;

    public Review(int id, int beerID, int userID, String description, int rating, Date createdAt, Date proefdatum) {
        super();
        this.id = id;
        this.beerID = beerID;
        this.userID = userID;
        this.description = description;
        this.rating = rating;
        this.createdAt = createdAt;
        this.proefdatum = proefdatum;
    }

    public int getID() {
        return id;
    }

    public int getBeerID() {
        return beerID;
    }

    public int getUserID() {
        return userID;
    }

    public String getDescription() {
        return description;
    }

    public int getRating() {
        return rating;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getProefdatum() {
        return proefdatum;
    }
}