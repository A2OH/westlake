package android.os;

public final class Message implements Parcelable {
    public int arg1 = 0;
    public int arg2 = 0;
    public Object obj = null;
    public int replyTo = 0;
    public int sendingUid = 0;
    public int what = 0;
    public Handler target = null;

    private static final int POOL_SIZE = 10;
    private static final Message[] sPool = new Message[POOL_SIZE];
    private static int sPoolSize = 0;

    public Message() {}

    public void copyFrom(Message o) {
        if (o != null) {
            this.what = o.what;
            this.arg1 = o.arg1;
            this.arg2 = o.arg2;
            this.obj = o.obj;
            this.target = o.target;
        }
    }

    public int describeContents() { return 0; }
    public Runnable getCallback() { return null; }
    private Bundle mData;
    public Bundle getData() { if (mData == null) mData = new Bundle(); return mData; }
    public Handler getTarget() { return target; }
    public long getWhen() { return 0L; }
    public boolean isAsynchronous() { return false; }

    public static Message obtain() {
        synchronized (sPool) {
            if (sPoolSize > 0) {
                Message m = sPool[--sPoolSize];
                sPool[sPoolSize] = null;
                m.what = 0;
                m.arg1 = 0;
                m.arg2 = 0;
                m.obj = null;
                m.target = null;
                return m;
            }
        }
        return new Message();
    }

    public static Message obtain(Message orig) {
        Message m = obtain();
        if (orig != null) m.copyFrom(orig);
        return m;
    }

    public static Message obtain(Handler h) {
        Message m = obtain();
        m.target = h;
        return m;
    }

    public static Message obtain(Handler h, int what) {
        Message m = obtain();
        m.target = h;
        m.what = what;
        return m;
    }

    public static Message obtain(Handler h, Runnable callback) {
        Message m = obtain();
        m.target = h;
        return m;
    }

    public static Message obtain(Handler h, int what, Object obj) {
        Message m = obtain();
        m.target = h;
        m.what = what;
        m.obj = obj;
        return m;
    }

    public static Message obtain(Handler h, int what, int arg1, int arg2) {
        Message m = obtain();
        m.target = h;
        m.what = what;
        m.arg1 = arg1;
        m.arg2 = arg2;
        return m;
    }

    public static Message obtain(Handler h, int what, int arg1, int arg2, Object obj) {
        Message m = obtain();
        m.target = h;
        m.what = what;
        m.arg1 = arg1;
        m.arg2 = arg2;
        m.obj = obj;
        return m;
    }

    public Bundle peekData() { return mData; }

    public void recycle() {
        what = 0;
        arg1 = 0;
        arg2 = 0;
        obj = null;
        target = null;
        synchronized (sPool) {
            if (sPoolSize < POOL_SIZE) {
                sPool[sPoolSize++] = this;
            }
        }
    }

    public void sendToTarget() {
        if (target != null) target.sendMessage(this);
    }

    public void setAsynchronous(boolean async) {}
    public void setData(Bundle data) { mData = data; }
    public void setTarget(Handler t) { target = t; }
    public void writeToParcel(Parcel p0, int p1) {}
}
