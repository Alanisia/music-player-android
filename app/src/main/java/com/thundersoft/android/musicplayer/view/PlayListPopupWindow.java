package com.thundersoft.android.musicplayer.view;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

@SuppressLint("ClickableViewAccessibility")
public class PlayListPopupWindow extends PopupWindow {
    private final int height;

    public PlayListPopupWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);

        this.height = height;
        setTouchable(true);
        setTouchInterceptor((v, e) -> false);
        setBackgroundDrawable(new ColorDrawable(0x000000));
    }

    public void show(View parent) {
        showAtLocation(parent, Gravity.NO_GRAVITY, 0, parent.getTop() - height);
    }
}
