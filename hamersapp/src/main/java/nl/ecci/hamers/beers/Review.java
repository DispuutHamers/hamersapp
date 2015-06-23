package nl.ecci.hamers.beers;

class Review {
    private final int beer_id;
    private final int user_id;
    private final String description;
    private final String rating;
    private final String created_at;
    private final String proefdatum;

    public Review(int beer_id, int user_id, String description, String rating, String created_at, String proefdatum) {
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

    public String getRating() {
        return rating;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getProefdatum() {
        return proefdatum;
    }
}