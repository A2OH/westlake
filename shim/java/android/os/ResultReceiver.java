package android.os;

/**
 * Shim: android.os.ResultReceiver
 * Pure Java — callback wrapper that dispatches results via Handler.
 */
public class ResultReceiver implements Parcelable {

    private final Handler mHandler;

    public ResultReceiver(Handler handler) {
        this.mHandler = handler;
    }

    /**
     * Deliver a result to this receiver. Will call {@link #onReceiveResult},
     * via the Handler if one was supplied.
     */
    public void send(int resultCode, Bundle resultData) {
        if (mHandler != null) {
            mHandler.post(() -> onReceiveResult(resultCode, resultData));
        } else {
            onReceiveResult(resultCode, resultData);
        }
    }

    /**
     * Override this to receive results. Called on the Handler thread if
     * a Handler was supplied, otherwise on the caller's thread.
     */
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        // Subclasses override this
    }

    public int describeContents() { return 0; }
    public void writeToParcel(Parcel dest, int flags) {}
}
