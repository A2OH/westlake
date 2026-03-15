package android.widget;
import android.view.View;
import android.view.View;

import android.view.View;
import com.ohos.shim.bridge.OHBridge;

/**
 * Shim: android.widget.ImageView → ARKUI_NODE_IMAGE
 */
public class ImageView extends View {
    static final int NODE_TYPE_IMAGE = 4;
    static final int ATTR_IMAGE_SRC = 4000;

    private int imageResource;
    private String imageUri;
    private android.graphics.Bitmap mBitmap;

    public ImageView() {
        super(NODE_TYPE_IMAGE);
    }

    public void setImageResource(int resId) {
        this.imageResource = resId;
        // Resource lookup would resolve resId → file path
    }

    public void setImageBitmap(android.graphics.Bitmap bm) {
        mBitmap = bm;
    }

    public void setImageURI(Object uri) {
        if (uri != null) {
            this.imageUri = uri.toString();
            if (nativeHandle != 0) {
                OHBridge.nodeSetAttrString(nativeHandle, ATTR_IMAGE_SRC, imageUri);
            }
        }
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    /** ScaleType enum (subset) */
    public enum ScaleType {
        MATRIX, FIT_XY, FIT_START, FIT_CENTER, FIT_END,
        CENTER, CENTER_CROP, CENTER_INSIDE
    }

    public void setScaleType(ScaleType scaleType) {
        // ArkUI image has objectFit — map later
    }
}
