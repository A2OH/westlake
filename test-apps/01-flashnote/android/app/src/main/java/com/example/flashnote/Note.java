package com.example.flashnote;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Note {
    private String id;
    private String title;
    private String content;
    private long createdAt;
    private long updatedAt;
    private long reminderAt;  // 0 = no reminder

    public Note() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
        this.title = "";
        this.content = "";
        this.reminderAt = 0;
    }

    public Note(String id, String title, String content, long createdAt, long updatedAt, long reminderAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reminderAt = reminderAt;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
    public long getReminderAt() { return reminderAt; }
    public void setReminderAt(long reminderAt) { this.reminderAt = reminderAt; }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(updatedAt));
    }

    public String getPreview() {
        if (content.length() <= 80) return content;
        return content.substring(0, 80) + "…";
    }
}
