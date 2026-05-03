package com.mcdonalds.offer;

import android.content.Context;
import android.util.AttributeSet;
import java.util.Collections;
import java.util.List;

public class MultiStepBonusProgressBar extends android.view.View {
    private List mMarkers = Collections.emptyList();
    private int mCurrentProgress;

    public MultiStepBonusProgressBar(Context context) {
        super(context);
    }

    public MultiStepBonusProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MultiStepBonusProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMarkers(List markers) {
        mMarkers = markers != null ? markers : Collections.emptyList();
        invalidate();
    }

    public List getMarkers() {
        return mMarkers;
    }

    public void setCurrentProgress(int currentProgress) {
        mCurrentProgress = currentProgress;
        invalidate();
    }

    public int getCurrentProgress() {
        return mCurrentProgress;
    }
}
