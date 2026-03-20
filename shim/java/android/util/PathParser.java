package android.util;

import android.graphics.Path;

/** Stub for AOSP compilation. Parses SVG path data strings. */
public class PathParser {

    public static Path createPathFromPathData(String pathData) {
        return new Path();
    }

    public static boolean canMorph(PathData startData, PathData endData) {
        return true;
    }

    public static boolean interpolatePathData(PathData outData, PathData startData,
            PathData endData, float fraction) {
        return true;
    }

    /** Represents parsed SVG path data. */
    public static class PathData {
        public PathData() {}
        public PathData(String pathString) {}
        public PathData(PathData data) {}
        public void setPathData(PathData source) {}
    }
}
