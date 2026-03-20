package android.hardware.input;

/** Stub for AOSP compilation. Identifies an input device. */
public class InputDeviceIdentifier {
    private final String mDescriptor;
    private final int mVendorId;
    private final int mProductId;

    public InputDeviceIdentifier(String descriptor, int vendorId, int productId) {
        mDescriptor = descriptor;
        mVendorId = vendorId;
        mProductId = productId;
    }

    public String getDescriptor() { return mDescriptor; }
    public int getVendorId() { return mVendorId; }
    public int getProductId() { return mProductId; }
}
