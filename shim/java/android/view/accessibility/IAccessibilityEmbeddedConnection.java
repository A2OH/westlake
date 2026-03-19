package android.view.accessibility;

import android.os.IBinder;

/**
 * Stub for IAccessibilityEmbeddedConnection used by SurfaceView.
 */
public interface IAccessibilityEmbeddedConnection {
    IBinder associateEmbeddedHierarchy(IBinder host, int hostViewId) throws android.os.RemoteException;
    void disassociateEmbeddedHierarchy() throws android.os.RemoteException;
    void setScreenMatrix(float[] matrixValues) throws android.os.RemoteException;
    IBinder asBinder();
}
