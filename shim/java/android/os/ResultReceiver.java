package android.os;

/**
 * Shim: android.os.ResultReceiver
 * Receives a callback result from someone. Optionally dispatches via Handler.
 */
public class ResultReceiver implements Parcelable {

    private final Handler mHandler;

    public ResultReceiver(Handler handler) {
        mHandler = handler;
    }

    public void send(int resultCode, Bundle resultData) {
        if (mHandler != null) {
            final int code = resultCode;
            final Bundle data = resultData;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onReceiveResult(code, data);
                }
            });
        } else {
            onReceiveResult(resultCode, resultData);
        }
    }

    protected void onReceiveResult(int resultCode, Bundle resultData) {
        // Subclasses override this
    }

    public int describeContents() { return 0; }
    public void writeToParcel(Parcel dest, int flags) {}
}
