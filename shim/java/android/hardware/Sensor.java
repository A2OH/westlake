package android.hardware;

/**
 * Android-compatible Sensor stub.
 * Represents a hardware sensor with type, name, vendor, and capability metadata.
 */
public class Sensor {
    public static final int TYPE_ALL                 = -1;
    public static final int TYPE_ACCELEROMETER       = 1;
    public static final int TYPE_MAGNETIC_FIELD      = 2;
    public static final int TYPE_ORIENTATION         = 3;
    public static final int TYPE_GYROSCOPE           = 4;
    public static final int TYPE_LIGHT               = 5;
    public static final int TYPE_PRESSURE            = 6;
    public static final int TYPE_TEMPERATURE         = 7;
    public static final int TYPE_PROXIMITY           = 8;
    public static final int TYPE_GRAVITY             = 9;
    public static final int TYPE_LINEAR_ACCELERATION = 10;
    public static final int TYPE_ROTATION_VECTOR     = 11;
    public static final int TYPE_STEP_DETECTOR       = 18;
    public static final int TYPE_STEP_COUNTER        = 19;

    private final String mName;
    private final int mType;
    private String mVendor = "Android-OH-Shim";
    private int mVersion = 1;
    private float mMaxRange = 0f;
    private float mResolution = 0f;
    private float mPower = 0f;
    private int mMinDelay = 0;

    /** Public constructor for internal / shim use. */
    public Sensor(String name, int type) {
        mName = name;
        mType = type;
    }

    /** Constructor with full metadata for internal use. */
    public Sensor(int type, String name) {
        mType = type;
        mName = name;
    }

    public String getName() { return mName; }
    public int getType() { return mType; }
    public String getVendor() { return mVendor; }
    public int getVersion() { return mVersion; }
    public float getMaximumRange() { return mMaxRange; }
    public float getResolution() { return mResolution; }
    public float getPower() { return mPower; }
    public int getMinDelay() { return mMinDelay; }

    /** Set vendor (internal use). */
    public void setVendor(String vendor) { mVendor = vendor; }
    /** Set version (internal use). */
    public void setVersion(int version) { mVersion = version; }
    /** Set maximum range (internal use). */
    public void setMaximumRange(float maxRange) { mMaxRange = maxRange; }
    /** Set resolution (internal use). */
    public void setResolution(float resolution) { mResolution = resolution; }
    /** Set power draw in mA (internal use). */
    public void setPower(float power) { mPower = power; }
    /** Set minimum delay in microseconds (internal use). */
    public void setMinDelay(int minDelay) { mMinDelay = minDelay; }

    @Override
    public String toString() {
        return "Sensor{name=" + mName + ", type=" + mType
                + ", vendor=" + mVendor + ", version=" + mVersion + "}";
    }
}
