package android.content.res;

import android.content.pm.ActivityInfo.Config;

/** Stub for AOSP compilation. */
public abstract class ConstantState<T> {
    public abstract @Config int getChangingConfigurations();
    public abstract T newInstance();
    public T newInstance(Resources res) { return newInstance(); }
    public T newInstance(Resources res, Resources.Theme theme) { return newInstance(res); }
}
