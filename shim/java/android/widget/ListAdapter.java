package android.widget;
import android.view.View;
import android.view.ViewGroup;

/**
 * Shim: android.widget.ListAdapter interface -- backing contract for list-like widgets.
 */
public interface ListAdapter extends Adapter {
    boolean areAllItemsEnabled();
    boolean isEnabled(int position);
}
