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
import com.example.todolist.*;
import com.ohos.shim.bridge.OHBridge;
import java.util.List;

/**
 * Headless test runner for TodoList app.
 * Exercises: SQLite CRUD, ListView, Intent extras, Activity lifecycle,
 * SharedPreferences, and Canvas rendering validation.
 *
 * Run: java -cp build-todolist TodoListRunner
 * Expected: 15 PASS, 0 FAIL
 */
public class TodoListRunner {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== TodoList End-to-End Test ===\n");

        try {
            // 1. Initialize MiniServer
            MiniServer.init("com.example.todolist");
            MiniServer server = MiniServer.get();
            MiniActivityManager am = server.getActivityManager();
            check("MiniServer initialized", server != null && am != null);

            // 2. Launch TodoListActivity — verify 3 seeded items
            Intent listIntent = new Intent();
            listIntent.setComponent(new ComponentName(
                    "com.example.todolist", "com.example.todolist.TodoListActivity"));
            am.startActivity(null, listIntent, -1);

            Activity listAct = am.getResumedActivity();
            check("TodoListActivity launched", listAct instanceof TodoListActivity);
            TodoListActivity todoList = (TodoListActivity) listAct;

            List<TodoItem> items = todoList.getTodoItems();
            check("TodoListActivity has 3 seeded items", items.size() == 3);

            // 3. ListView shows 3 items
            View decor = todoList.getWindow().getDecorView();
            ListView lv = findListView(decor);
            check("ListView shows 3 items", lv != null && lv.getChildCount() == 3);

            // 4. Add a new todo via TodoDbHelper
            TodoDbHelper dbHelper = todoList.getDbHelper();
            int newId = dbHelper.addTodo("Deploy to production", "Run final smoke tests first", 3);
            check("Add new todo returns valid id", newId > 0);

            // 5. Verify count is now 4
            List<TodoItem> updatedItems = dbHelper.getTodos();
            check("Todo count is now 4", updatedItems.size() == 4);

            // 6. Open TodoDetailActivity for first item via Intent
            TodoItem firstItem = updatedItems.get(0); // highest priority first
            Intent detailIntent = new Intent();
            detailIntent.setComponent(new ComponentName(
                    "com.example.todolist", "com.example.todolist.TodoDetailActivity"));
            detailIntent.putExtra("todo_id", firstItem.id);
            detailIntent.putExtra("todo_title", firstItem.title);
            detailIntent.putExtra("todo_description", firstItem.description);
            detailIntent.putExtra("todo_completed", firstItem.completed);
            detailIntent.putExtra("todo_priority", firstItem.priority);
            am.startActivity(listAct, detailIntent, 100);

            Activity detailAct = am.getResumedActivity();
            check("TodoDetailActivity launched", detailAct instanceof TodoDetailActivity);
            TodoDetailActivity detail = (TodoDetailActivity) detailAct;

            // 7. Verify title/description loaded from extras
            check("Detail shows correct title and description",
                    firstItem.title.equals(detail.getTodoTitle()) &&
                    firstItem.description.equals(detail.getTodoDescription()));

            // 8. Mark todo as completed, verify update persists
            TodoItem toComplete = dbHelper.getTodo(firstItem.id);
            toComplete.completed = true;
            dbHelper.updateTodo(toComplete);
            TodoItem reloaded = dbHelper.getTodo(firstItem.id);
            check("Mark completed persists", reloaded != null && reloaded.completed);

            // 9. Delete a todo, verify count decreases
            dbHelper.deleteTodo(newId);
            List<TodoItem> afterDelete = dbHelper.getTodos();
            check("Delete todo: count back to 3", afterDelete.size() == 3);

            // 10. Open TodoStatsActivity, verify counts
            detail.finish();
            Intent statsIntent = new Intent();
            statsIntent.setComponent(new ComponentName(
                    "com.example.todolist", "com.example.todolist.TodoStatsActivity"));
            am.startActivity(listAct, statsIntent, 200);

            Activity statsAct = am.getResumedActivity();
            check("TodoStatsActivity launched", statsAct instanceof TodoStatsActivity);
            TodoStatsActivity stats = (TodoStatsActivity) statsAct;
            check("Stats: total=3, completed=1, pending=2",
                    stats.getTotalCount() == 3 &&
                    stats.getCompletedCount() == 1 &&
                    stats.getPendingCount() == 2);

            // 11. SharedPreferences stores last_viewed timestamp
            SharedPreferences prefs = stats.getPrefs();
            long lastViewed = prefs.getLong("last_viewed", 0);
            check("SharedPreferences last_viewed > 0", lastViewed > 0);

            // ---- Canvas Render Validation ----
            System.out.println("\n--- Canvas Render Validation ---");

            // 12. Render TodoListActivity
            List<OHBridge.DrawRecord> listLog = renderAndGetLog(listAct, 480, 800);
            check("Canvas: TodoListActivity renders 'My TODOs' header",
                    hasDrawText(listLog, "My TODOs"));

            // 13. Render TodoListActivity item titles
            check("Canvas: TodoListActivity renders todo item title",
                    hasDrawText(listLog, "Buy groceries") || hasDrawText(listLog, "Write unit tests"));

            // 14. Render TodoDetailActivity
            List<OHBridge.DrawRecord> detailLog = renderAndGetLog(detailAct, 480, 800);
            check("Canvas: TodoDetailActivity renders detail text",
                    hasDrawText(detailLog, "TODO Detail") || hasDrawText(detailLog, "Title:"));

            // 15. Render TodoStatsActivity
            List<OHBridge.DrawRecord> statsLog = renderAndGetLog(statsAct, 480, 800);
            check("Canvas: TodoStatsActivity renders statistics",
                    hasDrawText(statsLog, "TODO Statistics") || hasDrawText(statsLog, "Total:"));

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

    /** Simulate a surface, render the Activity's View tree, and return the draw log. */
    private static List<OHBridge.DrawRecord> renderAndGetLog(Activity activity, int w, int h) {
        activity.onSurfaceCreated(0, w, h);
        activity.renderFrame();
        long surfaceCtx = getSurfaceCtx(activity);
        long canvasHandle = OHBridge.surfaceGetCanvas(surfaceCtx);
        List<OHBridge.DrawRecord> log = OHBridge.getDrawLog(canvasHandle);
        return log;
    }

    /** Get the Activity's mSurfaceCtx field via reflection. */
    private static long getSurfaceCtx(Activity activity) {
        try {
            java.lang.reflect.Field f = Activity.class.getDeclaredField("mSurfaceCtx");
            f.setAccessible(true);
            return f.getLong(activity);
        } catch (Exception e) {
            return 0;
        }
    }

    /** Check if draw log contains a drawText with the given substring. */
    private static boolean hasDrawText(List<OHBridge.DrawRecord> log, String text) {
        for (OHBridge.DrawRecord r : log) {
            if ("drawText".equals(r.op) && r.text != null && r.text.contains(text)) {
                return true;
            }
        }
        return false;
    }

    /** Check if draw log contains the given operation type. */
    private static boolean hasDrawOp(List<OHBridge.DrawRecord> log, String op) {
        for (OHBridge.DrawRecord r : log) {
            if (op.equals(r.op)) return true;
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
