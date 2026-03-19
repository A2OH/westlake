package android.graphics;

/**
 * Stub for android.graphics.TableMaskFilter.
 * Used by StackView for visual effects.
 */
public class TableMaskFilter extends MaskFilter {
    public TableMaskFilter(byte[] table) {}

    public static TableMaskFilter CreateClipTable(int min, int max) {
        return new TableMaskFilter(new byte[0]);
    }

    public static TableMaskFilter CreateGammaTable(float gamma) {
        return new TableMaskFilter(new byte[0]);
    }
}
