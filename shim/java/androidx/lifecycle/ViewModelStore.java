package androidx.lifecycle;

import java.util.HashMap;
import java.util.Map;

public class ViewModelStore {
    private final Map<String, ViewModel> mMap = new HashMap<>();
    final void put(String key, ViewModel vm) { mMap.put(key, vm); }
    final ViewModel get(String key) { return mMap.get(key); }
    public final void clear() {
        for (ViewModel vm : mMap.values()) vm.clear();
        mMap.clear();
    }
}
