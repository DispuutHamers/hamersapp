package nl.ecci.Hamers.Beers;

public class Beer {

    private int id;
    private String name;
    private String soort;
    private String imageURL;
    private String percentage;
    private String brewer;
    private String country;
    private String rating;

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