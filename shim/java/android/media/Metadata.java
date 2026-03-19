package android.media;

import java.util.Set;
import java.util.HashSet;

/**
 * Stub for android.media.Metadata used by VideoView.
 */
public class Metadata {
    public static final int PAUSE_AVAILABLE = 1;
    public static final int SEEK_BACKWARD_AVAILABLE = 2;
    public static final int SEEK_FORWARD_AVAILABLE = 3;
    public static final int SEEK_AVAILABLE = 4;
    public static final int ANY = 0;
    public static final int ALL = 0;

    public static final int METADATA_ALL = 0;
    public static final int BYPASS_METADATA_FILTER = 1;

    public Metadata() {}

    public boolean has(int metadataId) { return false; }
    public boolean getBoolean(int metadataId) { return false; }
    public Set<Integer> keySet() { return new HashSet<Integer>(); }
}
