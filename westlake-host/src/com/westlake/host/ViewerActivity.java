package com.westlake.host;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import java.io.File;
import java.io.FileInputStream;

/**
 * Displays framebuffer.raw rendered by dalvikvm's Westlake engine.
 * Uses ImageView instead of SurfaceView to avoid double-buffer flicker.
 */
public class ViewerActivity extends Activity {
    private static final String FB_PATH = "/data/local/tmp/a2oh/framebuffer.raw";
    private static final int FB_W = 480;
    private static final int FB_H = 800;

    private ImageView imageView;
    private Bitmap bitmap;
    private int[] pixels;
    private byte[] raw;
    private Handler handler;
    private long lastMod = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageView = new ImageView(this);
        imageView.setBackgroundColor(Color.BLACK);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        setContentView(imageView);

        bitmap = Bitmap.createBitmap(FB_W, FB_H, Bitmap.Config.ARGB_8888);
        pixels = new int[FB_W * FB_H];
        raw = new byte[FB_W * FB_H * 4];
        handler = new Handler();

        /* Poll for framebuffer updates */
        handler.post(new Runnable() {
            public void run() {
                updateFrame();
                handler.postDelayed(this, 100); /* 10fps polling */
            }
        });
    }

    private void updateFrame() {
        try {
            File f = new File(FB_PATH);
            if (!f.exists() || f.length() != FB_W * FB_H * 4) return;
            long mod = f.lastModified();
            if (mod == lastMod) return;
            lastMod = mod;

            /* Check lock file */
            if (new File(FB_PATH + ".lock").exists()) return;

            FileInputStream fis = new FileInputStream(f);
            int total = 0;
            while (total < raw.length) {
                int n = fis.read(raw, total, raw.length - total);
                if (n <= 0) break;
                total += n;
            }
            fis.close();
            if (total != raw.length) return;

            /* BGRA → ARGB */
            for (int i = 0; i < FB_W * FB_H; i++) {
                int off = i * 4;
                int b = raw[off] & 0xFF;
                int g = raw[off + 1] & 0xFF;
                int r = raw[off + 2] & 0xFF;
                int a = raw[off + 3] & 0xFF;
                pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
            }
            bitmap.setPixels(pixels, 0, FB_W, 0, 0, FB_W, FB_H);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            /* ignore */
        }
    }
}
