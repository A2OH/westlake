package android.view;

import android.os.Parcel;
import android.os.Parcelable;

/** Stub for AOSP compilation. Identifies the physical address of a display. */
public abstract class DisplayAddress implements Parcelable {

    public int describeContents() { return 0; }
    public void writeToParcel(Parcel dest, int flags) {}

    /** Physical display address. */
    public static final class Physical extends DisplayAddress {
        private final long mPhysicalDisplayId;

        private Physical(long physicalDisplayId) {
            mPhysicalDisplayId = physicalDisplayId;
        }

        public static Physical fromPhysicalDisplayId(long physicalDisplayId) {
            return new Physical(physicalDisplayId);
        }

        public long getPhysicalDisplayId() { return mPhysicalDisplayId; }
    }

    /** Network display address. */
    public static final class Network extends DisplayAddress {
        public static Network fromMacAddress(String macAddress) {
            return new Network();
        }
    }
}
