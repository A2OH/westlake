package com.example.mockdonalds;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;

/** Main menu screen — shows all menu items in a ListView. */
public class MenuActivity extends Activity {
    private MenuDbHelper dbHelper;
    private CartManager cartManager;
    private List<MenuItem> menuItems;
    private TextView cartCountView;

    @Override
    private android.content.Context getContext() {
        try { return getApplicationContext(); } catch (Exception e) {}
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            return (android.content.Context) host.getField("instance").get(null);
        } catch (Exception e) {}
        try { return new android.content.Context(); } catch (Exception|Error e) {}
        return null;
    }

    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
        } catch (Exception e) {
            // On real Android, Activity internals may not be fully initialized
        }

        try {
            dbHelper = new MenuDbHelper(this);
            cartManager = new CartManager(this);
            menuItems = dbHelper.getAllItems();
        } catch (Exception | NoSuchMethodError e) {
            // Database not available on real Android — use hardcoded menu
            menuItems = new java.util.ArrayList<>();
            menuItems.add(new MenuItem(1, "Big Mock Burger", "Delicious", 5.99, "Burgers"));
            menuItems.add(new MenuItem(2, "Quarter Mocker", "Classic", 4.99, "Burgers"));
            menuItems.add(new MenuItem(3, "Mock Nuggets (6)", "Crispy", 3.49, "Sides"));
            menuItems.add(new MenuItem(4, "Mock Fries (L)", "Golden", 2.99, "Sides"));
            menuItems.add(new MenuItem(5, "Mock Cola (L)", "Refreshing", 1.99, "Drinks"));
            menuItems.add(new MenuItem(6, "Mock Shake", "Creamy", 3.99, "Drinks"));
            menuItems.add(new MenuItem(7, "Mock Flurry", "Sweet", 2.49, "Desserts"));
            menuItems.add(new MenuItem(8, "Apple Mock Pie", "Warm", 1.49, "Desserts"));
        }

        // Build UI programmatically
        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);

        // Header
        TextView header = new TextView(getApplicationContext());
        header.setText("MockDonalds Menu");
        header.setTextSize(24);
        header.setTextColor(0xFFFF0000);
        root.addView(header);

        // Cart count
        cartCountView = new TextView(getApplicationContext());
        cartCountView.setTextSize(14);
        updateCartCount();
        root.addView(cartCountView);

        // Menu ListView
        ListView listView = new ListView(getApplicationContext());
        listView.setAdapter(new MenuAdapter());
        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override public void onItemClick(android.widget.AdapterView parent, android.view.View view, int position, long id) {
                MenuItem item = menuItems.get(position);
                Intent intent = new Intent();
                intent.setComponent(new android.content.ComponentName(
                        getPackageName(), "com.example.mockdonalds.ItemDetailActivity"));
                intent.putExtra("item_id", item.id);
                intent.putExtra("item_name", item.name);
                intent.putExtra("item_price", item.price);
                intent.putExtra("item_description", item.description);
                startActivityForResult(intent, 100);
            }
        });
        root.addView(listView);

        // Cart button with fixed height
        Button cartBtn = new Button(getContext());
        cartBtn.setText("View Cart");
        cartBtn.setTextSize(18);
        cartBtn.setOnClickListener(new android.view.View.OnClickListener() {
            @Override public void onClick(android.view.View v) {
                Intent intent = new Intent();
                intent.setComponent(new android.content.ComponentName(
                        getPackageName(), "com.example.mockdonalds.CartActivity"));
                startActivityForResult(intent, 200);
            }
        });
        root.addView(cartBtn);

        setContentView(root);
        // Also store for host activity to pick up when running on real Android
        try {
            Class<?> host = Class.forName("com.westlake.host.WestlakeActivity");
            java.lang.reflect.Field f = host.getField("shimRootView");
            f.set(null, root);
        } catch (Exception e) { /* not running in host APK */ }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateCartCount();
    }

    private void updateCartCount() {
        int count = cartManager.getCartCount();
        cartCountView.setText("Cart: " + count + " items");
    }

    public List<MenuItem> getMenuItems() { return menuItems; }
    public CartManager getCartManager() { return cartManager; }
    public MenuDbHelper getDbHelper() { return dbHelper; }

    /** Adapter for the menu ListView. */
    private class MenuAdapter extends BaseAdapter {
        @Override public int getCount() { return menuItems.size(); }
        @Override public Object getItem(int position) { return menuItems.get(position); }
        @Override public long getItemId(int position) { return menuItems.get(position).id; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MenuItem item = menuItems.get(position);
            LinearLayout row = new LinearLayout(getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);

            TextView nameView = new TextView(getApplicationContext());
            nameView.setText(item.name);
            nameView.setTextSize(16);
            row.addView(nameView);

            TextView priceView = new TextView(getApplicationContext());
            priceView.setText("  " + item.getPriceString());
            priceView.setTextColor(0xFF008800);
            row.addView(priceView);

            row.setTag(item);
            return row;
        }
    }
}
