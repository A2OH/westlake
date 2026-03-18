package android.view.textclassifier;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Parcelable;

public final class TextClassificationContext implements Parcelable {
    private String mPackageName;
    private String mWidgetType;

    public TextClassificationContext() {}
    private TextClassificationContext(String packageName, String widgetType) {
        mPackageName = packageName;
        mWidgetType = widgetType;
    }

    public String getPackageName() { return mPackageName; }
    public String getWidgetType() { return mWidgetType; }

    public int describeContents() { return 0; }
    public void writeToParcel(Parcel p0, int p1) {}

    public static class Builder {
        private String mPackageName;
        private String mWidgetType;
        public Builder(String packageName, String widgetType) {
            mPackageName = packageName;
            mWidgetType = widgetType;
        }
        public Builder setWidgetVersion(String widgetVersion) { return this; }
        public TextClassificationContext build() {
            return new TextClassificationContext(mPackageName, mWidgetType);
        }
    }
}
