package nl.ecci.Hamers.Beers;

public class Review {
    private int beer_id;
    private int user_id;
    private String description;
    private int rating;
    private String created_at;
    private String proefdatum;

    public Review(int beer_id, int user_id, String description, int rating, String created_at, String proefdatum) {
        super();
        this.beer_id = beer_id;
        this.user_id = user_id;
        this.description = description;
        this.rating = rating;
        this.created_at = created_at;
        this.proefdatum = proefdatum;
    }

    public int getBeer_id() {
        return beer_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getDescription() {
        return description;
    }

    public int getRating() {
        return rating;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getProefdatum() {
        return proefdatum;
    }
}