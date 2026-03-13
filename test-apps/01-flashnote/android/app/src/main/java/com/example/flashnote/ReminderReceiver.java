package com.example.flashnote;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "ReminderReceiver";
    private static final String CHANNEL_ID = "flashnote_reminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        String noteId = intent.getStringExtra("noteId");
        String noteTitle = intent.getStringExtra("noteTitle");

        Log.d(TAG, "Reminder fired for note: " + noteId);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("FlashNote Reminder")
            .setContentText(noteTitle != null ? noteTitle : "You have a note to review")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true);

        // Click notification → open the note
        Intent openIntent = new Intent(context, NoteEditorActivity.class);
        openIntent.putExtra("noteId", noteId);
        openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        builder.setContentIntent(
            android.app.PendingIntent.getActivity(context, 0, openIntent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_IMMUTABLE)
        );

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.notify(noteId.hashCode(), builder.build());
        }
    }
}
