package android.location;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Android-compatible Address shim. Stores geocoded address components.
 */
public class Address {
    private Locale mLocale;
    private Map<Integer, String> mAddressLines = new HashMap<>();
    private String mFeatureName;
    private String mAdminArea;
    private String mSubAdminArea;
    private String mLocality;
    private String mSubLocality;
    private String mThoroughfare;
    private String mSubThoroughfare;
    private String mPostalCode;
    private String mCountryCode;
    private String mCountryName;
    private double mLatitude = Double.NaN;
    private double mLongitude = Double.NaN;
    private boolean mHasLatitude;
    private boolean mHasLongitude;

    public Address(Locale locale) { mLocale = locale; }

    public Locale getLocale() { return mLocale; }

    public int getMaxAddressLineIndex() { return mAddressLines.size() - 1; }
    public String getAddressLine(int index) { return mAddressLines.get(index); }
    public void setAddressLine(int index, String line) { mAddressLines.put(index, line); }

    public String getFeatureName() { return mFeatureName; }
    public void setFeatureName(String name) { mFeatureName = name; }

    public String getAdminArea() { return mAdminArea; }
    public void setAdminArea(String area) { mAdminArea = area; }

    public String getLocality() { return mLocality; }
    public void setLocality(String locality) { mLocality = locality; }

    public String getSubLocality() { return mSubLocality; }
    public void setSubLocality(String subLocality) { mSubLocality = subLocality; }

    public String getThoroughfare() { return mThoroughfare; }
    public void setThoroughfare(String thoroughfare) { mThoroughfare = thoroughfare; }

    public String getPostalCode() { return mPostalCode; }
    public void setPostalCode(String postalCode) { mPostalCode = postalCode; }

    public String getCountryCode() { return mCountryCode; }
    public void setCountryCode(String code) { mCountryCode = code; }

    public String getCountryName() { return mCountryName; }
    public void setCountryName(String name) { mCountryName = name; }

    public boolean hasLatitude() { return mHasLatitude; }
    public double getLatitude() { return mLatitude; }
    public void setLatitude(double lat) { mLatitude = lat; mHasLatitude = true; }

    public boolean hasLongitude() { return mHasLongitude; }
    public double getLongitude() { return mLongitude; }
    public void setLongitude(double lon) { mLongitude = lon; mHasLongitude = true; }
}
