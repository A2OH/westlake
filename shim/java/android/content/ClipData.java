package android.content;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

/**
 * Shim: android.content.ClipData
 * Pure Java — clipboard data with items.
 */
public class ClipData implements Parcelable {

    private ClipDescription mDescription;
    private final List<Item> mItems = new ArrayList<>();

    public static class Item {
        private final CharSequence mText;
        private final String mHtmlText;
        private final Intent mIntent;
        private final Uri mUri;

        public Item(CharSequence text) {
            this(text, null, null, null);
        }

        public Item(CharSequence text, String htmlText) {
            this(text, htmlText, null, null);
        }

        public Item(Intent intent) {
            this(null, null, intent, null);
        }

        public Item(Uri uri) {
            this(null, null, null, uri);
        }

        public Item(CharSequence text, String htmlText, Intent intent, Uri uri) {
            mText = text;
            mHtmlText = htmlText;
            mIntent = intent;
            mUri = uri;
        }

        public CharSequence getText() { return mText; }
        public String getHtmlText() { return mHtmlText; }
        public Intent getIntent() { return mIntent; }
        public Uri getUri() { return mUri; }

        public CharSequence coerceToText(Context context) {
            if (mText != null) return mText;
            if (mHtmlText != null) return mHtmlText;
            if (mUri != null) return mUri.toString();
            if (mIntent != null) return mIntent.toString();
            return "";
        }
    }

    public ClipData(CharSequence label, String[] mimeTypes, Item item) {
        mDescription = new ClipDescription(label, mimeTypes);
        if (item != null) mItems.add(item);
    }

    public ClipData(ClipDescription description, Item item) {
        mDescription = description;
        if (item != null) mItems.add(item);
    }

    public ClipData(ClipData other) {
        if (other != null) {
            mDescription = other.mDescription;
            mItems.addAll(other.mItems);
        }
    }

    public void addItem(Item item) {
        if (item != null) mItems.add(item);
    }

    public void addItem(ContentResolver resolver, Item item) {
        addItem(item);
    }

    public ClipDescription getDescription() { return mDescription; }
    public int getItemCount() { return mItems.size(); }

    public Item getItemAt(int index) {
        if (index < 0 || index >= mItems.size()) return null;
        return mItems.get(index);
    }

    public static ClipData newPlainText(CharSequence label, CharSequence text) {
        return new ClipData(label, new String[]{"text/plain"}, new Item(text));
    }

    public static ClipData newHtmlText(CharSequence label, CharSequence text, String htmlText) {
        return new ClipData(label, new String[]{"text/html"}, new Item(text, htmlText));
    }

    public static ClipData newIntent(CharSequence label, Intent intent) {
        return new ClipData(label, new String[]{"text/vnd.android.intent"}, new Item(intent));
    }

    public static ClipData newUri(ContentResolver resolver, CharSequence label, Uri uri) {
        return new ClipData(label, new String[]{"text/uri-list"}, new Item(uri));
    }

    public static ClipData newRawUri(CharSequence label, Uri uri) {
        return new ClipData(label, new String[]{"text/uri-list"}, new Item(uri));
    }

    public int describeContents() { return 0; }
    public void writeToParcel(Parcel dest, int flags) {}
}
