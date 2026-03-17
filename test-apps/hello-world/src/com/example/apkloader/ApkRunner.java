package com.example.apkloader;

import android.app.Activity;
import android.app.MiniServer;
import android.app.MiniActivityManager;
import android.content.Intent;
import java.io.*;


/**
 * APK Runner: extracts DEX from APK, parses manifest, launches Activity.
 *
 * Usage: dalvikvm -cp apkrunner.dex com.example.apkloader.ApkRunner hello.apk
 *
 * Or from MiniServer: ApkRunner.loadAndLaunch("hello.apk")
 */
public class ApkRunner {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: ApkRunner <apk-path>");
            System.exit(1);
        }
        String apkPath = args[0];
        System.out.println("=== APK Runner ===");
        System.out.println("Loading: " + apkPath);

        try {
            loadAndLaunch(apkPath);
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
            e.printStackTrace();
        }
    }

    public static void loadAndLaunch(String apkPath) throws Exception {
        // Step 1: Read manifest from APK
        byte[] manifestBytes = readFromZip(apkPath, "AndroidManifest.xml");
        if (manifestBytes == null) {
            throw new IOException("No AndroidManifest.xml in APK");
        }

        System.out.println("Manifest: " + manifestBytes.length + " bytes");

        // Step 2: Parse manifest
        BinaryXmlParser parser = new BinaryXmlParser();
        BinaryXmlParser.ParseResult manifest = parser.parse(manifestBytes);
        System.out.println("Package: " + manifest.packageName);
        System.out.println("Launcher: " + manifest.launcherActivity);
        System.out.println("Activities: " + manifest.activities);

        if (manifest.packageName == null || manifest.launcherActivity == null) {
            throw new Exception("Manifest missing package or launcher activity");
        }

        // Step 2b: Load resources.arsc string table
        try {
            byte[] resData = readFromZip(apkPath, "resources.arsc");
            if (resData != null) {
                loadResources(resData);
                System.out.println("Resources: loaded " + resData.length + " bytes");
            }
        } catch (Exception e) {
            System.out.println("Resources: skipped (" + e.getMessage() + ")");
        }

        // Step 3: Load APK's classes via DexClassLoader
        ClassLoader appClassLoader = null;
        try {
            String cacheDir = System.getenv("ANDROID_DATA");
            if (cacheDir == null) cacheDir = "/tmp";
            cacheDir += "/dex-cache";
            new java.io.File(cacheDir).mkdirs();
            Class<?> dclClass = Class.forName("dalvik.system.DexClassLoader");
            appClassLoader = (ClassLoader) dclClass.getConstructor(
                String.class, String.class, String.class, ClassLoader.class)
                .newInstance(apkPath, cacheDir, null, ApkRunner.class.getClassLoader());
            System.out.println("DexClassLoader: loaded " + apkPath);
        } catch (Exception e) {
            System.out.println("DexClassLoader failed: " + e + " (using bootclasspath)");
        }

        // Step 4: Initialize MiniServer with app classloader
        MiniServer.init(manifest.packageName);
        MiniActivityManager am = MiniServer.get().getActivityManager();

        // Step 5: Launch the launcher Activity
        Intent intent = new Intent();
        intent.setClassName(manifest.packageName, manifest.launcherActivity);
        System.out.println("Starting: " + manifest.launcherActivity);

        // If we have DexClassLoader, pre-load the Activity class
        if (appClassLoader != null) {
            try {
                Class<?> actClass = appClassLoader.loadClass(manifest.launcherActivity);
                System.out.println("Loaded class: " + actClass.getName());
                // Register with MiniServer so it can instantiate
                am.registerActivityClass(manifest.launcherActivity, actClass);
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found in APK: " + manifest.launcherActivity);
            }
        }

        am.startActivity(null, intent, 0);

        // Step 6: Verify
        Activity activity = am.getResumedActivity();
        if (activity != null) {
            System.out.println("Activity running: " + activity.getClass().getName());
            System.out.println("=== APK Launch COMPLETE ===");
        } else {
            System.out.println("ERROR: Activity failed to start");
        }
    }

    /** Read a file from a ZIP/APK — manual ZIP parsing, no native Inflater */
    private static byte[] readFromZip(String zipPath, String entryName) throws IOException {
        FileInputStream fis = new FileInputStream(zipPath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] tmp = new byte[8192];
        int nr;
        while ((nr = fis.read(tmp)) > 0) bos.write(tmp, 0, nr);
        fis.close();
        byte[] apk = bos.toByteArray();

        // Find EOCD
        int eocd = -1;
        for (int i = apk.length - 22; i >= 0; i--) {
            if (apk[i]==0x50 && apk[i+1]==0x4B && apk[i+2]==0x05 && apk[i+3]==0x06) {
                eocd = i; break;
            }
        }
        if (eocd < 0) throw new IOException("Not a ZIP");

        int cdOfs = ri(apk, eocd+16);
        int cdN = rs(apk, eocd+10);
        int pos = cdOfs;

        for (int e = 0; e < cdN && pos+46 <= apk.length; e++) {
            int nLen = rs(apk, pos+28);
            int xLen = rs(apk, pos+30);
            int cLen = rs(apk, pos+32);
            int comp = rs(apk, pos+10);
            int cSz = ri(apk, pos+20);
            int uSz = ri(apk, pos+24);
            int lOfs = ri(apk, pos+42);

            String name;
            try { name = new String(apk, pos+46, nLen, "UTF-8"); }
            catch (Exception ex) { name = ""; }

            if (name.equals(entryName)) {
                int lnLen = rs(apk, lOfs+26);
                int lxLen = rs(apk, lOfs+28);
                int dStart = lOfs + 30 + lnLen + lxLen;
                System.out.println("ZIP: found '" + name + "' comp=" + comp + " cSz=" + cSz + " uSz=" + uSz + " dStart=" + dStart + " apkLen=" + apk.length);
                if (comp == 0) {
                    /* STORED — direct copy */
                    byte[] r = new byte[uSz];
                    System.arraycopy(apk, dStart, r, 0, uSz);
                    return r;
                } else {
                    /* DEFLATED — decompress via reflection to avoid dexopt crash */
                    byte[] compressed = new byte[cSz];
                    System.arraycopy(apk, dStart, compressed, 0, cSz);
                    try {
                        Class<?> infClass = Class.forName("java.util.zip.Inflater");
                        Object inf = infClass.getConstructor(boolean.class).newInstance(true);
                        infClass.getMethod("setInput", byte[].class, int.class, int.class)
                            .invoke(inf, compressed, 0, cSz);
                        byte[] result = new byte[uSz];
                        infClass.getMethod("inflate", byte[].class).invoke(inf, result);
                        infClass.getMethod("end").invoke(inf);
                        return result;
                    } catch (Exception ex) {
                        System.out.println("WARNING: Cannot decompress entry '" + entryName + "': " + ex);
                        return compressed; /* return raw compressed data */
                    }
                }
            }
            pos += 46 + nLen + xLen + cLen;
        }
        return null;
    }
    /** Minimal resources.arsc parser — extracts string resources by ID.
     * Registers them with android.content.res.Resources so getString(id) works. */
    private static void loadResources(byte[] data) {
        // resources.arsc format:
        // Header: type(0x0002), headerSize, size
        // Then chunks: string pool (0x0001), package(0x0200), type(0x0201/0x0202)
        int pos = 0;
        if (data.length < 12) return;
        int fileType = rs(data, 0);
        if (fileType != 0x0002) return; // Not a resource table

        pos = rs(data, 2); // skip header

        // Find the global string pool (first chunk, type 0x0001)
        if (pos + 8 > data.length) return;
        int spType = rs(data, pos);
        int spSize = ri(data, pos + 4);
        if (spType != 0x0001) return; // First chunk should be string pool

        int strCount = ri(data, pos + 8);
        int flags = ri(data, pos + 16);
        boolean isUtf8 = (flags & (1 << 8)) != 0;
        int stringsStart = ri(data, pos + 20);

        String[] strings = new String[strCount];
        int ofsBase = pos + 28; // offset array starts here

        for (int i = 0; i < strCount && i < 10000; i++) {
            if (ofsBase + i * 4 + 4 > data.length) break;
            int sOfs = ri(data, ofsBase + i * 4);
            int absOfs = pos + stringsStart + sOfs;
            if (absOfs >= data.length) { strings[i] = ""; continue; }

            if (isUtf8) {
                int charLen = data[absOfs] & 0xFF;
                int skip = 1;
                if ((charLen & 0x80) != 0) { charLen = ((charLen & 0x7F) << 8) | (data[absOfs+1] & 0xFF); skip = 2; }
                int byteLen = data[absOfs + skip] & 0xFF;
                int bskip = 1;
                if ((byteLen & 0x80) != 0) { byteLen = ((byteLen & 0x7F) << 8) | (data[absOfs+skip+1] & 0xFF); bskip = 2; }
                int strStart = absOfs + skip + bskip;
                if (strStart + byteLen <= data.length) {
                    try { strings[i] = new String(data, strStart, byteLen, "UTF-8"); }
                    catch (Exception e) { strings[i] = ""; }
                } else strings[i] = "";
            } else {
                int charLen = rs(data, absOfs);
                absOfs += 2;
                char[] chars = new char[charLen];
                for (int c = 0; c < charLen && absOfs + c*2 + 1 < data.length; c++)
                    chars[c] = (char) rs(data, absOfs + c * 2);
                strings[i] = new String(chars);
            }
        }

        // Register strings with the Resources system
        // Resource IDs for strings are typically 0x7F0X00YY where YY is the index
        // We register all strings so getString(anyId) can find them
        try {
            Class<?> resClass = Class.forName("android.content.res.Resources");
            java.lang.reflect.Method regMethod = null;
            try {
                regMethod = resClass.getMethod("registerString", int.class, String.class);
            } catch (NoSuchMethodException e) { /* not available */ }

            if (regMethod != null) {
                Object res = resClass.getMethod("getSystem").invoke(null);
                for (int i = 0; i < strings.length; i++) {
                    if (strings[i] != null && strings[i].length() > 0) {
                        // Register with a synthetic ID: 0x7F040000 + i (string resource base)
                        regMethod.invoke(res, 0x7F040000 + i, strings[i]);
                    }
                }
                System.out.println("Registered " + strings.length + " resource strings");
            } else {
                System.out.println("Resources.registerString not available — strings not registered");
            }
        } catch (Exception e) {
            System.out.println("Resource registration failed: " + e.getMessage());
        }
    }

    private static int ri(byte[] d, int o) {
        return (d[o]&0xFF)|((d[o+1]&0xFF)<<8)|((d[o+2]&0xFF)<<16)|((d[o+3]&0xFF)<<24);
    }
    private static int rs(byte[] d, int o) {
        return (d[o]&0xFF)|((d[o+1]&0xFF)<<8);
    }
}
