package com.bytedance.videoplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class playvideo extends VideoView {
    public playvideo(Context context) {
        super(context);
    }

    public playvideo(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public playvideo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
}