package android.app;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
/** AOSP compilation stub. */
public interface INotificationManager extends IInterface {
    void enqueueToast(String pkg, IBinder token, ITransientNotification callback,
            int duration, int displayId) throws RemoteException;
    void enqueueTextToast(String pkg, IBinder token, CharSequence text,
            int duration, int displayId, ITransientNotificationCallback callback)
            throws RemoteException;
    void cancelToast(String pkg, IBinder token) throws RemoteException;

    abstract class Stub extends android.os.Binder implements INotificationManager {
        public static INotificationManager asInterface(IBinder obj) { return null; }
        public IBinder asBinder() { return this; }
    }
}
