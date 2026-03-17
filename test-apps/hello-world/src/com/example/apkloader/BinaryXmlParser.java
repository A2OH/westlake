package com.example.apkloader;

import java.io.*;
import java.util.*;

/**
 * Minimal binary Android XML (AXML) parser.
 * Parses AndroidManifest.xml from APK files to extract:
 * - package name
 * - launcher Activity class
 * - min/target SDK version
 *
 * AXML format:
 *   Header: magic (0x00080003), file_size
 *   String Pool: all strings referenced by index
 *   Resource ID table (optional)
 *   XML nodes: START_NAMESPACE, START_TAG, END_TAG, TEXT, END_NAMESPACE
 */
public class BinaryXmlParser {

    // Chunk types
    private static final int CHUNK_AXML = 0x00080003;
    private static final int CHUNK_STRING_POOL = 0x001C0001;
    private static final int CHUNK_RESOURCE_IDS = 0x00080180;
    private static final int CHUNK_START_NAMESPACE = 0x00100100;
    private static final int CHUNK_END_NAMESPACE = 0x00100101;
    private static final int CHUNK_START_TAG = 0x00100102;
    private static final int CHUNK_END_TAG = 0x00100103;
    private static final int CHUNK_TEXT = 0x00100104;

    // Attribute value types
    private static final int TYPE_STRING = 0x03;
    private static final int TYPE_INT = 0x10;
    private static final int TYPE_BOOL = 0x12;

    private String[] stringPool;
    private String packageName;
    private String launcherActivity;
    private int minSdkVersion = -1;
    private int targetSdkVersion = -1;
    private List<String> activities = new ArrayList<String>();
    private List<String> permissions = new ArrayList<String>();

    public static class ParseResult {
        public String packageName;
        public String launcherActivity;
        public int minSdkVersion;
        public int targetSdkVersion;
        public List<String> activities;
        public List<String> permissions;

        public String toString() {
            return "package=" + packageName +
                   " launcher=" + launcherActivity +
                   " minSdk=" + minSdkVersion +
                   " targetSdk=" + targetSdkVersion +
                   " activities=" + activities;
        }
    }

    public ParseResult parse(byte[] data) {
        int pos = 0;

        // AXML header
        int magic = readInt(data, pos); pos += 4;
        int fileSize = readInt(data, pos); pos += 4;

        if (magic != CHUNK_AXML) {
            // Not binary XML — might be text XML, try simple text parse
            return parseTextXml(new String(data));
        }

        // Parse chunks
        while (pos < data.length - 8) {
            int chunkType = readInt(data, pos);
            int chunkSize = readInt(data, pos + 4);
            if (chunkSize <= 0) break;

            switch (chunkType) {
                case CHUNK_STRING_POOL:
                    parseStringPool(data, pos);
                    break;
                case CHUNK_START_TAG:
                    parseStartTag(data, pos);
                    break;
                // Other chunks: skip
            }
            pos += chunkSize;
        }

        ParseResult result = new ParseResult();
        result.packageName = packageName;
        result.launcherActivity = launcherActivity;
        result.minSdkVersion = minSdkVersion;
        result.targetSdkVersion = targetSdkVersion;
        result.activities = activities;
        result.permissions = permissions;
        return result;
    }

    private void parseStringPool(byte[] data, int offset) {
        int stringCount = readInt(data, offset + 8);
        int stringsStart = readInt(data, offset + 20); // offset from pool header to string data
        int poolOffset = offset + 28; // string offsets start here

        // Read string offsets
        int[] offsets = new int[stringCount];
        int ofsPos = offset + 28;
        for (int i = 0; i < stringCount; i++) {
            offsets[i] = readInt(data, ofsPos);
            ofsPos += 4;
        }

        // Read strings (UTF-16LE or UTF-8)
        int flags = readInt(data, offset + 16);
        boolean isUtf8 = (flags & (1 << 8)) != 0;

        stringPool = new String[stringCount];
        int absStringsStart = offset + stringsStart;

        for (int i = 0; i < stringCount; i++) {
            int strOfs = absStringsStart + offsets[i];
            if (strOfs >= data.length) { stringPool[i] = ""; continue; }

            if (isUtf8) {
                // UTF-8: 2 length bytes then string
                int charLen = data[strOfs] & 0xFF;
                if ((charLen & 0x80) != 0) {
                    charLen = ((charLen & 0x7F) << 8) | (data[strOfs + 1] & 0xFF);
                    strOfs++;
                }
                strOfs++;
                int byteLen = data[strOfs] & 0xFF;
                if ((byteLen & 0x80) != 0) {
                    byteLen = ((byteLen & 0x7F) << 8) | (data[strOfs + 1] & 0xFF);
                    strOfs++;
                }
                strOfs++;
                try {
                    stringPool[i] = new String(data, strOfs, byteLen, "UTF-8");
                } catch (Exception e) {
                    stringPool[i] = "";
                }
            } else {
                // UTF-16LE: 2 length shorts then string
                int charLen = readShort(data, strOfs);
                strOfs += 2;
                char[] chars = new char[charLen];
                for (int c = 0; c < charLen; c++) {
                    chars[c] = (char) readShort(data, strOfs + c * 2);
                }
                stringPool[i] = new String(chars);
            }
        }
    }

    private void parseStartTag(byte[] data, int offset) {
        int nsIdx = readInt(data, offset + 8 + 8);
        int nameIdx = readInt(data, offset + 8 + 12);
        int attrStart = readInt(data, offset + 8 + 16) & 0xFFFF;
        int attrCount = readInt(data, offset + 8 + 20) & 0xFFFF;

        String tagName = getString(nameIdx);

        // Track state for intent-filter detection
        boolean isMainAction = false;
        boolean isLauncherCategory = false;
        String currentActivityName = null;

        if ("manifest".equals(tagName)) {
            // Read package attribute
            for (int i = 0; i < attrCount; i++) {
                int attrOfs = offset + 36 + i * 20;
                String attrName = getString(readInt(data, attrOfs + 4));
                int attrType = (readInt(data, attrOfs + 12) >> 24) & 0xFF;
                int attrValue = readInt(data, attrOfs + 16);
                String attrStrValue = getString(readInt(data, attrOfs + 8));

                if ("package".equals(attrName)) {
                    packageName = attrStrValue;
                }
            }
        } else if ("activity".equals(tagName)) {
            for (int i = 0; i < attrCount; i++) {
                int attrOfs = offset + 36 + i * 20;
                String attrName = getString(readInt(data, attrOfs + 4));
                String attrStrValue = getString(readInt(data, attrOfs + 8));

                if ("name".equals(attrName) && attrStrValue != null) {
                    if (attrStrValue.startsWith(".") && packageName != null) {
                        attrStrValue = packageName + attrStrValue;
                    }
                    activities.add(attrStrValue);
                }
            }
        } else if ("action".equals(tagName)) {
            for (int i = 0; i < attrCount; i++) {
                int attrOfs = offset + 36 + i * 20;
                String attrName = getString(readInt(data, attrOfs + 4));
                String attrStrValue = getString(readInt(data, attrOfs + 8));
                if ("name".equals(attrName) && "android.intent.action.MAIN".equals(attrStrValue)) {
                    // Mark the last activity as potential launcher
                    if (!activities.isEmpty()) {
                        launcherActivity = activities.get(activities.size() - 1);
                    }
                }
            }
        } else if ("uses-sdk".equals(tagName)) {
            for (int i = 0; i < attrCount; i++) {
                int attrOfs = offset + 36 + i * 20;
                String attrName = getString(readInt(data, attrOfs + 4));
                int attrValue = readInt(data, attrOfs + 16);
                if ("minSdkVersion".equals(attrName)) minSdkVersion = attrValue;
                if ("targetSdkVersion".equals(attrName)) targetSdkVersion = attrValue;
            }
        } else if ("uses-permission".equals(tagName)) {
            for (int i = 0; i < attrCount; i++) {
                int attrOfs = offset + 36 + i * 20;
                String attrName = getString(readInt(data, attrOfs + 4));
                String attrStrValue = getString(readInt(data, attrOfs + 8));
                if ("name".equals(attrName) && attrStrValue != null) {
                    permissions.add(attrStrValue);
                }
            }
        }
    }

    /** Fallback: parse plain text XML manifest */
    private ParseResult parseTextXml(String xml) {
        ParseResult r = new ParseResult();
        r.activities = new ArrayList<String>();
        r.permissions = new ArrayList<String>();

        // Extract package="..."
        int pkgIdx = xml.indexOf("package=\"");
        if (pkgIdx >= 0) {
            int start = pkgIdx + 9;
            int end = xml.indexOf('"', start);
            r.packageName = xml.substring(start, end);
        }

        // Extract activity android:name="..."
        int actIdx = 0;
        while ((actIdx = xml.indexOf("android:name=\"", actIdx)) >= 0) {
            int start = actIdx + 14;
            int end = xml.indexOf('"', start);
            String name = xml.substring(start, end);
            // Check if this is inside an <activity> tag
            int tagStart = xml.lastIndexOf('<', actIdx);
            if (tagStart >= 0) {
                String before = xml.substring(tagStart, actIdx);
                if (before.indexOf("activity") >= 0) {
                    if (name.startsWith(".") && r.packageName != null) {
                        name = r.packageName + name;
                    }
                    r.activities.add(name);
                }
            }
            actIdx = end;
        }

        // Find launcher (has MAIN action)
        if (xml.indexOf("android.intent.action.MAIN") >= 0 && !r.activities.isEmpty()) {
            r.launcherActivity = r.activities.get(0);
        }

        return r;
    }

    private String getString(int idx) {
        if (stringPool == null || idx < 0 || idx >= stringPool.length) return null;
        return stringPool[idx];
    }

    private static int readInt(byte[] data, int offset) {
        if (offset + 4 > data.length) return 0;
        return (data[offset] & 0xFF) |
               ((data[offset + 1] & 0xFF) << 8) |
               ((data[offset + 2] & 0xFF) << 16) |
               ((data[offset + 3] & 0xFF) << 24);
    }

    private static int readShort(byte[] data, int offset) {
        if (offset + 2 > data.length) return 0;
        return (data[offset] & 0xFF) | ((data[offset + 1] & 0xFF) << 8);
    }
}
