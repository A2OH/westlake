package com.westlake.host;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ViewerActivity extends Activity {
    private static final String SRC_PNG = "/sdcard/westlake_frame.png";
    private static final String TOUCH_PATH = "/sdcard/westlake_touch.dat";
    private static final int FB_W = 480;
    private static final int FB_H = 800;

    private ImageView iv;
    private Handler handler;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        iv = new ImageView(this);
        iv.setBackgroundColor(Color.BLACK);
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
        setContentView(iv);

        iv.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX() * FB_W / v.getWidth();
                float y = event.getY() * FB_H / v.getHeight();
                int action = 2;
                if (event.getAction() == MotionEvent.ACTION_DOWN) action = 0;
                else if (event.getAction() == MotionEvent.ACTION_UP) action = 1;
                writeTouchEvent(action, (int)x, (int)y);
                return true;
            }
        });

        handler = new Handler();
        handler.post(pollPng);
    }

    private Runnable pollPng = new Runnable() {
        public void run() {
            try {
                /* Copy PNG from /sdcard/ to app's internal cache, then decode */
                File src = new File(SRC_PNG);
                if (src.exists() && src.length() > 100) {
                    File dst = new File(getCacheDir(), "frame.png");
                    copyFile(src, dst);
                    Bitmap bmp = BitmapFactory.decodeFile(dst.getAbsolutePath());
                    if (bmp != null) {
                        iv.setImageBitmap(bmp);
                    }
                }
            } catch (Exception e) { /* retry */ }
            handler.postDelayed(this, 500);
        }
    };

    private void copyFile(File src, File dst) throws Exception {
        FileInputStream fis = new FileInputStream(src);
        FileOutputStream fos = new FileOutputStream(dst);
        byte[] buf = new byte[8192];
        int n;
        while ((n = fis.read(buf)) > 0) fos.write(buf, 0, n);
        fos.close();
        fis.close();
    }

    private void writeTouchEvent(int action, int x, int y) {
        try {
            RandomAccessFile raf = new RandomAccessFile(TOUCH_PATH, "rw");
            ByteBuffer buf = ByteBuffer.allocate(16);
            buf.order(ByteOrder.LITTLE_ENDIAN);
            buf.putInt(action);
            buf.putInt(x);
            buf.putInt(y);
            buf.putInt((int)(System.currentTimeMillis() & 0x7FFFFFFF));
            raf.write(buf.array());
            raf.close();
        } catch (Exception e) { /* ignore */ }
    }
}
