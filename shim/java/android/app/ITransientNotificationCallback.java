package android.app;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
/** AOSP compilation stub. */
public interface ITransientNotificationCallback extends IInterface {
    void onToastShown() throws RemoteException;
    void onToastHidden() throws RemoteException;

    abstract class Stub extends android.os.Binder implements ITransientNotificationCallback {
        public static ITransientNotificationCallback asInterface(IBinder obj) { return null; }
        public IBinder asBinder() { return this; }
        public void onToastShown() throws RemoteException {}
        public void onToastHidden() throws RemoteException {}
    }
}
