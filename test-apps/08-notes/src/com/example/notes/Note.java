package com.example.notes;

/**
 * POJO representing a single note.
 */
public class Note {
    public int id;
    public String title;
    public String body;
    public long updatedAt;

    public Note() {}

    public Note(int id, String title, String body, long updatedAt) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.updatedAt = updatedAt;
    }

    /** Returns the first 50 chars of body as a preview. */
    public String getPreview() {
        if (body == null) return "";
        if (body.length() <= 50) return body;
        return body.substring(0, 50);
    }

    @Override
    public String toString() {
        return title + ": " + getPreview();
    }
}
