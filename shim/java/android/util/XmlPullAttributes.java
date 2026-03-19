package android.util;

import org.xmlpull.v1.XmlPullParser;

/** Stub: wraps an XmlPullParser as an AttributeSet. */
public class XmlPullAttributes implements AttributeSet {
    private final XmlPullParser mParser;

    public XmlPullAttributes(XmlPullParser parser) {
        mParser = parser;
    }

    public int getAttributeCount() {
        return mParser.getAttributeCount();
    }

    public String getAttributeName(int index) {
        return mParser.getAttributeName(index);
    }

    public String getAttributeValue(int index) {
        return mParser.getAttributeValue(index);
    }

    public String getAttributeValue(String namespace, String name) {
        return mParser.getAttributeValue(namespace, name);
    }

    public int getStyleAttribute() { return 0; }
    public String getClassAttribute() { return null; }
}
