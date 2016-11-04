package nl.ecci.hamers.stickers;

public class Sticker {

    public static final String ID = "ID";

    private final int id;
    private final float lat;
    private final float lon;
    private final String notes;

    // lat, lon, notes
    public Sticker(int id, float lat, float lon, String notes) {
        this.id = id;
        this.notes = notes;
        this.lat = lat;
        this.lon = lon;
    }

    public int getID() {
        return id;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public String getNotes() {
        return notes;
    }
}
