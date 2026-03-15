package android.graphics.drawable;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.Color;

import android.graphics.Canvas;

/**
 * Shim: android.graphics.drawable.ColorDrawable
 * OH mapping: solid-color rectangle fill
 *
 * Pure Java stub — draw() is a no-op; color and alpha are stored in memory.
 */
public class ColorDrawable extends Drawable {

    private int color = 0x00000000; // fully transparent black by default

    // ── Constructors ─────────────────────────────────────────────────────────

    public ColorDrawable() {}

    public ColorDrawable(int color) {
        this.color = color;
    }

    // ── Color ────────────────────────────────────────────────────────────────

    public int getColor() { return color; }

    public void setColor(int color) { this.color = color; }

    // ── Alpha (stored in the high byte of color) ─────────────────────────────

    @Override
    public int getAlpha() {
        return (color >>> 24);
    }

    @Override
    public void setAlpha(int alpha) {
        color = (color & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }

    // ── Draw (no-op stub) ────────────────────────────────────────────────────

    @Override
    public void draw(Canvas canvas) {
        if (canvas == null || (color >>> 24) == 0) return;
        android.graphics.Rect b = getBounds();
        if (b.width() > 0 && b.height() > 0) {
            android.graphics.Paint paint = new android.graphics.Paint();
            paint.setColor(color);
            paint.setStyle(android.graphics.Paint.Style.FILL);
            canvas.drawRect(b.left, b.top, b.right, b.bottom, paint);
        } else {
            canvas.drawColor(color);
        }
    }

    // ── Object overrides ─────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "ColorDrawable(color=0x" + Integer.toHexString(color) + ")";
    }
}
