package android.content.res;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AssetManager {
    private File mAssetDir;
    private static File sGlobalAssetDir;  // shared across all AssetManager instances
    private String mApkPath;
    private static String sGlobalApkPath;

    /** Set the root directory where APK assets were extracted. */
    public void setAssetDir(String path) {
        mAssetDir = (path != null) ? new File(path) : null;
        if (mAssetDir != null) sGlobalAssetDir = mAssetDir;
        trace("setAssetDir path=" + path + " local=" + mAssetDir + " global=" + sGlobalAssetDir);
    }

    /** Set the source APK used when assets are not pre-extracted. */
    public void setApkPath(String path) {
        mApkPath = path;
        if (path != null && path.length() > 0) sGlobalApkPath = path;
        trace("setApkPath path=" + path + " local=" + mApkPath + " global=" + sGlobalApkPath);
    }

    public InputStream open(String fileName) throws IOException {
        String[] roots = assetRootPaths();
        for (int i = 0; i < roots.length; i++) {
            String root = roots[i];
            if (root != null && root.length() > 0) {
                String path = joinAssetPath(root, fileName);
                byte[] data = com.westlake.engine.WestlakeLauncher.readFileBytesForShim(path);
                if (data != null) {
                    trace("open(" + fileName + ") -> " + path + " bytes=" + data.length);
                    return new ByteArrayInputStream(data);
                }
            }
        }

        InputStream apkStream = openFromApk(fileName);
        if (apkStream != null) return apkStream;

        trace("open(" + fileName + ") NOT FOUND local=" + mAssetDir + " global=" + sGlobalAssetDir);
        throw new IOException("Asset not found: " + fileName);
    }

    public InputStream open(String fileName, int accessMode) throws IOException {
        return open(fileName);
    }

    public String[] list(String path) throws IOException {
        String[] indexedNames = listFromExtractedIndex(path);
        if (indexedNames != null) return indexedNames;

        String[] apkNames = listFromApk(path);
        if (apkNames != null) return apkNames;

        trace("list(" + path + ") NOT FOUND local=" + mAssetDir + " global=" + sGlobalAssetDir);
        return new String[0];
    }

    private String resolveApkPath() {
        if (mApkPath != null && mApkPath.length() > 0) return mApkPath;
        if (sGlobalApkPath != null && sGlobalApkPath.length() > 0) return sGlobalApkPath;
        try {
            String prop = System.getProperty("westlake.apk.path");
            if (prop != null && prop.length() > 0) return prop;
        } catch (Throwable ignored) {
        }
        return null;
    }

    private String[] assetRootPaths() {
        String local = pathOf(mAssetDir);
        String global = pathOf(sGlobalAssetDir);
        if (local != null && local.equals(global)) {
            return new String[] { local };
        }
        return new String[] { local, global };
    }

    private static String pathOf(File file) {
        if (file == null) return null;
        try {
            return file.getPath();
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static String joinAssetPath(String root, String fileName) {
        String clean = fileName == null ? "" : fileName;
        while (clean.startsWith("/")) clean = clean.substring(1);
        if (clean.startsWith("assets/")) clean = clean.substring("assets/".length());
        if (root.endsWith("/")) return root + clean;
        return root + "/" + clean;
    }

    private InputStream openFromApk(String fileName) throws IOException {
        String apkPath = resolveApkPath();
        if (apkPath == null || apkPath.length() == 0 || fileName == null) return null;
        String entryName = fileName.startsWith("assets/")
                ? fileName
                : "assets/" + fileName;
        ZipFile zip = null;
        try {
            zip = new ZipFile(apkPath);
            ZipEntry entry = zip.getEntry(entryName);
            if (entry == null || entry.isDirectory()) return null;
            InputStream in = zip.getInputStream(entry);
            ByteArrayOutputStream out = new ByteArrayOutputStream(
                    entry.getSize() > 0 && entry.getSize() < Integer.MAX_VALUE
                            ? (int) entry.getSize()
                            : 4096);
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
            in.close();
            byte[] data = out.toByteArray();
            trace("open(" + fileName + ") -> apk:" + entryName + " bytes=" + data.length);
            return new ByteArrayInputStream(data);
        } finally {
            if (zip != null) zip.close();
        }
    }

    private String[] listFromApk(String path) throws IOException {
        String apkPath = resolveApkPath();
        if (apkPath == null || apkPath.length() == 0) return null;
        String clean = path == null ? "" : path;
        while (clean.startsWith("/")) clean = clean.substring(1);
        if (clean.startsWith("assets/")) clean = clean.substring("assets/".length());
        String prefix = clean.length() == 0 ? "assets/" : "assets/" + clean + "/";
        ZipFile zip = null;
        try {
            zip = new ZipFile(apkPath);
            LinkedHashSet<String> names = new LinkedHashSet<String>();
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String name = entry.getName();
                if (!name.startsWith(prefix) || name.length() == prefix.length()) continue;
                String rest = name.substring(prefix.length());
                int slash = rest.indexOf('/');
                names.add(slash >= 0 ? rest.substring(0, slash) : rest);
            }
            if (names.isEmpty()) return null;
            String[] result = names.toArray(new String[names.size()]);
            trace("list(" + path + ") -> apk:" + prefix + " count=" + result.length
                    + " first=" + firstNames(result));
            return result;
        } finally {
            if (zip != null) zip.close();
        }
    }

    private String[] listFromExtractedIndex(String path) {
        String clean = path == null ? "" : path;
        while (clean.startsWith("/")) clean = clean.substring(1);
        if (clean.startsWith("assets/")) clean = clean.substring("assets/".length());
        String[] roots = assetRootPaths();
        for (int i = 0; i < roots.length; i++) {
            String root = roots[i];
            if (root == null || root.length() == 0) continue;
            byte[] indexBytes = com.westlake.engine.WestlakeLauncher.readFileBytesForShim(
                    joinAssetPath(root, ".westlake_asset_index"));
            if (indexBytes == null) continue;
            String index;
            try {
                index = new String(indexBytes, "UTF-8");
            } catch (Throwable t) {
                index = new String(indexBytes);
            }
            LinkedHashSet<String> names = new LinkedHashSet<String>();
            int start = 0;
            while (start <= index.length()) {
                int end = index.indexOf('\n', start);
                if (end < 0) end = index.length();
                String line = index.substring(start, end);
                if (line.endsWith("\r")) line = line.substring(0, line.length() - 1);
                addIndexedAssetName(names, clean, line);
                if (end == index.length()) break;
                start = end + 1;
            }
            String[] result = names.toArray(new String[names.size()]);
            trace("list(" + path + ") -> index:" + root + " count=" + result.length
                    + " first=" + firstNames(result));
            return result;
        }
        return null;
    }

    private static void addIndexedAssetName(LinkedHashSet<String> names, String cleanPath, String line) {
        if (line == null || line.length() == 0) return;
        while (line.startsWith("/")) line = line.substring(1);
        if (line.startsWith("assets/")) line = line.substring("assets/".length());
        String rest;
        if (cleanPath == null || cleanPath.length() == 0) {
            rest = line;
        } else {
            String prefix = cleanPath.endsWith("/") ? cleanPath : cleanPath + "/";
            if (!line.startsWith(prefix)) return;
            rest = line.substring(prefix.length());
        }
        if (rest.length() == 0) return;
        int slash = rest.indexOf('/');
        names.add(slash >= 0 ? rest.substring(0, slash) : rest);
    }

    private static String firstNames(String[] names) {
        if (names == null || names.length == 0) return "";
        StringBuilder sb = new StringBuilder();
        int count = Math.min(names.length, 6);
        for (int i = 0; i < count; i++) {
            if (i > 0) sb.append(',');
            sb.append(names[i]);
        }
        return sb.toString();
    }

    private static void trace(String message) {
        try {
            com.westlake.engine.WestlakeLauncher.strictTrace("[AssetManager] " + message);
        } catch (Throwable ignored) {
            try {
                System.err.println("[AssetManager] " + message);
            } catch (Throwable ignoredToo) {
            }
        }
    }

    public void close() {
        // no-op — assets stay extracted for app lifetime
    }

    public android.util.SparseArray<String> getAssignedPackageIdentifiers() {
        return new android.util.SparseArray<String>();
    }
}
