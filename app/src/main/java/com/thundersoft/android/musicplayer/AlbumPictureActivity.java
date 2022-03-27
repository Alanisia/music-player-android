package com.thundersoft.android.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.thundersoft.android.musicplayer.util.Constants;
import com.thundersoft.android.musicplayer.util.Utils;

@SuppressLint("ClickableViewAccessibility")
public class AlbumPictureActivity extends AppCompatActivity implements View.OnTouchListener {
    private static final String TAG = AlbumPictureActivity.class.getSimpleName();
    private float x, y;
    private ImageView albumPicture;
    private ImageLocation imageLocation;
    private int screenWidth, screenHeight;
    private int zoomTimes;

    private static class ImageLocation {
        int l, t, r, b;

        public ImageLocation(int left, int top, int right, int bottom) {
            l = left; t = top; r = right; b = bottom;
        }

        @Override
        public String toString() {
            return Utils.json(this, false);
        }
    }

    private int getTitleHeight() {
        return getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
    }

    private int getStatusBarHeight() {
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v.getId() == R.id.album_picture) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = event.getX();
                    y = event.getY();
                    Log.d(TAG, "onTouch: x = " + x + "|y = " + y);
                case MotionEvent.ACTION_MOVE:
                    int left = (int) (event.getRawX() - x);
                    int top = (int) (event.getRawY() - y - getTitleHeight() - getStatusBarHeight());
                    int right = left + v.getWidth();
                    int bottom = top + v.getHeight();
                    if (left >= 0 && top >= 0 && right <= screenWidth && bottom <= screenHeight) {
                        v.layout(left, top, right, bottom);
                        imageLocation = new ImageLocation(left, top, v.getWidth() + left, v.getHeight() + top);
                        Log.d(TAG, "onTouch: " + imageLocation);
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
        Log.d(TAG, "onTouch: " + v.getId());
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_picture);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;

        Intent intent = getIntent();
        byte[] bytes = intent.getByteArrayExtra(Constants.ALBUM_ART);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        albumPicture = findViewById(R.id.album_picture);
        albumPicture.setImageBitmap(bitmap);
        albumPicture.setOnTouchListener(this);
//        int[] xy = new int[2], xys = new int[2];
//        albumPicture.getLocationInWindow(xy);
//        albumPicture.getLocationOnScreen(xys);
//        Log.d(TAG, String.format("onCreate: x = %d, y = %d", xy[0], xy[1]));
//        Log.d(TAG, String.format("onCreate: x = %d, y = %d", xys[0], xys[1]));

        ImageButton zoomIn = findViewById(R.id.zoom_in);
        ImageButton zoomOut = findViewById(R.id.zoom_out);

        zoomIn.setOnClickListener(v -> {
            if (zoomTimes < 3) {
                resize(bitmap, albumPicture.getWidth(), albumPicture.getHeight(), true);
                zoomTimes++;
            }
        });

        zoomOut.setOnClickListener(v -> {
            if (zoomTimes > 0) {
                resize(bitmap, albumPicture.getWidth(), albumPicture.getHeight(), false);
                zoomTimes--;
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Rect rect = new Rect();
        albumPicture.getGlobalVisibleRect(rect);
        Log.d(TAG, String.format("onWindowFocusChanged: left = %d, top = %d, right = %d, bottom = %d",
                rect.left, rect.top, rect.right, rect.bottom));
        imageLocation = new ImageLocation(rect.left, rect.top, rect.right, rect.bottom);
        Log.d(TAG, "onWindowFocusChanged: " + imageLocation);
    }

    private void resize(Bitmap bitmap, int w, int h, boolean in) {
        Log.d(TAG, String.format("resize: w = %d, h = %d", w, h));
        ConstraintLayout layout = findViewById(R.id.image_layout);
        layout.removeView(albumPicture);
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);
        imageView.setId(R.id.album_picture);
        float scale = 1.25f;
        if (in) imageView.setLayoutParams(new ConstraintLayout.LayoutParams((int) (w * scale), (int) (h * scale)));
        else imageView.setLayoutParams(new ConstraintLayout.LayoutParams((int) (w / scale), (int) (h / scale)));
        Log.d(TAG, "resize: " + imageLocation);
        imageView.layout(imageLocation.l, imageLocation.t, imageLocation.r, imageLocation.b);
        layout.addView(imageView);
        albumPicture = imageView;
        albumPicture.setOnTouchListener(this);
    }
}