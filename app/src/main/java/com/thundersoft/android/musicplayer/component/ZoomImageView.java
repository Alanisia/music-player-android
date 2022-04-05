package com.thundersoft.android.musicplayer.component;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.thundersoft.android.musicplayer.util.Todo;

@Todo
public class ZoomImageView extends AppCompatImageView {
    private ScaleGestureDetector.SimpleOnScaleGestureListener onScaleGestureListener;
    private Matrix matrix;
    private float scaleFactor;

    public ZoomImageView(@NonNull Context context) {
        this(context, null);
    }

    public ZoomImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        matrix = new Matrix();
        setScaleType(ScaleType.MATRIX);
        onScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                Drawable drawable = getDrawable();


                float oldScaleFactor = detector.getScaleFactor();
                matrix.postScale(oldScaleFactor, oldScaleFactor, detector.getFocusX(), detector.getFocusY());
                setImageMatrix(matrix);



                return super.onScale(detector);
            }
        };




    }
}
