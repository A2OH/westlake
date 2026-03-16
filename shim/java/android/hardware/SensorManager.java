package android.hardware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Android-compatible SensorManager stub.
 * Maps to @ohos.sensor on OpenHarmony.
 *
 * This implementation stores listener registrations in-memory.
 * On-device bridge calls (actual @ohos.sensor subscription) would be
 * wired through OHBridge when running on OpenHarmony hardware.
 */
public class SensorManager {

    // ── Sampling-period delay constants ──────────────────────────────────────

    /** Rate suitable for games. */
    public static final int SENSOR_DELAY_FASTEST = 0;
    /** Rate suitable for games. */
    public static final int SENSOR_DELAY_GAME    = 1;
    /** Rate suitable for the user interface. */
    public static final int SENSOR_DELAY_UI      = 2;
    /** Rate (default) suitable for screen orientation changes. */
    public static final int SENSOR_DELAY_NORMAL  = 3;

    // ── Accuracy constants ────────────────────────────────────────────────────

    public static final int SENSOR_STATUS_ACCURACY_LOW    = 1;
    public static final int SENSOR_STATUS_ACCURACY_MEDIUM = 2;
    public static final int SENSOR_STATUS_ACCURACY_HIGH   = 3;
    public static final int SENSOR_STATUS_UNRELIABLE      = 0;

    // ── Gravity / physics constants ───────────────────────────────────────────

    /** Standard acceleration of gravity on Earth (m/s^2). */
    public static final float GRAVITY_EARTH = 9.80665f;

    // ── Internal listener registry ────────────────────────────────────────────

    // Maps listener -> list of sensors it is registered for
    private final Map<SensorEventListener, List<Sensor>> mListeners = new HashMap<>();

    // ── Sensor catalogue (populated lazily) ──────────────────────────────────

    private static final Sensor ACCEL    = new Sensor("Accelerometer",   Sensor.TYPE_ACCELEROMETER);
    private static final Sensor MAG      = new Sensor("Magnetometer",    Sensor.TYPE_MAGNETIC_FIELD);
    private static final Sensor GYRO     = new Sensor("Gyroscope",       Sensor.TYPE_GYROSCOPE);
    private static final Sensor LIGHT    = new Sensor("Light",           Sensor.TYPE_LIGHT);
    private static final Sensor PROXIMITY= new Sensor("Proximity",       Sensor.TYPE_PROXIMITY);
    private static final Sensor PRESSURE = new Sensor("Pressure",        Sensor.TYPE_PRESSURE);
    private static final Sensor GRAVITY  = new Sensor("Gravity",         Sensor.TYPE_GRAVITY);
    private static final Sensor LINEAR   = new Sensor("Linear Acceleration", Sensor.TYPE_LINEAR_ACCELERATION);
    private static final Sensor ROTATION = new Sensor("Rotation Vector", Sensor.TYPE_ROTATION_VECTOR);

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Returns the default sensor for the given type, or {@code null} if
     * no such sensor is available on this device.
     */
    public Sensor getDefaultSensor(int type) {
        switch (type) {
            case Sensor.TYPE_ACCELEROMETER:       return ACCEL;
            case Sensor.TYPE_MAGNETIC_FIELD:       return MAG;
            case Sensor.TYPE_GYROSCOPE:            return GYRO;
            case Sensor.TYPE_LIGHT:                return LIGHT;
            case Sensor.TYPE_PROXIMITY:            return PROXIMITY;
            case Sensor.TYPE_PRESSURE:             return PRESSURE;
            case Sensor.TYPE_GRAVITY:              return GRAVITY;
            case Sensor.TYPE_LINEAR_ACCELERATION:  return LINEAR;
            case Sensor.TYPE_ROTATION_VECTOR:      return ROTATION;
            default:                               return null;
        }
    }

    /**
     * Returns all sensors of the given type, or all sensors if
     * {@code type == Sensor.TYPE_ALL}.
     */
    public List<Sensor> getSensorList(int type) {
        List<Sensor> list = new ArrayList<>();
        Sensor[] all = {ACCEL, MAG, GYRO, LIGHT, PROXIMITY, PRESSURE, GRAVITY, LINEAR, ROTATION};
        if (type == Sensor.TYPE_ALL) {
            for (Sensor s : all) list.add(s);
        } else {
            for (Sensor s : all) {
                if (s.getType() == type) list.add(s);
            }
        }
        return list;
    }

    /**
     * Registers a {@link SensorEventListener} for the given sensor at the
     * requested sampling rate.
     *
     * @param listener          the listener to receive events
     * @param sensor            the sensor to monitor
     * @param samplingPeriodUs  one of the {@code SENSOR_DELAY_*} constants or
     *                          a rate in microseconds
     * @return {@code true} if the sensor was successfully enabled
     */
    public boolean registerListener(SensorEventListener listener,
                                    Sensor sensor,
                                    int samplingPeriodUs) {
        if (listener == null || sensor == null) return false;

        List<Sensor> sensors = mListeners.get(listener);
        if (sensors == null) {
            sensors = new ArrayList<>();
            mListeners.put(listener, sensors);
        }
        if (!sensors.contains(sensor)) {
            sensors.add(sensor);
        }
        // On-device: OHBridge.sensorSubscribe(sensor.getType(), samplingPeriodUs, listener)
        return true;
    }

    /**
     * Unregisters a listener from all sensors it was registered for.
     */
    public void unregisterListener(SensorEventListener listener) {
        if (listener == null) return;
        mListeners.remove(listener);
        // On-device: OHBridge.sensorUnsubscribeAll(listener)
    }

    /**
     * Unregisters a listener from a specific sensor only.
     */
    public void unregisterListener(SensorEventListener listener, Sensor sensor) {
        if (listener == null || sensor == null) return;
        List<Sensor> sensors = mListeners.get(listener);
        if (sensors != null) {
            sensors.remove(sensor);
            if (sensors.isEmpty()) {
                mListeners.remove(listener);
            }
        }
        // On-device: OHBridge.sensorUnsubscribe(sensor.getType(), listener)
    }

    // ── Static math helpers ─────────────────────────────────────────────────

    /**
     * Computes the rotation matrix {@code R} from the gravity and geomagnetic
     * field vectors. This is a simplified implementation matching AOSP behavior.
     *
     * @param R           output rotation matrix (9 or 16 elements), or null
     * @param I           output inclination matrix (9 or 16 elements), or null
     * @param gravity     accelerometer values (3 elements)
     * @param geomagnetic magnetometer values (3 elements)
     * @return {@code true} if the matrices were computed successfully
     */
    public static boolean getRotationMatrix(float[] R, float[] I,
                                            float[] gravity, float[] geomagnetic) {
        if (gravity == null || gravity.length < 3) return false;
        if (geomagnetic == null || geomagnetic.length < 3) return false;

        float Ax = gravity[0];
        float Ay = gravity[1];
        float Az = gravity[2];

        // Cross product: H = geomagnetic x gravity (East vector)
        float Ex = geomagnetic[0];
        float Ey = geomagnetic[1];
        float Ez = geomagnetic[2];

        float Hx = Ey * Az - Ez * Ay;
        float Hy = Ez * Ax - Ex * Az;
        float Hz = Ex * Ay - Ey * Ax;

        float normH = (float) Math.sqrt(Hx * Hx + Hy * Hy + Hz * Hz);
        if (normH < 0.1f) {
            // device is close to free fall or magnetic field is unreliable
            return false;
        }
        float invH = 1.0f / normH;
        Hx *= invH;
        Hy *= invH;
        Hz *= invH;

        // Normalize gravity (Down vector)
        float normA = (float) Math.sqrt(Ax * Ax + Ay * Ay + Az * Az);
        if (normA < 0.1f) return false;
        float invA = 1.0f / normA;
        Ax *= invA;
        Ay *= invA;
        Az *= invA;

        // Cross product: M = gravity x H (North vector)
        float Mx = Ay * Hz - Az * Hy;
        float My = Az * Hx - Ax * Hz;
        float Mz = Ax * Hy - Ay * Hx;

        // Fill rotation matrix R
        if (R != null) {
            if (R.length == 9) {
                R[0] = Hx; R[1] = Hy; R[2] = Hz;
                R[3] = Mx; R[4] = My; R[5] = Mz;
                R[6] = Ax; R[7] = Ay; R[8] = Az;
            } else if (R.length >= 16) {
                R[0]  = Hx; R[1]  = Hy; R[2]  = Hz; R[3]  = 0;
                R[4]  = Mx; R[5]  = My; R[6]  = Mz; R[7]  = 0;
                R[8]  = Ax; R[9]  = Ay; R[10] = Az; R[11] = 0;
                R[12] = 0;  R[13] = 0;  R[14] = 0;  R[15] = 1;
            }
        }

        // Fill inclination matrix I
        if (I != null) {
            // Compute inclination angle
            float normE = (float) Math.sqrt(Ex * Ex + Ey * Ey + Ez * Ez);
            if (normE < 0.1f) return false;
            // Simplified: set I to identity with inclination
            // For a basic implementation, the inclination matrix is identity
            if (I.length == 9) {
                I[0] = 1; I[1] = 0; I[2] = 0;
                I[3] = 0; I[4] = 1; I[5] = 0;
                I[6] = 0; I[7] = 0; I[8] = 1;
            } else if (I.length >= 16) {
                for (int i = 0; i < 16; i++) I[i] = 0;
                I[0] = 1; I[5] = 1; I[10] = 1; I[15] = 1;
            }
        }

        return true;
    }

    /**
     * Computes the device orientation (azimuth, pitch, roll) from a rotation
     * matrix obtained from {@link #getRotationMatrix}.
     *
     * @param R       rotation matrix (9 or 16 elements)
     * @param values  output array of at least 3 elements:
     *                values[0] = azimuth (rotation around -Z axis, [-PI..PI])
     *                values[1] = pitch   (rotation around X axis, [-PI/2..PI/2])
     *                values[2] = roll    (rotation around Y axis, [-PI..PI])
     * @return the values array
     */
    public static float[] getOrientation(float[] R, float[] values) {
        if (R == null || values == null || values.length < 3) return values;

        // Works for both 9-element and 16-element (4x4) matrices.
        // For 9-element: row stride = 3
        // For 16-element: row stride = 4
        int stride = (R.length == 16) ? 4 : 3;

        // azimuth = atan2(R[1], R[4]) for 3x3 or atan2(R[1], R[5]) for 4x4
        values[0] = (float) Math.atan2(R[1], R[stride + 1]);
        // pitch = asin(-R[7]) for 3x3 or asin(-R[9]) for 4x4
        values[1] = (float) Math.asin(-R[2 * stride + 1]);
        // roll = atan2(-R[6], R[8]) for 3x3 or atan2(-R[8], R[10]) for 4x4
        values[2] = (float) Math.atan2(-R[2 * stride], R[2 * stride + 2]);

        return values;
    }

    // ── Test-support helpers (not in Android API) ─────────────────────────────

    /** Returns {@code true} if the given listener is currently registered. */
    public boolean hasListener(SensorEventListener listener) {
        return mListeners.containsKey(listener);
    }

    /** Returns the total number of registered listeners. */
    public int getListenerCount() {
        return mListeners.size();
    }

    /** Returns the sensors registered for the given listener, or an empty list. */
    public List<Sensor> getSensorsForListener(SensorEventListener listener) {
        List<Sensor> result = mListeners.get(listener);
        return result != null ? result : new ArrayList<Sensor>();
    }
}
