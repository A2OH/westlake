package com.example.flashnote;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class NoteEditorActivity extends Activity {
    private static final String TAG = "NoteEditorActivity";
    private static final String CHANNEL_ID = "flashnote_reminders";

    private EditText etTitle;
    private EditText etContent;
    private NoteStore noteStore;
    private Note currentNote;
    private boolean isNewNote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        noteStore = new NoteStore(this);
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);

        // Load existing note or create new one
        String noteId = getIntent().getStringExtra("noteId");
        if (noteId != null) {
            currentNote = noteStore.findById(noteId);
            if (currentNote != null) {
                etTitle.setText(currentNote.getTitle());
                etContent.setText(currentNote.getContent());
            }
        }
        if (currentNote == null) {
            currentNote = new Note();
            isNewNote = true;
        }

        // Save button
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(v -> saveNote());

        // Remind button — sets a notification 1 hour from now
        Button btnRemind = findViewById(R.id.btnRemind);
        btnRemind.setOnClickListener(v -> setReminder());

        // Delete button
        Button btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> {
            if (!isNewNote) {
                noteStore.deleteNote(currentNote.getId());
                Toast.makeText(this, R.string.msg_deleted, Toast.LENGTH_SHORT).show();
            }
            finish();
        });

        createNotificationChannel();
        Log.d(TAG, "Editor opened for note: " + currentNote.getId());
    }

    private void saveNote() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty() && content.isEmpty()) {
            Toast.makeText(this, R.string.msg_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        currentNote.setTitle(title);
        currentNote.setContent(content);
        currentNote.setUpdatedAt(System.currentTimeMillis());
        noteStore.saveNote(currentNote);

        Toast.makeText(this, R.string.msg_saved, Toast.LENGTH_SHORT).show();
        isNewNote = false;
        Log.d(TAG, "Note saved: " + currentNote.getId());
    }

    private void setReminder() {
        // Save first
        saveNote();

        // Schedule notification 1 hour from now
        long triggerTime = System.currentTimeMillis() + (60 * 60 * 1000);
        currentNote.setReminderAt(triggerTime);
        noteStore.saveNote(currentNote);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("noteId", currentNote.getId());
        intent.putExtra("noteTitle", currentNote.getTitle());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
            this, currentNote.getId().hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        }

        Toast.makeText(this, R.string.msg_reminder_set, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Reminder set for note: " + currentNote.getId());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                getString(R.string.channel_reminders),
                NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
