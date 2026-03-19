package android.widget;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import java.util.Observer;

import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.widget.BaseAdapter — common base for concrete adapter implementations.
 *
 * Implements ListAdapter. Provides DataSetObserver registration stubs and default
 * implementations of areAllItemsEnabled() / isEnabled() so subclasses only need
 * to implement the four abstract methods.
 */
public class BaseAdapter implements ListAdapter, SpinnerAdapter {

    private final List<android.database.DataSetObserver> observers = new ArrayList<>();

    // ── Abstract methods ──

    @Override
    public int getCount() { return 0; }

    @Override
    public Object getItem(int position) { return null; }

    @Override
    public long getItemId(int position) { return 0; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) { return null; }

    // ── ListAdapter defaults ──

    @Override
    public boolean areAllItemsEnabled() { return true; }

    @Override
    public boolean isEnabled(int position) { return true; }

    @Override
    public int getItemViewType(int position) { return 0; }

    @Override
    public int getViewTypeCount() { return 1; }

    @Override
    public boolean isEmpty() { return getCount() == 0; }

    // ── SpinnerAdapter ──

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    @Override
    public boolean hasStableIds() { return false; }

    // ── Observer management ──

    public void registerDataSetObserver(android.database.DataSetObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void unregisterDataSetObserver(android.database.DataSetObserver observer) {
        observers.remove(observer);
    }

    public void notifyDataSetChanged() {
        for (android.database.DataSetObserver o : observers) o.onChanged();
    }

    public void notifyDataSetInvalidated() {
        for (android.database.DataSetObserver o : observers) o.onInvalidated();
    }
}
