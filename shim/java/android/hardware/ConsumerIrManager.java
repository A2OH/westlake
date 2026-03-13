package android.hardware;

/**
 * Android-compatible ConsumerIrManager shim. Stub for IR blaster.
 */
public class ConsumerIrManager {

    public static class CarrierFrequencyRange {
        private final int mMinFrequency;
        private final int mMaxFrequency;

        public CarrierFrequencyRange(int min, int max) {
            mMinFrequency = min;
            mMaxFrequency = max;
        }

        public int getMinFrequency() { return mMinFrequency; }
        public int getMaxFrequency() { return mMaxFrequency; }
    }

    public boolean hasIrEmitter() { return false; }

    public void transmit(int carrierFrequency, int[] pattern) {
        System.out.println("[IR] transmit freq=" + carrierFrequency + " pattern length=" + pattern.length);
    }

    public CarrierFrequencyRange[] getCarrierFrequencies() {
        return new CarrierFrequencyRange[0];
    }
}
