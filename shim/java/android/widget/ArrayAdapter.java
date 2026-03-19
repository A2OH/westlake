package android.widget;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Shim: android.widget.ArrayAdapter<T> — adapter backed by a Java List.
 *
 * The resource ID and context are accepted for source compatibility but are not
 * used in this headless shim — getView() returns null, which callers must handle.
 */
public class ArrayAdapter<T> extends BaseAdapter implements Filterable, ThemedSpinnerAdapter {

    private final Context context;
    private final int resource;
    private int dropDownResource;
    private final List<T> objects;

    public ArrayAdapter(Context context, int resource) {
        this.context = context;
        this.resource = resource;
        this.dropDownResource = resource;
        this.objects = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public ArrayAdapter(Context context, int resource, T[] items) {
        this.context = context;
        this.resource = resource;
        this.dropDownResource = resource;
        this.objects = new ArrayList<>(Arrays.asList(items));
    }

    public ArrayAdapter(Context context, int resource, List<T> items) {
        this.context = context;
        this.resource = resource;
        this.dropDownResource = resource;
        this.objects = new ArrayList<>(items);
    }

    public ArrayAdapter(Context context, int resource, int textViewResourceId) {
        this.context = context;
        this.resource = resource;
        this.dropDownResource = resource;
        this.objects = new ArrayList<>();
    }

    public ArrayAdapter(Context context, int resource, int textViewResourceId, T[] items) {
        this.context = context;
        this.resource = resource;
        this.dropDownResource = resource;
        this.objects = new ArrayList<>(Arrays.asList(items));
    }

    public ArrayAdapter(Context context, int resource, int textViewResourceId, List<T> items) {
        this.context = context;
        this.resource = resource;
        this.dropDownResource = resource;
        this.objects = new ArrayList<>(items);
    }

    // ── Mutation ──

    public void add(T object) {
        objects.add(object);
        notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    public void addAll(T... items) {
        Collections.addAll(objects, items);
        notifyDataSetChanged();
    }

    public void addAll(java.util.Collection<? extends T> items) {
        objects.addAll(items);
        notifyDataSetChanged();
    }

    public void insert(T object, int index) {
        objects.add(index, object);
        notifyDataSetChanged();
    }

    public void remove(T object) {
        objects.remove(object);
        notifyDataSetChanged();
    }

    public void clear() {
        objects.clear();
        notifyDataSetChanged();
    }

    public void sort(Comparator<? super T> comparator) {
        Collections.sort(objects, comparator);
        notifyDataSetChanged();
    }

    public int getPosition(T item) {
        return objects.indexOf(item);
    }

    public Context getContext() {
        return context;
    }

    public void setDropDownViewResource(int resource) {
        this.dropDownResource = resource;
    }

    public void setNotifyOnChange(boolean notifyOnChange) {
        // stub
    }

    // ── ThemedSpinnerAdapter ──

    @Override
    public void setDropDownViewTheme(android.content.res.Resources.Theme theme) {
        // stub
    }

    @Override
    public android.content.res.Resources.Theme getDropDownViewTheme() {
        return null;
    }

    // ── Filterable ──

    @Override
    public Filter getFilter() {
        return null;
    }

    // ── BaseAdapter contract ──

    @Override
    public int getCount() { return objects.size(); }

    @Override
    public T getItem(int position) { return objects.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) { return null; }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) { return null; }

    public static ArrayAdapter<CharSequence> createFromResource(Context context, int textArrayResId, int textViewResId) {
        return new ArrayAdapter<CharSequence>(context, textViewResId);
    }
}
