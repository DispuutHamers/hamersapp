package nl.ecci.hamers.news;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

class News {

    private final int id;
    private final String title;
    private final String body;
    @SerializedName("cat")
    private final String category;
    private final Date date;
    @SerializedName("created_at")
    private final Date createdAt;

    public News(int id, String title, String body, String category, Date date, Date createdAt) {
        super();
        this.id = id;
        this.title = title;
        this.body = body;
        this.category = category;
        this.date = date;
        this.createdAt = createdAt;
    }

    public int getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getCategory() {
        return category;
    }

    public Date getDate() {
        return date;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public enum Category {
        @SerializedName("d")DISPUUT,
        @SerializedName("e")EXTERN,
        @SerializedName("l")LEDEN
    }
}
