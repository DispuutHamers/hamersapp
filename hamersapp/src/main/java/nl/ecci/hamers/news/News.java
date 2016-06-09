package nl.ecci.hamers.news;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

class News {

    public enum Category {
        @SerializedName("d")DISPUUT,
        @SerializedName("e")EXTERN,
        @SerializedName("l")LEDEN
    }

    @SerializedName("id")
    private final int newsID;
    private final String title;
    private final String body;
    @SerializedName("cat")
    private final String category;
    private final Date date;
    @SerializedName("created_at")
    private final Date createdAt;

    public News(int newsID, String title, String body, String category, Date date, Date createdAt) {
        super();
        this.newsID = newsID;
        this.title = title;
        this.body = body;
        this.category = category;
        this.date = date;
        this.createdAt = createdAt;
    }

    public int getNewsID() {
        return newsID;
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
}
