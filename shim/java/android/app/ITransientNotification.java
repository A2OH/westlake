package android.app;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
/** AOSP compilation stub. */
public interface ITransientNotification extends IInterface {
    void show(IBinder windowToken) throws RemoteException;
    void hide() throws RemoteException;

    abstract class Stub extends android.os.Binder implements ITransientNotification {
        public static ITransientNotification asInterface(IBinder obj) { return null; }
        public IBinder asBinder() { return this; }
    }
}
