package nl.ecci.hamers.beers;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Beer {

    public static final String BEER = "BEER";

    private final int id;
    private final String name;
    @SerializedName("soort")
    private final String kind;
    @SerializedName("picture")
    private final String imageURL;
    private final String percentage;
    private final String brewer;
    private final String country;
    @SerializedName("cijfer")
    private final String rating;
    private final String url;
    @SerializedName("created_at")
    private final Date createdAt;

    @SuppressWarnings("SameParameterValue")
    public Beer(int id, String name, String kind, String imageURL, String percentage, String brewer, String country, String rating, String url, Date createdAt) {
        super();
        this.id = id;
        this.name = name;
        this.kind = kind;
        this.imageURL = imageURL;
        this.percentage = percentage;
        this.brewer = brewer;
        this.country = country;
        this.rating = rating;
        this.url = url;
        this.createdAt = createdAt;
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getKind() {
        return kind;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getPercentage() {
        return percentage;
    }

    public String getBrewer() {
        return brewer;
    }

    public String getCountry() {
        return country;
    }

    public String getRating() {
        return rating;
    }

    public String getUrl() {
        return url;
    }

    public Date getCreatedAt() {
        return createdAt;
    }
}