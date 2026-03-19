package org.xmlpull.v1;

/** AOSP compilation stub for XmlPullParser. */
public interface XmlPullParser {
    int START_TAG = 2;
    int END_TAG = 3;
    int END_DOCUMENT = 1;
    int START_DOCUMENT = 0;
    int TEXT = 4;

    int getEventType() throws XmlPullParserException;
    int next() throws XmlPullParserException, java.io.IOException;
    String getName();
    String getAttributeValue(String namespace, String name);
    int getAttributeCount();
    String getAttributeName(int index);
    String getAttributeValue(int index);
    int getDepth();
    String getNamespace();
    String getPositionDescription();
    boolean isEmptyElementTag() throws XmlPullParserException;
    void require(int type, String namespace, String name) throws XmlPullParserException, java.io.IOException;
    String nextText() throws XmlPullParserException, java.io.IOException;
}
