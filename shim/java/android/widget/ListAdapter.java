package android.widget;
import android.view.View;
import android.view.ViewGroup;

/**
 * Shim: android.widget.ListAdapter interface -- backing contract for list-like widgets.
 */
public interface ListAdapter {
    int getCount();
    Object getItem(int position);
    long getItemId(int position);
    View getView(int position, View convertView, ViewGroup parent);
    default int getItemViewType(int position) { return 0; }
    default int getViewTypeCount() { return 1; }
    default boolean isEmpty() { return getCount() == 0; }
    default boolean areAllItemsEnabled() { return true; }
    default boolean isEnabled(int position) { return true; }
}
