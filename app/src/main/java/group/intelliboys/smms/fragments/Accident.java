package group.intelliboys.smms.fragments;

public class Accident {
    private String location;
    private String time;
    private String date;

    public Accident(String location, String time, String date) {
        this.location = location;
        this.time = time;
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }
}
