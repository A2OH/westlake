package com.example.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Gravity;
import android.widget.*;

import java.util.ArrayList;

public class ListActivity extends Activity {
    private ArrayList<String> items;
    private ArrayAdapter<String> adapter;
    private TextView countText;
    private EditText inputField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        items = new ArrayList<String>();

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(32, 48, 32, 32);

        TextView title = new TextView(this);
        title.setText("Shopping List");
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        // Input row
        LinearLayout inputRow = new LinearLayout(this);
        inputRow.setOrientation(LinearLayout.HORIZONTAL);

        inputField = new EditText(this);
        inputField.setHint("Add item...");
        inputRow.addView(inputField);

        Button addBtn = new Button(this);
        addBtn.setText("Add");
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addItem();
            }
        });
        inputRow.addView(addBtn);

        root.addView(inputRow);

        // Count display
        countText = new TextView(this);
        updateCount();
        root.addView(countText);

        // ListView
        ListView listView = new ListView(this);
        adapter = new ArrayAdapter<String>(this, 0, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                removeItem(position);
            }
        });
        root.addView(listView);

        // Back button
        Button backBtn = new Button(this);
        backBtn.setText("Back");
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        root.addView(backBtn);

        setContentView(root);
    }

    private void addItem() {
        String text = inputField.getText().toString();
        if (text.length() > 0) {
            items.add(text);
            adapter.notifyDataSetChanged();
            inputField.setText("");
            updateCount();
        }
    }

    private void removeItem(int position) {
        if (position >= 0 && position < items.size()) {
            items.remove(position);
            adapter.notifyDataSetChanged();
            updateCount();
        }
    }

    private void updateCount() {
        countText.setText(items.size() + " items");
    }

    /** Expose for testing */
    public int getItemCount() { return items.size(); }
    public ArrayList<String> getItems() { return items; }
    public EditText getInputField() { return inputField; }
    public TextView getCountText() { return countText; }

    /** Programmatic add for testing */
    public void addItemByText(String text) {
        inputField.setText(text);
        addItem();
    }

    /** Programmatic remove for testing */
    public void removeItemAt(int position) {
        removeItem(position);
    }
}
