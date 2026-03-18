package android.os;

/** AOSP compilation stub for ParcelableParcel. */
public class ParcelableParcel implements Parcelable {
    public ParcelableParcel() {}

    public void writeToParcel(Parcel out, int flags) {}

    public int describeContents() { return 0; }

    public static final Parcelable.Creator<ParcelableParcel> CREATOR =
        new Parcelable.Creator<ParcelableParcel>() {
            public ParcelableParcel createFromParcel(Parcel in) { return new ParcelableParcel(); }
            public ParcelableParcel[] newArray(int size) { return new ParcelableParcel[size]; }
        };
}
