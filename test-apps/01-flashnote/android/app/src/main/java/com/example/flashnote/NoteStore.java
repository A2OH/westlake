package com.example.flashnote;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Stores notes as JSON in SharedPreferences.
 * Simple approach for a small note app — no SQLite needed.
 */
public class NoteStore {
    private static final String TAG = "NoteStore";
    private static final String PREFS_NAME = "flashnote_data";
    private static final String KEY_NOTES = "notes_json";

    private final SharedPreferences prefs;

    public NoteStore(Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public List<Note> loadAll() {
        List<Note> notes = new ArrayList<>();
        String json = prefs.getString(KEY_NOTES, "[]");
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                notes.add(new Note(
                    obj.getString("id"),
                    obj.getString("title"),
                    obj.getString("content"),
                    obj.getLong("createdAt"),
                    obj.getLong("updatedAt"),
                    obj.optLong("reminderAt", 0)
                ));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error loading notes", e);
        }
        // Sort by updatedAt descending (newest first)
        Collections.sort(notes, (a, b) -> Long.compare(b.getUpdatedAt(), a.getUpdatedAt()));
        return notes;
    }

    public void saveAll(List<Note> notes) {
        JSONArray arr = new JSONArray();
        for (Note note : notes) {
            try {
                JSONObject obj = new JSONObject();
                obj.put("id", note.getId());
                obj.put("title", note.getTitle());
                obj.put("content", note.getContent());
                obj.put("createdAt", note.getCreatedAt());
                obj.put("updatedAt", note.getUpdatedAt());
                obj.put("reminderAt", note.getReminderAt());
                arr.put(obj);
            } catch (JSONException e) {
                Log.e(TAG, "Error saving note", e);
            }
        }
        prefs.edit().putString(KEY_NOTES, arr.toString()).apply();
    }

    public void saveNote(Note note) {
        List<Note> notes = loadAll();
        boolean found = false;
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId().equals(note.getId())) {
                notes.set(i, note);
                found = true;
                break;
            }
        }
        if (!found) {
            notes.add(note);
        }
        saveAll(notes);
    }

    public void deleteNote(String noteId) {
        List<Note> notes = loadAll();
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId().equals(noteId)) {
                notes.remove(i);
                break;
            }
        }
        saveAll(notes);
    }

    public Note findById(String noteId) {
        for (Note note : loadAll()) {
            if (note.getId().equals(noteId)) {
                return note;
            }
        }
        return null;
    }
}
