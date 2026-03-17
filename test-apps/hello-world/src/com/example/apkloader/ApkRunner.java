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

        // Step 3: Initialize MiniServer
        MiniServer.init(manifest.packageName);
        MiniActivityManager am = MiniServer.get().getActivityManager();

        // Step 4: Launch the launcher Activity
        Intent intent = new Intent();
        intent.setClassName(manifest.packageName, manifest.launcherActivity);
        System.out.println("Starting: " + manifest.launcherActivity);
        am.startActivity(null, intent, 0);

        // Step 5: Verify
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
                if (comp == 0) {
                    /* STORED — direct copy */
                    byte[] r = new byte[uSz];
                    System.arraycopy(apk, dStart, r, 0, uSz);
                    return r;
                } else {
                    /* DEFLATED — decompress using Inflater */
                    byte[] compressed = new byte[cSz];
                    System.arraycopy(apk, dStart, compressed, 0, cSz);
                    java.util.zip.Inflater inf = new java.util.zip.Inflater(true);
                    inf.setInput(compressed, 0, cSz);
                    byte[] result = new byte[uSz];
                    try {
                        inf.inflate(result);
                    } catch (java.util.zip.DataFormatException dfe) {
                        /* ignore — return partial data */
                    }
                    inf.end();
                    return result;
                }
            }
            pos += 46 + nLen + xLen + cLen;
        }
        return null;
    }
    private static int ri(byte[] d, int o) {
        return (d[o]&0xFF)|((d[o+1]&0xFF)<<8)|((d[o+2]&0xFF)<<16)|((d[o+3]&0xFF)<<24);
    }
    private static int rs(byte[] d, int o) {
        return (d[o]&0xFF)|((d[o+1]&0xFF)<<8);
    }
}
