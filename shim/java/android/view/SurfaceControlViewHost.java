package android.view;
import android.content.Context;
import android.os.IBinder;

public class SurfaceControlViewHost {
    public SurfaceControlViewHost(Context p0, Display p1, IBinder p2) {}

    public void relayout(int p0, int p1) {}
    public void release() {}
    public void setView(View p0, int p1, int p2) {}

    public SurfacePackage getSurfacePackage() { return null; }

    public static class SurfacePackage implements android.os.Parcelable {
        public SurfacePackage() {}
        public SurfacePackage(SurfaceControl sc) {}

        public SurfaceControl getSurfaceControl() { return new SurfaceControl(); }
        public IBinder getInputToken() { return null; }
        public void release() {}

        public android.view.accessibility.IAccessibilityEmbeddedConnection getAccessibilityEmbeddedConnection() { return null; }

        public int describeContents() { return 0; }
        public void writeToParcel(android.os.Parcel dest, int flags) {}

        public static final android.os.Parcelable.Creator<SurfacePackage> CREATOR =
            new android.os.Parcelable.Creator<SurfacePackage>() {
                public SurfacePackage createFromParcel(android.os.Parcel in) { return new SurfacePackage(); }
                public SurfacePackage[] newArray(int size) { return new SurfacePackage[size]; }
            };
    }

}
