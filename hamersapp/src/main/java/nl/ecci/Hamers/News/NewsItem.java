package nl.ecci.Hamers.News;

import java.util.Date;

public class NewsItem {

    private String title;
    private String body;
    private String category;
    private Date date;

    public NewsItem(String title, String body, String category, Date date) {
        super();
        this.title = title;
        this.body = body;
        this.category = category;
        this.date = date;
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

}
