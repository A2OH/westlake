package com.example.mockdonalds;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends Activity {
    private List<MenuItem> menuItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try { super.onCreate(savedInstanceState); } catch (Exception e) {}

        // Get a real Context - try host Activity, fallback to this
        Context ctx = this;
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            Object inst = host.getField("instance").get(null);
            if (inst instanceof Context) ctx = (Context) inst;
        } catch (Exception e) {}

        // Hardcoded menu data (no database needed)
        menuItems = new ArrayList<>();
        menuItems.add(new MenuItem(1, "Big Mock Burger", "Delicious", 5.99, "Burgers"));
        menuItems.add(new MenuItem(2, "Quarter Mocker", "Classic", 4.99, "Burgers"));
        menuItems.add(new MenuItem(3, "Mock Nuggets (6)", "Crispy", 3.49, "Sides"));
        menuItems.add(new MenuItem(4, "Mock Fries (L)", "Golden", 2.99, "Sides"));
        menuItems.add(new MenuItem(5, "Mock Cola (L)", "Refreshing", 1.99, "Drinks"));
        menuItems.add(new MenuItem(6, "Mock Shake", "Creamy", 3.99, "Drinks"));
        menuItems.add(new MenuItem(7, "Mock Flurry", "Sweet", 2.49, "Desserts"));
        menuItems.add(new MenuItem(8, "Apple Mock Pie", "Warm", 1.49, "Desserts"));

        // Build UI with real Android Views
        final Context fCtx = ctx;
        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(0xFFF5F5F5);

        TextView header = new TextView(ctx);
        header.setText("MockDonalds Menu");
        header.setTextSize(28);
        header.setTextColor(0xFFFF0000);
        header.setPadding(16, 16, 16, 8);
        root.addView(header);

        ListView listView = new ListView(ctx);
        listView.setAdapter(new BaseAdapter() {
            public int getCount() { return menuItems.size(); }
            public Object getItem(int p) { return menuItems.get(p); }
            public long getItemId(int p) { return p; }
            public View getView(int pos, View cv, ViewGroup parent) {
                LinearLayout row = new LinearLayout(fCtx);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setPadding(16, 12, 16, 12);
                MenuItem item = menuItems.get(pos);
                TextView name = new TextView(fCtx);
                name.setText(item.name);
                name.setTextSize(20);
                name.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                row.addView(name);
                TextView price = new TextView(fCtx);
                price.setText("$" + String.format("%.2f", item.price));
                price.setTextSize(16);
                price.setTextColor(0xFF4CAF50);
                row.addView(price);
                return row;
            }
        });
        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(android.widget.AdapterView<?> parent, View view, int pos, long id) {
                MenuItem item = menuItems.get(pos);
                // Show item detail in a new view
                showItemDetail(fCtx, item);
            }
        });
        root.addView(listView, new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));

        Button cartBtn = new Button(ctx);
        cartBtn.setText("View Cart");
        cartBtn.setTextSize(18);
        root.addView(cartBtn);

        // Store root view for WestlakeActivity to display
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            host.getField("shimRootView").set(null, root);
        } catch (Exception e) {}

        try { setContentView(root); } catch (Exception e) {}
    }

    private void showItemDetail(final Context ctx, final MenuItem item) {
        LinearLayout detail = new LinearLayout(ctx);
        detail.setOrientation(LinearLayout.VERTICAL);
        detail.setBackgroundColor(0xFFFFFFFF);
        detail.setPadding(32, 32, 32, 32);

        TextView title = new TextView(ctx);
        title.setText(item.name);
        title.setTextSize(32);
        title.setTextColor(0xFF000000);
        detail.addView(title);

        TextView desc = new TextView(ctx);
        desc.setText(item.description);
        desc.setTextSize(18);
        desc.setTextColor(0xFF666666);
        desc.setPadding(0, 16, 0, 16);
        detail.addView(desc);

        TextView price = new TextView(ctx);
        price.setText("$" + String.format("%.2f", item.price));
        price.setTextSize(28);
        price.setTextColor(0xFF4CAF50);
        price.setPadding(0, 0, 0, 32);
        detail.addView(price);

        Button addBtn = new Button(ctx);
        addBtn.setText("Add to Cart");
        addBtn.setTextSize(20);
        addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Go back to menu
                try {
                    Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
                    Object shimRoot = host.getField("shimRootView").get(null);
                    if (shimRoot instanceof View) {
                        // Restore menu view
                        final View menuView = (View) shimRoot;
                        Object inst = host.getField("instance").get(null);
                        if (inst instanceof Activity) {
                            ((Activity) inst).runOnUiThread(new Runnable() {
                                public void run() {
                                    if (menuView.getParent() != null)
                                        ((ViewGroup) menuView.getParent()).removeView(menuView);
                                    try {
                                        Class<?> h = Class.forName("com.westlake.host.WestlakeActivity");
                                        ((Activity) h.getField("instance").get(null)).setContentView(menuView);
                                    } catch (Exception ex) {}
                                }
                            });
                        }
                    }
                } catch (Exception e) {}
            }
        });
        detail.addView(addBtn);

        Button backBtn = new Button(ctx);
        backBtn.setText("← Back to Menu");
        backBtn.setTextSize(16);
        backBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Same as add — go back to menu
                addBtn.performClick();
            }
        });
        detail.addView(backBtn);

        // Show detail view
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            final Activity hostAct = (Activity) host.getField("instance").get(null);
            final View detailView = detail;
            hostAct.runOnUiThread(new Runnable() {
                public void run() { hostAct.setContentView(detailView); }
            });
        } catch (Exception e) {}
    }

    public List<MenuItem> getMenuItems() { return menuItems; }
}
