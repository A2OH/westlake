package android.content.res;

import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;

/** AOSP compilation stub for XmlResourceParser. */
public interface XmlResourceParser extends XmlPullParser, AttributeSet, AutoCloseable {
    void close();
}
