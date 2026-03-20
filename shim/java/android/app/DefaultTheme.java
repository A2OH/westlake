package android.app;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.ToggleButton;

/**
 * Provides default Holo-Light-like visual appearance for widgets
 * that have no theme applied. Called by Activity.renderFrame() before
 * measure/layout/draw to ensure widgets have visible default drawables.
 *
 * This is a bridge-layer file (not AOSP) -- safe to modify.
 */
public class DefaultTheme {

    // Holo Light colors
    public static final int COLOR_ACCENT = 0xFF33B5E5;       // Holo blue
    public static final int COLOR_BUTTON_BG = 0xFFD6D7D7;    // Light gray
    public static final int COLOR_BUTTON_PRESSED = 0xFFBBBBBB;
    public static final int COLOR_TEXT_PRIMARY = 0xFF212121;   // Near black
    public static final int COLOR_TEXT_SECONDARY = 0xFF757575; // Gray
    public static final int COLOR_TEXT_HINT = 0xFF9E9E9E;
    public static final int COLOR_DIVIDER = 0xFFBDBDBD;
    public static final int COLOR_BG = 0xFFF5F5F5;            // Off-white
    public static final int COLOR_TRACK = 0xFFBDBDBD;          // Gray track
    public static final int COLOR_PROGRESS = 0xFF33B5E5;       // Blue fill
    public static final int COLOR_STAR_FILLED = 0xFFFFB400;    // Gold star
    public static final int COLOR_STAR_EMPTY = 0xFFBDBDBD;     // Gray star outline
    public static final int COLOR_SWITCH_ON = 0xFF33B5E5;      // Blue
    public static final int COLOR_SWITCH_OFF = 0xFFBDBDBD;     // Gray

    // Tracks whether we already applied to a given view tree root
    // (to avoid re-applying every frame). Uses View.setTag().
    private static final Object TAG_THEMED = new Object();

    /**
     * Apply default theme to a view tree after inflation.
     * Walks the tree and sets default drawables for widgets that have none.
     * Safe to call multiple times -- skips views already themed.
     */
    public static void applyToViewTree(View root) {
        if (root == null) return;
        // Only apply once per root to avoid re-doing work each frame
        if (root.getTag() == TAG_THEMED) return;
        root.setTag(TAG_THEMED);
        applyRecursive(root);
    }

    private static void applyRecursive(View v) {
        applyToView(v);
        if (v instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) v;
            for (int i = 0; i < vg.getChildCount(); i++) {
                applyRecursive(vg.getChildAt(i));
            }
        }
    }

    /**
     * Apply theme to an individual view based on its widget type.
     */
    public static void applyToView(View v) {
        // Button (but not CompoundButton subclasses)
        if (v instanceof Button && !(v instanceof CompoundButton)
                && !(v instanceof ToggleButton) && v.getBackground() == null) {
            applyButtonTheme((Button) v);
        }
        // CheckBox
        if (v instanceof CheckBox) {
            applyCheckBoxTheme((CheckBox) v);
        }
        // RadioButton
        if (v instanceof RadioButton) {
            applyRadioButtonTheme((RadioButton) v);
        }
        // Switch
        if (v instanceof Switch) {
            applySwitchTheme((Switch) v);
        }
        // ToggleButton
        if (v instanceof ToggleButton && !(v instanceof Switch)) {
            applyToggleTheme((ToggleButton) v);
        }
        // EditText (but not AutoCompleteTextView)
        if (v instanceof EditText && !(v instanceof AutoCompleteTextView)
                && v.getBackground() == null) {
            applyEditTextTheme((EditText) v);
        }
        // ProgressBar (but not SeekBar or RatingBar)
        if (v instanceof ProgressBar && !(v instanceof SeekBar)
                && !(v instanceof RatingBar)) {
            applyProgressBarTheme((ProgressBar) v);
        }
        // SeekBar
        if (v instanceof SeekBar && !(v instanceof RatingBar)) {
            applySeekBarTheme((SeekBar) v);
        }
        // RatingBar
        if (v instanceof RatingBar) {
            applyRatingBarTheme((RatingBar) v);
        }
    }

    // ── Button ──────────────────────────────────────────────────────────

    private static void applyButtonTheme(Button btn) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_BUTTON_BG);
        bg.setCornerRadius(4);
        bg.setStroke(1, 0xFFAAAAAA);
        btn.setBackground(bg);
        btn.setPadding(24, 12, 24, 12);
        if (btn.getCurrentTextColor() == 0) {
            btn.setTextColor(COLOR_TEXT_PRIMARY);
        }
    }

    // ── CheckBox ────────────────────────────────────────────────────────

    private static void applyCheckBoxTheme(CheckBox cb) {
        if (cb.getButtonDrawable() != null) return;
        cb.setButtonDrawable(new CheckBoxDrawable());
        cb.setPadding(8, 0, 0, 0);
    }

    /**
     * Custom drawable that draws a checkbox: 24x24 box with optional checkmark.
     * Reads state from the drawable state set (state_checked).
     */
    static class CheckBoxDrawable extends Drawable {
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path mCheckPath = new Path();
        private boolean mChecked;

        public int getIntrinsicWidth() { return 48; }
        public int getIntrinsicHeight() { return 48; }

        public boolean isStateful() { return true; }

        protected boolean onStateChange(int[] stateSet) {
            boolean wasChecked = mChecked;
            mChecked = hasState(stateSet, android.R.attr.state_checked);
            return mChecked != wasChecked;
        }

        public void draw(Canvas canvas) {
            Rect b = getBounds();
            int cx = b.centerX();
            int cy = b.centerY();
            int half = Math.min(b.width(), b.height()) / 2 - 2;

            // Draw box outline
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(3);
            mPaint.setColor(mChecked ? COLOR_ACCENT : COLOR_DIVIDER);
            RectF box = new RectF(cx - half, cy - half, cx + half, cy + half);
            canvas.drawRoundRect(box, 4, 4, mPaint);

            if (mChecked) {
                // Fill background
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(COLOR_ACCENT);
                canvas.drawRoundRect(box, 4, 4, mPaint);

                // Draw checkmark
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(4);
                mPaint.setColor(0xFFFFFFFF);
                mPaint.setStrokeCap(Paint.Cap.ROUND);
                mPaint.setStrokeJoin(Paint.Join.ROUND);
                mCheckPath.reset();
                // Checkmark path: from lower-left through bottom-center to upper-right
                mCheckPath.moveTo(cx - half * 0.45f, cy);
                mCheckPath.lineTo(cx - half * 0.05f, cy + half * 0.4f);
                mCheckPath.lineTo(cx + half * 0.5f, cy - half * 0.35f);
                canvas.drawPath(mCheckPath, mPaint);
                mPaint.setStrokeCap(Paint.Cap.BUTT);
                mPaint.setStrokeJoin(Paint.Join.MITER);
            }
        }

        public void setAlpha(int alpha) { mPaint.setAlpha(alpha); }
        public void setColorFilter(ColorFilter cf) { mPaint.setColorFilter(cf); }
        public int getOpacity() { return android.graphics.PixelFormat.TRANSLUCENT; }
    }

    // ── RadioButton ─────────────────────────────────────────────────────

    private static void applyRadioButtonTheme(RadioButton rb) {
        if (rb.getButtonDrawable() != null) return;
        rb.setButtonDrawable(new RadioButtonDrawable());
        rb.setPadding(8, 0, 0, 0);
    }

    /**
     * Custom drawable that draws a radio button: circle with optional filled dot.
     */
    static class RadioButtonDrawable extends Drawable {
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private boolean mChecked;

        public int getIntrinsicWidth() { return 48; }
        public int getIntrinsicHeight() { return 48; }

        public boolean isStateful() { return true; }

        protected boolean onStateChange(int[] stateSet) {
            boolean wasChecked = mChecked;
            mChecked = hasState(stateSet, android.R.attr.state_checked);
            return mChecked != wasChecked;
        }

        public void draw(Canvas canvas) {
            Rect b = getBounds();
            int cx = b.centerX();
            int cy = b.centerY();
            int radius = Math.min(b.width(), b.height()) / 2 - 3;

            // Outer circle
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(3);
            mPaint.setColor(mChecked ? COLOR_ACCENT : COLOR_DIVIDER);
            canvas.drawCircle(cx, cy, radius, mPaint);

            // Inner filled dot when checked
            if (mChecked) {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(COLOR_ACCENT);
                canvas.drawCircle(cx, cy, radius * 0.5f, mPaint);
            }
        }

        public void setAlpha(int alpha) { mPaint.setAlpha(alpha); }
        public void setColorFilter(ColorFilter cf) { mPaint.setColorFilter(cf); }
        public int getOpacity() { return android.graphics.PixelFormat.TRANSLUCENT; }
    }

    // ── Switch ──────────────────────────────────────────────────────────

    private static void applySwitchTheme(Switch sw) {
        // Switch uses track and thumb drawables via its own setTrackDrawable/setThumbDrawable
        // Since those are AOSP code, we set them via the public API
        if (sw.getTrackDrawable() == null) {
            sw.setTrackDrawable(new SwitchTrackDrawable());
        }
        if (sw.getThumbDrawable() == null) {
            sw.setThumbDrawable(new SwitchThumbDrawable());
        }
    }

    static class SwitchTrackDrawable extends Drawable {
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private boolean mChecked;

        public int getIntrinsicWidth() { return 96; }
        public int getIntrinsicHeight() { return 48; }
        public boolean isStateful() { return true; }

        protected boolean onStateChange(int[] stateSet) {
            boolean was = mChecked;
            mChecked = hasState(stateSet, android.R.attr.state_checked);
            return mChecked != was;
        }

        public void draw(Canvas canvas) {
            Rect b = getBounds();
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(mChecked ? 0x8033B5E5 : 0x80BDBDBD);
            float r = b.height() / 2.0f;
            canvas.drawRoundRect(new RectF(b.left, b.top, b.right, b.bottom), r, r, mPaint);
        }

        public void setAlpha(int alpha) { mPaint.setAlpha(alpha); }
        public void setColorFilter(ColorFilter cf) { mPaint.setColorFilter(cf); }
        public int getOpacity() { return android.graphics.PixelFormat.TRANSLUCENT; }
    }

    static class SwitchThumbDrawable extends Drawable {
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private boolean mChecked;

        public int getIntrinsicWidth() { return 48; }
        public int getIntrinsicHeight() { return 48; }
        public boolean isStateful() { return true; }

        protected boolean onStateChange(int[] stateSet) {
            boolean was = mChecked;
            mChecked = hasState(stateSet, android.R.attr.state_checked);
            return mChecked != was;
        }

        public void draw(Canvas canvas) {
            Rect b = getBounds();
            int cx = b.centerX();
            int cy = b.centerY();
            int radius = Math.min(b.width(), b.height()) / 2 - 2;

            // Shadow
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(0x40000000);
            canvas.drawCircle(cx + 1, cy + 2, radius, mPaint);

            // Thumb circle
            mPaint.setColor(mChecked ? COLOR_SWITCH_ON : 0xFFFAFAFA);
            canvas.drawCircle(cx, cy, radius, mPaint);

            // Border
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(1);
            mPaint.setColor(0x30000000);
            canvas.drawCircle(cx, cy, radius, mPaint);
        }

        public void setAlpha(int alpha) { mPaint.setAlpha(alpha); }
        public void setColorFilter(ColorFilter cf) { mPaint.setColorFilter(cf); }
        public int getOpacity() { return android.graphics.PixelFormat.TRANSLUCENT; }
    }

    // ── ToggleButton ────────────────────────────────────────────────────

    private static void applyToggleTheme(ToggleButton tb) {
        if (tb.getBackground() != null) return;
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_BUTTON_BG);
        bg.setCornerRadius(4);
        bg.setStroke(1, 0xFFAAAAAA);
        tb.setBackground(bg);
        tb.setPadding(24, 12, 24, 12);
        if (tb.getTextOn() == null) tb.setTextOn("ON");
        if (tb.getTextOff() == null) tb.setTextOff("OFF");
        if (tb.getCurrentTextColor() == 0) {
            tb.setTextColor(COLOR_TEXT_PRIMARY);
        }
    }

    // ── EditText ────────────────────────────────────────────────────────

    private static void applyEditTextTheme(EditText et) {
        // EditText Holo look: bottom underline
        et.setBackground(new EditTextUnderlineDrawable());
        et.setPadding(4, 8, 4, 8);
        if (et.getCurrentTextColor() == 0) {
            et.setTextColor(COLOR_TEXT_PRIMARY);
        }
    }

    static class EditTextUnderlineDrawable extends Drawable {
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public void draw(Canvas canvas) {
            Rect b = getBounds();
            // Bottom underline - 2px thick
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(COLOR_ACCENT);
            canvas.drawRect(b.left, b.bottom - 4, b.right, b.bottom, mPaint);
        }

        public void setAlpha(int alpha) { mPaint.setAlpha(alpha); }
        public void setColorFilter(ColorFilter cf) { mPaint.setColorFilter(cf); }
        public int getOpacity() { return android.graphics.PixelFormat.TRANSLUCENT; }
    }

    // ── ProgressBar ─────────────────────────────────────────────────────

    private static void applyProgressBarTheme(ProgressBar pb) {
        if (pb.getProgressDrawable() != null) return;

        // Background track (gray)
        GradientDrawable trackDrawable = new GradientDrawable();
        trackDrawable.setColor(COLOR_TRACK);
        trackDrawable.setSize(-1, 8);
        trackDrawable.setCornerRadius(4);

        // Progress fill (blue) - wrapped in ClipDrawable
        GradientDrawable progressFill = new GradientDrawable();
        progressFill.setColor(COLOR_PROGRESS);
        progressFill.setSize(-1, 8);
        progressFill.setCornerRadius(4);
        ClipDrawable progressClip = new ClipDrawable(
                progressFill, Gravity.LEFT, ClipDrawable.HORIZONTAL);

        // Build LayerDrawable: [0]=background, [1]=secondaryProgress, [2]=progress
        // ProgressBar expects layer IDs: android.R.id.background, android.R.id.secondaryProgress, android.R.id.progress
        LayerDrawable layers = new LayerDrawable(new Drawable[]{
                trackDrawable, progressClip, progressClip});
        layers.setId(0, android.R.id.background);
        layers.setId(1, android.R.id.secondaryProgress);
        layers.setId(2, android.R.id.progress);
        pb.setProgressDrawable(layers);

        // Ensure minimum height
        if (pb.getMinimumHeight() <= 0) {
            pb.setMinimumHeight(8);
        }
    }

    // ── SeekBar ─────────────────────────────────────────────────────────

    private static void applySeekBarTheme(SeekBar sb) {
        if (sb.getProgressDrawable() != null && sb.getThumb() != null) return;

        if (sb.getProgressDrawable() == null) {
            // Track (thin gray line)
            GradientDrawable trackDrawable = new GradientDrawable();
            trackDrawable.setColor(COLOR_TRACK);
            trackDrawable.setSize(-1, 6);
            trackDrawable.setCornerRadius(3);

            // Progress (thin blue line)
            GradientDrawable progressFill = new GradientDrawable();
            progressFill.setColor(COLOR_PROGRESS);
            progressFill.setSize(-1, 6);
            progressFill.setCornerRadius(3);
            ClipDrawable progressClip = new ClipDrawable(
                    progressFill, Gravity.LEFT, ClipDrawable.HORIZONTAL);

            LayerDrawable layers = new LayerDrawable(new Drawable[]{
                    trackDrawable, progressClip, progressClip});
            layers.setId(0, android.R.id.background);
            layers.setId(1, android.R.id.secondaryProgress);
            layers.setId(2, android.R.id.progress);
            sb.setProgressDrawable(layers);
        }

        if (sb.getThumb() == null) {
            // Thumb: blue circle
            sb.setThumb(new SeekBarThumbDrawable());
        }
    }

    static class SeekBarThumbDrawable extends Drawable {
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public int getIntrinsicWidth() { return 40; }
        public int getIntrinsicHeight() { return 40; }

        public void draw(Canvas canvas) {
            Rect b = getBounds();
            int cx = b.centerX();
            int cy = b.centerY();
            int radius = Math.min(b.width(), b.height()) / 2 - 2;

            // Shadow
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(0x30000000);
            canvas.drawCircle(cx + 1, cy + 2, radius, mPaint);

            // Blue circle
            mPaint.setColor(COLOR_ACCENT);
            canvas.drawCircle(cx, cy, radius, mPaint);
        }

        public void setAlpha(int alpha) { mPaint.setAlpha(alpha); }
        public void setColorFilter(ColorFilter cf) { mPaint.setColorFilter(cf); }
        public int getOpacity() { return android.graphics.PixelFormat.TRANSLUCENT; }
    }

    // ── RatingBar ───────────────────────────────────────────────────────

    private static void applyRatingBarTheme(RatingBar rb) {
        if (rb.getProgressDrawable() != null) return;

        int numStars = rb.getNumStars();
        if (numStars <= 0) numStars = 5;

        // Create a star drawable for the rating bar
        // RatingBar uses a LayerDrawable with background/secondaryProgress/progress
        int starSize = 48;

        // Background: empty stars
        StarDrawable bgStars = new StarDrawable(numStars, starSize, false);
        // Progress: filled stars (clipped by ClipDrawable)
        StarDrawable filledStars = new StarDrawable(numStars, starSize, true);
        ClipDrawable progressClip = new ClipDrawable(
                filledStars, Gravity.LEFT, ClipDrawable.HORIZONTAL);

        LayerDrawable layers = new LayerDrawable(new Drawable[]{
                bgStars, progressClip, progressClip});
        layers.setId(0, android.R.id.background);
        layers.setId(1, android.R.id.secondaryProgress);
        layers.setId(2, android.R.id.progress);
        rb.setProgressDrawable(layers);
    }

    /**
     * Draws a row of star shapes for the RatingBar.
     */
    static class StarDrawable extends Drawable {
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final int mNumStars;
        private final int mStarSize;
        private final boolean mFilled;
        private final Path mStarPath = new Path();

        StarDrawable(int numStars, int starSize, boolean filled) {
            mNumStars = numStars;
            mStarSize = starSize;
            mFilled = filled;
        }

        public int getIntrinsicWidth() { return mStarSize * mNumStars; }
        public int getIntrinsicHeight() { return mStarSize; }

        public void draw(Canvas canvas) {
            Rect b = getBounds();
            float cellW = (float) b.width() / mNumStars;
            float cellH = b.height();
            float size = Math.min(cellW, cellH) * 0.85f;

            for (int i = 0; i < mNumStars; i++) {
                float cx = b.left + cellW * i + cellW / 2;
                float cy = b.top + cellH / 2;
                drawStar(canvas, cx, cy, size / 2);
            }
        }

        private void drawStar(Canvas canvas, float cx, float cy, float outerR) {
            float innerR = outerR * 0.4f;
            mStarPath.reset();

            // 5-pointed star
            for (int i = 0; i < 10; i++) {
                // Start from top (rotate -90 degrees = -PI/2)
                double angle = Math.PI * i / 5.0 - Math.PI / 2.0;
                float r = (i % 2 == 0) ? outerR : innerR;
                float x = cx + (float)(r * Math.cos(angle));
                float y = cy + (float)(r * Math.sin(angle));
                if (i == 0) {
                    mStarPath.moveTo(x, y);
                } else {
                    mStarPath.lineTo(x, y);
                }
            }
            mStarPath.close();

            if (mFilled) {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(COLOR_STAR_FILLED);
                canvas.drawPath(mStarPath, mPaint);
            } else {
                // Empty star: just outline
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(2);
                mPaint.setColor(COLOR_STAR_EMPTY);
                canvas.drawPath(mStarPath, mPaint);
            }
        }

        public void setAlpha(int alpha) { mPaint.setAlpha(alpha); }
        public void setColorFilter(ColorFilter cf) { mPaint.setColorFilter(cf); }
        public int getOpacity() { return android.graphics.PixelFormat.TRANSLUCENT; }
    }

    // ── Utility ─────────────────────────────────────────────────────────

    /**
     * Check if a state set contains a given state attribute.
     * Used by custom drawables to detect checked/pressed/etc. states
     * from the drawable state set passed via onStateChange().
     */
    private static boolean hasState(int[] stateSet, int state) {
        if (stateSet == null) return false;
        for (int i = 0; i < stateSet.length; i++) {
            if (stateSet[i] == state) return true;
        }
        return false;
    }
}
