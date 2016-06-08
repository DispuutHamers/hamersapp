package nl.ecci.hamers.events;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class Event {

    private final int id;
    private final String title;
    @SerializedName("beschrijving")
    private final String description;
    private final String location;
    private final Date date;
    private final Date end_time;
    private final Date deadline;
    private final ArrayList<Signup> signups;
    @SerializedName("created_at")
    private final Date createdAt;

    @SuppressWarnings("SameParameterValue")
    public Event(int id, String title, String description, String location, Date date, Date end_time, Date deadline, ArrayList<Signup> signups, Date createdAt) {
        super();
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.date = date;
        this.end_time = end_time;
        this.deadline = deadline;
        this.signups = signups;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public Date getDate() {
        return date;
    }

    public Date getEndTime() {
        return end_time;
    }

    public Date getDeadline() {
        return deadline;
    }

    public ArrayList<Signup> getSignups() {
        return signups;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public class Signup {

        @SerializedName("id")
        private final int signupID;
        @SerializedName("event_id")
        private final int eventID;
        @SerializedName("user_id")
        private final int userID;
        @SerializedName("status")
        private final boolean attending;
        @SerializedName("created_at")
        private final Date createdAt;

        public Signup(int signupID, int eventID, int userID, boolean attending, Date createdAt) {
            super();
            this.signupID = signupID;
            this.eventID = eventID;
            this.userID = userID;
            this.attending = attending;
            this.createdAt = createdAt;
        }

        public int getSignupID() {
            return signupID;
        }

        public int getEventID() {
            return eventID;
        }

        public int getUserID() {
            return userID;
        }

        public boolean isAttending() {
            return attending;
        }

        public Date getCreatedAt() {
            return createdAt;
        }
    }

}
