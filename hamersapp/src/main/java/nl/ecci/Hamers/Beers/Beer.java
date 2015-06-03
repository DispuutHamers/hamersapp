package nl.ecci.Hamers.Beers;

public class Beer {

    public static final String BEER_ID = "BEER_ID";
    public static final String BEER_NAME = "BEER_NAME";
    public static final String BEER_KIND = "BEER_KIND";
    public static final String BEER_URL = "BEER_URL";
    public static final String BEER_PERCENTAGE = "BEER_PERCENTAGE";
    public static final String BEER_BREWER = "BEER_BREWER";
    public static final String BEER_COUNTRY = "BEER_COUNTRY";
    public static final String BEER_RATING = "BEER_RATING";

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