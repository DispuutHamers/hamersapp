package nl.ecci.hamers.meetings;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Meeting {
    private final int id;
    private final String agenda;
    private final String notes;
    @SerializedName("onderwerp")
    private final String subject;
    @SerializedName("user_id")
    private final int userID;
    private final Date date;
    @SerializedName("created_at")
    private final Date createdAt;
    @SerializedName("updated_at")
    private final Date updatedAt;

    public Meeting(int id, String agenda, String notes, String subject, int userID, Date date, Date createdAt, Date updatedAt) {
        super();
        this.id = id;
        this.agenda = agenda;
        this.notes = notes;
        this.subject = subject;
        this.userID = userID;
        this.date = date;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public String getAgenda() {
        return agenda;
    }

    public String getNotes() {
        return notes;
    }

    public String getSubject() {
        return subject;
    }

    public int getUserID() {
        return userID;
    }

    public Date getDate() {
        return date;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
