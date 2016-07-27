package nl.ecci.hamers.stickers;

public class Sticker {

    public static final String ID = "ID";

    private final int id;
    private final String notes;

    // lat, lon, notes
    public Sticker(int id, String notes) {
        this.id = id;
        this.notes = notes;
    }

    public int getID() {
        return id;
    }

    public String getNotes() {
        return notes;
    }
}
