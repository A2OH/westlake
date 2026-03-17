package com.example.superapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory ContentProvider for testing CRUD operations.
 */
public class TestProvider extends ContentProvider {

    private final List<Map<String, String>> mRows = new ArrayList<Map<String, String>>();
    private int mNextId = 1;
    public static boolean sOnCreateCalled = false;

    public static void reset() {
        sOnCreateCalled = false;
    }

    @Override
    public boolean onCreate() {
        sOnCreateCalled = true;
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Map<String, String> row = new HashMap<String, String>();
        int id = mNextId++;
        row.put("_id", String.valueOf(id));
        if (values != null) {
            // Extract known keys
            String name = values.getAsString("name");
            if (name != null) row.put("name", name);
            String value = values.getAsString("value");
            if (value != null) row.put("value", value);
        }
        mRows.add(row);
        return Uri.parse(uri.toString() + "/" + id);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String[] columns = (projection != null) ? projection : new String[]{"_id", "name", "value"};
        MatrixCursor cursor = new MatrixCursor(columns);
        for (int i = 0; i < mRows.size(); i++) {
            Map<String, String> row = mRows.get(i);
            Object[] rowData = new Object[columns.length];
            for (int c = 0; c < columns.length; c++) {
                String val = row.get(columns[c]);
                rowData[c] = val != null ? val : "";
            }
            cursor.addRow(rowData);
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        String targetName = (selectionArgs != null && selectionArgs.length > 0) ? selectionArgs[0] : null;
        for (int i = 0; i < mRows.size(); i++) {
            Map<String, String> row = mRows.get(i);
            boolean match = (targetName == null) || targetName.equals(row.get("name"));
            if (match) {
                if (values != null) {
                    String newValue = values.getAsString("value");
                    if (newValue != null) row.put("value", newValue);
                    String newName = values.getAsString("name");
                    if (newName != null) row.put("name", newName);
                }
                count++;
            }
        }
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String targetName = (selectionArgs != null && selectionArgs.length > 0) ? selectionArgs[0] : null;
        int count = 0;
        for (int i = mRows.size() - 1; i >= 0; i--) {
            Map<String, String> row = mRows.get(i);
            boolean match = (targetName == null) || targetName.equals(row.get("name"));
            if (match) {
                mRows.remove(i);
                count++;
            }
        }
        return count;
    }

    @Override
    public String getType(Uri uri) {
        return "vnd.android.cursor.dir/test";
    }
}
