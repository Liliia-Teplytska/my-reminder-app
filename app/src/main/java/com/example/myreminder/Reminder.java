package com.example.myreminder;

import java.io.Serializable;
import java.util.Objects;

public class Reminder implements Serializable {
    private String id;
    private String title;
    private String date; // формат: yyyy-MM-dd
    private String time; // формат: HH:mm
    private String repetition; // "none", "10min", "30min", "2hour", "4hour"
    private long createdAt;

    public Reminder(String id, String title, String date, String time, String repetition) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.repetition = repetition;
        this.createdAt = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getRepetition() { return repetition; }
    public void setRepetition(String repetition) { this.repetition = repetition; }
    public long getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reminder reminder = (Reminder) o;
        return Objects.equals(id, reminder.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
