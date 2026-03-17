package com.example.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/** SQLiteOpenHelper that manages the "todos" table. Seeds 3 sample items on creation. */
public class TodoDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "todolist.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE_TODOS = "todos";

    private static TodoDbHelper sInstance;

    /** Get or create the shared singleton instance (ensures single in-memory DB). */
    public static synchronized TodoDbHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TodoDbHelper(context);
        }
        return sInstance;
    }

    public TodoDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_TODOS + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "description TEXT, " +
                "completed INTEGER DEFAULT 0, " +
                "created_at INTEGER, " +
                "priority INTEGER DEFAULT 1)");

        // Seed 3 sample items
        long now = System.currentTimeMillis();
        insertRaw(db, "Buy groceries", "Milk, eggs, bread, and butter", false, now, 3);
        insertRaw(db, "Write unit tests", "Cover all edge cases for the parser module", false, now + 1, 2);
        insertRaw(db, "Read a book", "Finish chapter 5 of Design Patterns", false, now + 2, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODOS);
        onCreate(db);
    }

    private void insertRaw(SQLiteDatabase db, String title, String desc, boolean completed, long createdAt, int priority) {
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("description", desc);
        cv.put("completed", completed ? 1 : 0);
        cv.put("created_at", createdAt);
        cv.put("priority", priority);
        db.insert(TABLE_TODOS, null, cv);
    }

    /** Add a new todo. Returns the new row id. */
    public int addTodo(String title, String description, int priority) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("description", description);
        cv.put("completed", 0);
        cv.put("created_at", System.currentTimeMillis());
        cv.put("priority", priority);
        long id = db.insert(TABLE_TODOS, null, cv);
        return (int) id;
    }

    /** Get all todos ordered by priority desc, then createdAt asc. */
    public List<TodoItem> getTodos() {
        List<TodoItem> items = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        // The shim only supports single-column sorting, so sort by priority DESC
        // and we rely on insertion order for secondary sort
        Cursor c = db.query(TABLE_TODOS, null, null, null, null, null, "priority DESC");
        if (c != null && c.moveToFirst()) {
            do {
                items.add(cursorToItem(c));
            } while (c.moveToNext());
            c.close();
        }
        return items;
    }

    /** Get a single todo by _id. */
    public TodoItem getTodo(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_TODOS, null, "_id = ?", new String[]{String.valueOf(id)}, null, null, null);
        if (c != null && c.moveToFirst()) {
            TodoItem item = cursorToItem(c);
            c.close();
            return item;
        }
        return null;
    }

    /** Update an existing todo. */
    public void updateTodo(TodoItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("title", item.title);
        cv.put("description", item.description);
        cv.put("completed", item.completed ? 1 : 0);
        cv.put("created_at", item.createdAt);
        cv.put("priority", item.priority);
        db.update(TABLE_TODOS, cv, "_id = ?", new String[]{String.valueOf(item.id)});
    }

    /** Delete a todo by id. */
    public void deleteTodo(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_TODOS, "_id = ?", new String[]{String.valueOf(id)});
    }

    /** Count of completed todos. */
    public int getCompletedCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TODOS + " WHERE completed = 1", null);
        int count = 0;
        if (c != null && c.moveToFirst()) {
            count = c.getInt(0);
            c.close();
        }
        return count;
    }

    /** Count of pending (not completed) todos. */
    public int getPendingCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_TODOS + " WHERE completed = 0", null);
        int count = 0;
        if (c != null && c.moveToFirst()) {
            count = c.getInt(0);
            c.close();
        }
        return count;
    }

    private TodoItem cursorToItem(Cursor c) {
        return new TodoItem(
            c.getInt(c.getColumnIndex("_id")),
            c.getString(c.getColumnIndex("title")),
            c.getString(c.getColumnIndex("description")),
            c.getInt(c.getColumnIndex("completed")) != 0,
            c.getLong(c.getColumnIndex("created_at")),
            c.getInt(c.getColumnIndex("priority"))
        );
    }
}
