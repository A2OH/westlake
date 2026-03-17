import android.app.Activity;
import android.app.MiniServer;
import android.app.MiniActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.example.notes.*;
import com.ohos.shim.bridge.OHBridge;
import java.util.List;

/**
 * Headless test runner for Notes app.
 * Exercises: SQLite CRUD, search, ListView, Intent extras, Activity lifecycle,
 * SharedPreferences, and Canvas rendering validation.
 *
 * Run: java -cp build-notes NotesRunner
 * Expected: 15 PASS, 0 FAIL
 */
public class NotesRunner {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== Notes End-to-End Test ===\n");

        try {
            // 1. Initialize MiniServer
            MiniServer.init("com.example.notes");
            MiniServer server = MiniServer.get();
            MiniActivityManager am = server.getActivityManager();
            check("MiniServer initialized", server != null && am != null);

            // 2. Launch NoteListActivity — verify 2 seeded notes
            Intent listIntent = new Intent();
            listIntent.setComponent(new ComponentName(
                    "com.example.notes", "com.example.notes.NoteListActivity"));
            am.startActivity(null, listIntent, -1);

            Activity listAct = am.getResumedActivity();
            check("NoteListActivity launched", listAct instanceof NoteListActivity);
            NoteListActivity noteList = (NoteListActivity) listAct;

            List<Note> notes = noteList.getNotes();
            check("NoteListActivity has 2 seeded notes", notes.size() == 2);

            // 3. ListView has 2 items
            View decor = noteList.getWindow().getDecorView();
            ListView lv = findListView(decor);
            check("ListView has 2 items", lv != null && lv.getChildCount() == 2);

            // 4. Add a new note -> count is 3
            NoteDbHelper dbHelper = noteList.getDbHelper();
            int newId = dbHelper.addNote("Test Note", "This is a test note body for testing.");
            List<Note> afterAdd = dbHelper.getNotes();
            check("Add new note: count is 3", afterAdd.size() == 3);

            // 5. Open NoteEditActivity for note 1
            Note firstNote = afterAdd.get(0);
            Intent editIntent = new Intent();
            editIntent.setComponent(new ComponentName(
                    "com.example.notes", "com.example.notes.NoteEditActivity"));
            editIntent.putExtra("note_id", firstNote.id);
            am.startActivity(listAct, editIntent, 100);

            Activity editAct = am.getResumedActivity();
            check("NoteEditActivity launched", editAct instanceof NoteEditActivity);
            NoteEditActivity noteEdit = (NoteEditActivity) editAct;

            // 6. Title loaded from Intent
            check("Title loaded from note", firstNote.title.equals(noteEdit.getTitleEdit().getText().toString()));

            // 7. Update note body -> persists
            Note noteToUpdate = dbHelper.getNote(firstNote.id);
            noteToUpdate.body = "Updated body content for persistence test.";
            dbHelper.updateNote(noteToUpdate);
            Note reloaded = dbHelper.getNote(firstNote.id);
            check("Update note body persists", "Updated body content for persistence test.".equals(reloaded.body));

            // 8. Delete note -> count decreases
            dbHelper.deleteNote(newId);
            List<Note> afterDelete = dbHelper.getNotes();
            check("Delete note: count back to 2", afterDelete.size() == 2);

            // 9. Search "sample" -> finds results
            List<Note> searchResults = dbHelper.searchNotes("sample");
            check("Search 'sample' finds results", searchResults.size() > 0);

            // 10. Search "nonexistent" -> empty
            List<Note> emptyResults = dbHelper.searchNotes("nonexistent");
            check("Search 'nonexistent' is empty", emptyResults.size() == 0);

            // 11. SharedPreferences stores last_opened_note_id
            SharedPreferences prefs = noteEdit.getPrefs();
            int lastOpenedId = prefs.getInt("last_opened_note_id", -1);
            check("SharedPreferences last_opened_note_id stored", lastOpenedId == firstNote.id);

            // ---- Canvas Render Validation ----
            System.out.println("\n--- Canvas Render Validation ---");

            // 12. Canvas: NoteListActivity renders "My Notes"
            List<OHBridge.DrawRecord> listLog = renderAndGetLog(listAct, 480, 800);
            check("Canvas: NoteListActivity renders 'My Notes'",
                    hasDrawText(listLog, "My Notes"));

            // 13. Canvas: renders note title
            check("Canvas: renders note title",
                    hasDrawText(listLog, "Sample Note") || hasDrawText(listLog, "Test Note"));

            // 14. Canvas: NoteEditActivity renders edit fields
            List<OHBridge.DrawRecord> editLog = renderAndGetLog(editAct, 480, 800);
            check("Canvas: NoteEditActivity renders 'Edit Note'",
                    hasDrawText(editLog, "Edit Note") || hasDrawText(editLog, "Title:"));

            // 15. Canvas: renders Save/Delete buttons
            check("Canvas: renders Save/Delete buttons",
                    hasDrawText(editLog, "Save") && hasDrawText(editLog, "Delete"));

        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            failed++;
        }

        System.out.println("\n=== Results ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println(failed == 0 ? "ALL TESTS PASSED" : "SOME TESTS FAILED");
        System.exit(failed);
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            System.out.println("  [PASS] " + name);
            passed++;
        } else {
            System.out.println("  [FAIL] " + name);
            failed++;
        }
    }

    private static List<OHBridge.DrawRecord> renderAndGetLog(Activity activity, int w, int h) {
        activity.onSurfaceCreated(0, w, h);
        activity.renderFrame();
        long surfaceCtx = getSurfaceCtx(activity);
        long canvasHandle = OHBridge.surfaceGetCanvas(surfaceCtx);
        List<OHBridge.DrawRecord> log = OHBridge.getDrawLog(canvasHandle);
        return log;
    }

    private static long getSurfaceCtx(Activity activity) {
        try {
            java.lang.reflect.Field f = Activity.class.getDeclaredField("mSurfaceCtx");
            f.setAccessible(true);
            return f.getLong(activity);
        } catch (Exception e) {
            return 0;
        }
    }

    private static boolean hasDrawText(List<OHBridge.DrawRecord> log, String text) {
        for (OHBridge.DrawRecord r : log) {
            if ("drawText".equals(r.op) && r.text != null && r.text.contains(text)) {
                return true;
            }
        }
        return false;
    }

    private static ListView findListView(View root) {
        if (root instanceof ListView) return (ListView) root;
        if (root instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) root;
            for (int i = 0; i < vg.getChildCount(); i++) {
                ListView found = findListView(vg.getChildAt(i));
                if (found != null) return found;
            }
        }
        return null;
    }
}
