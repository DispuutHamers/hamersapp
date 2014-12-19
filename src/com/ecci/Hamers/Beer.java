package com.ecci.Hamers;

public class Beer {

    private String name;
    private String soort;
    private String pictureURL;
    private String percentage;
    private String brewer;
    private String country;

    public Beer(String name, String soort, String pictureURL, String percentage, String brewer, String country) {
        super();
        this.name = name;
        this.soort = soort;
        this.pictureURL = pictureURL;
        this.percentage = percentage;
        this.brewer = brewer;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public String getSoort() {
        return soort;
    }

    public String getPictureUrl() {
        return pictureURL;
    }

    public String getPercentage() { return percentage; }

    public String getBrewer() { return brewer; }

    public String getCountry() { return country; }
}