package nl.ecci.Hamers.Beers;

class Beer {

    private final int id;
    private final String name;
    private final String soort;
    private final String imageURL;
    private final String percentage;
    private final String brewer;
    private final String country;
    private final String rating;

    public Beer(int id, String name, String soort, String imageURL, String percentage, String brewer, String country, String rating) {
        super();
        this.id = id;
        this.name = name;
        this.soort = soort;
        this.imageURL = imageURL;
        this.percentage = percentage;
        this.brewer = brewer;
        this.country = country;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSoort() {
        return soort;
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
}