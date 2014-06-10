package com.seem.android.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by igbopie on 19/03/14.
 */
public class SquareImageView extends ImageView {


    public SquareImageView(Context context) {
        super(context);
        init();
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }

    private void init(){
        this.setScaleType(ScaleType.CENTER_CROP);
    }

}
