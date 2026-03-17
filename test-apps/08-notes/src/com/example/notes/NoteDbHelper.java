package com.example.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLiteOpenHelper that manages the "notes" table.
 * Seeds with 2 sample notes on creation.
 */
public class NoteDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "notes.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE_NOTES = "notes";

    private static NoteDbHelper sInstance;

    /** Get or create the shared singleton instance. */
    public static synchronized NoteDbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new NoteDbHelper(context);
        }
        return sInstance;
    }

    public NoteDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NOTES + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "body TEXT, " +
                "updated_at INTEGER)");

        // Seed 2 sample notes
        long now = System.currentTimeMillis();
        insertRaw(db, "Sample Note 1", "This is the first sample note with some content to read.", now);
        insertRaw(db, "Sample Note 2", "A second sample note for testing search and display features.", now + 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

    private void insertRaw(SQLiteDatabase db, String title, String body, long updatedAt) {
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("body", body);
        cv.put("updated_at", updatedAt);
        db.insert(TABLE_NOTES, null, cv);
    }

    /** Add a new note. Returns the new row id. */
    public int addNote(String title, String body) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("body", body);
        cv.put("updated_at", System.currentTimeMillis());
        long id = db.insert(TABLE_NOTES, null, cv);
        return (int) id;
    }

    /** Update an existing note. */
    public void updateNote(Note note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", note.title);
        cv.put("body", note.body);
        cv.put("updated_at", System.currentTimeMillis());
        db.update(TABLE_NOTES, cv, "_id = ?", new String[]{String.valueOf(note.id)});
    }

    /** Delete a note by id. */
    public void deleteNote(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NOTES, "_id = ?", new String[]{String.valueOf(id)});
    }

    /** Get all notes ordered by updated_at desc. */
    public List<Note> getNotes() {
        List<Note> notes = new ArrayList();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_NOTES, null, null, null, null, null, "updated_at DESC");
        if (c != null && c.moveToFirst()) {
            do {
                notes.add(cursorToNote(c));
            } while (c.moveToNext());
            c.close();
        }
        return notes;
    }

    /** Get a single note by id. */
    public Note getNote(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_NOTES, null, "_id = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (c != null && c.moveToFirst()) {
            Note note = cursorToNote(c);
            c.close();
            return note;
        }
        return null;
    }

    /** Search notes by title or body (case-insensitive contains). */
    public List<Note> searchNotes(String query) {
        List<Note> allNotes = getNotes();
        List<Note> results = new ArrayList();
        String lowerQuery = query.toLowerCase(java.util.Locale.US);
        for (int i = 0; i < allNotes.size(); i++) {
            Note n = allNotes.get(i);
            boolean titleMatch = n.title != null && n.title.toLowerCase(java.util.Locale.US).indexOf(lowerQuery) >= 0;
            boolean bodyMatch = n.body != null && n.body.toLowerCase(java.util.Locale.US).indexOf(lowerQuery) >= 0;
            if (titleMatch || bodyMatch) {
                results.add(n);
            }
        }
        return results;
    }

    private Note cursorToNote(Cursor c) {
        return new Note(
            c.getInt(c.getColumnIndex("_id")),
            c.getString(c.getColumnIndex("title")),
            c.getString(c.getColumnIndex("body")),
            c.getLong(c.getColumnIndex("updated_at"))
        );
    }
}
