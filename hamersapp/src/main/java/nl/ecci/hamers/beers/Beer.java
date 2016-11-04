package nl.ecci.hamers.beers;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Beer {

    public static final String BEER = "BEER";

    private final int id;
    private String name;
    @SerializedName("soort")
    private String kind;
    @SerializedName("picture")
    private final String imageURL;
    private String percentage;
    private String brewer;
    private String country;
    @SerializedName("cijfer")
    private String rating;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public void setBrewer(String brewer) {
        this.brewer = brewer;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}