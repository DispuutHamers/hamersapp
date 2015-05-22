package nl.ecci.Hamers.News;

import java.util.Date;

class NewsItem {

    private final String title;
    private final String body;
    private final String category;
    private final Date date;

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
