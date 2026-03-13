package com.example.flashnote;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final String PREFS_SETTINGS = "flashnote_settings";

    private LinearLayout notesList;
    private EditText etSearch;
    private NoteStore noteStore;
    private List<Note> allNotes = new ArrayList<>();
    private SharedPreferences settingsPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noteStore = new NoteStore(this);
        settingsPrefs = getSharedPreferences(PREFS_SETTINGS, MODE_PRIVATE);

        notesList = findViewById(R.id.notesList);
        etSearch = findViewById(R.id.etSearch);

        // Search filter
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                filterNotes(s.toString());
            }
        });

        // Add note button
        Button btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, NoteEditorActivity.class);
            startActivity(intent);
        });

        // Settings button
        Button btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });

        Log.d(TAG, "MainActivity created");
    }

    @Override
    protected void onResume() {
        super.onResume();
        allNotes = noteStore.loadAll();
        displayNotes(allNotes);
        applySettings();
        Log.d(TAG, "Resumed with " + allNotes.size() + " notes");
    }

    private void applySettings() {
        boolean darkMode = settingsPrefs.getBoolean("dark_mode", false);
        int fontSize = settingsPrefs.getInt("font_size", 16);

        View root = findViewById(android.R.id.content);
        if (darkMode) {
            root.setBackgroundColor(Color.parseColor("#1a1a2e"));
        } else {
            root.setBackgroundColor(Color.WHITE);
        }
    }

    private void displayNotes(List<Note> notes) {
        notesList.removeAllViews();
        int fontSize = settingsPrefs.getInt("font_size", 16);
        boolean darkMode = settingsPrefs.getBoolean("dark_mode", false);

        for (Note note : notes) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(24, 16, 24, 16);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 0, 0, 12);
            card.setLayoutParams(lp);
            card.setBackgroundColor(darkMode ? Color.parseColor("#16213e") : Color.parseColor("#f0f0f0"));

            // Title
            TextView tvTitle = new TextView(this);
            tvTitle.setText(note.getTitle().isEmpty() ? "Untitled" : note.getTitle());
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize + 2);
            tvTitle.setTextColor(darkMode ? Color.WHITE : Color.BLACK);
            card.addView(tvTitle);

            // Preview
            TextView tvPreview = new TextView(this);
            tvPreview.setText(note.getPreview());
            tvPreview.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize - 2);
            tvPreview.setTextColor(darkMode ? Color.LTGRAY : Color.DKGRAY);
            card.addView(tvPreview);

            // Date
            TextView tvDate = new TextView(this);
            tvDate.setText(note.getFormattedDate());
            tvDate.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize - 4);
            tvDate.setTextColor(darkMode ? Color.GRAY : Color.parseColor("#888888"));
            card.addView(tvDate);

            // Click → open editor
            card.setOnClickListener(v -> {
                Intent intent = new Intent(this, NoteEditorActivity.class);
                intent.putExtra("noteId", note.getId());
                startActivity(intent);
            });

            notesList.addView(card);
        }

        if (notes.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No notes yet. Tap 'Add Note' to create one.");
            empty.setTextColor(Color.GRAY);
            empty.setPadding(0, 48, 0, 0);
            notesList.addView(empty);
        }
    }

    private void filterNotes(String query) {
        if (query.isEmpty()) {
            displayNotes(allNotes);
            return;
        }
        String lower = query.toLowerCase();
        List<Note> filtered = new ArrayList<>();
        for (Note note : allNotes) {
            if (note.getTitle().toLowerCase().contains(lower)
                || note.getContent().toLowerCase().contains(lower)) {
                filtered.add(note);
            }
        }
        displayNotes(filtered);
    }
}
