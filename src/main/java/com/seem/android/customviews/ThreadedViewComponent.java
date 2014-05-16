package com.seem.android.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

import com.seem.android.GlobalVars;
import com.seem.android.R;
import com.seem.android.util.Utils;

/**
 * Created by igbopie on 16/05/14.
 */
public class ThreadedViewComponent extends FrameLayout  implements GestureDetector.OnGestureListener {
    int parentWidth;
    int parentHeight;
    View currentAction;
    float initialAmount;

    ImageView image1;
    ImageView image2;
    ImageView image3;
    FrameLayout layout;
    public ThreadedViewComponent(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, new int[]{}, 0, 0);

        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_threaded_view, this, true);

        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        image3 = (ImageView) findViewById(R.id.image3);
        layout = (FrameLayout) findViewById(R.id.frameLayout);


        final GestureDetector mGestureDectector = new GestureDetector(context,this);
        image3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                currentAction = image3;
                return mGestureDectector.onTouchEvent(motionEvent);
            }
        });
        image2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                currentAction = image2;
                return mGestureDectector.onTouchEvent(motionEvent);
            }
        });
        image1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                currentAction = image1;
                return mGestureDectector.onTouchEvent(motionEvent);
            }
        });

    }

    public void calculateImagePosition(View image,float amount){
        float qrt1 =5f/100f;
        float qrt2 =40f/100f;
        //float qrt3 =3f/3f;

        float minSize = parentWidth - Utils.dpToPixel(100,getContext());
        float maxSize = parentWidth;
        float topOffset = 0;

        //0 up, 1 down

        //1 / 3 min -> max
        //2 / 3  max -> max
        //3 / 3 max->min
        float convertedAmount = 0;
        float size = parentWidth;

        if(amount < qrt1){
            convertedAmount = amount * (1/qrt1);
            size = (convertedAmount*(maxSize-minSize))+minSize;
        }else if(amount>=qrt1 && amount <= qrt2){
            size=parentWidth;
        }else if (amount > qrt2){
            convertedAmount = (amount-qrt2) * (1/qrt1);
            size = ((1-convertedAmount)*(maxSize-minSize))+minSize;
            topOffset = convertedAmount * Utils.dpToPixel(200,getContext());
        }

        if(size < minSize){
            size = minSize;
        }
        FrameLayout.LayoutParams params = (LayoutParams) image.getLayoutParams();
        params.height = (int) size;
        params.width = (int) size;
        params.leftMargin = (int) ((parentWidth - size)/2);
        params.topMargin= (int) ((parentHeight*amount)+topOffset);

        image.setLayoutParams(params);

    }
    public void fontImageMove(){

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        initialAmount=(float)currentAction.getTop()/(float)parentHeight;
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float diffAmount = (e1.getRawY()-e2.getRawY()) /(float)parentHeight;
        float currentAmount = initialAmount - diffAmount;

        float diff = .33f;
        int index =layout.indexOfChild(currentAction);
        float firstAmount = currentAmount - (diff*index);

        for(int i = 0;i<layout.getChildCount();i++){
            View child = layout.getChildAt(i);
            calculateImagePosition(child,firstAmount);
            firstAmount+=diff;
        }

        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

}