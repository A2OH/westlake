package android.content.res;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AssetManager {
    private File mAssetDir;
    private static File sGlobalAssetDir;  // shared across all AssetManager instances

    /** Set the root directory where APK assets were extracted. */
    public void setAssetDir(String path) {
        mAssetDir = (path != null) ? new File(path) : null;
        if (mAssetDir != null) sGlobalAssetDir = mAssetDir;
    }

    public InputStream open(String fileName) throws IOException {
        // Try instance dir first, then global dir
        File[] dirs = { mAssetDir, sGlobalAssetDir };
        for (File dir : dirs) {
            if (dir != null) {
                File f = new File(dir, fileName);
                if (f.exists() && f.isFile()) {
                    System.err.println("[AssetManager] open(" + fileName + ") -> " + f.getAbsolutePath() + " (" + f.length() + " bytes)");
                    return new FileInputStream(f);
                }
            }
        }
        System.err.println("[AssetManager] open(" + fileName + ") NOT FOUND (local=" + mAssetDir + " global=" + sGlobalAssetDir + ")");
        throw new IOException("Asset not found: " + fileName);
    }

    public InputStream open(String fileName, int accessMode) throws IOException {
        return open(fileName);
    }

    public String[] list(String path) throws IOException {
        if (mAssetDir != null) {
            File dir = path.isEmpty() ? mAssetDir : new File(mAssetDir, path);
            if (dir.exists() && dir.isDirectory()) {
                String[] names = dir.list();
                return names != null ? names : new String[0];
            }
        }
        return new String[0];
    }

    public void close() {
        // no-op — assets stay extracted for app lifetime
    }

    public android.util.SparseArray<String> getAssignedPackageIdentifiers() {
        return new android.util.SparseArray<String>();
    }
}
