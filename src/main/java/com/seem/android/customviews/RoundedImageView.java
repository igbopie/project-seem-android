package com.seem.android.customviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by igbopie on 13/05/14.
 */
public class RoundedImageView  extends ImageView {

        public static float radius = 100.0f;

        public RoundedImageView(Context context) {
            super(context);
            this.setScaleType(ScaleType.CENTER_CROP);
        }

        public RoundedImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.setScaleType(ScaleType.CENTER_CROP);
        }

        public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            this.setScaleType(ScaleType.CENTER_CROP);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            //float radius = 36.0f;
            Path clipPath = new Path();
            RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
            clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW);
            canvas.clipPath(clipPath);
            super.onDraw(canvas);
        }
}
