package com.seem.android.customviews;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.seem.android.R;
import com.seem.android.util.Utils;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Created by igbopie on 16/05/14.
 */
public class ThreadedViewComponent extends FrameLayout  implements GestureDetector.OnGestureListener {

    boolean disabled = true;

    int parentWidth;
    int parentHeight;
    GestureDetector mGestureDectector;
    FrameLayout layout;


    float qrt1 =10f/100f;
    float qrt2 =15f/100f;
    int minDisplayedItem = 0;
    int maxDisplayedItem = 0;
    int nItems = 10;
    final float ITEMS_DIFF = .10f;
    final float INIT_POSITION = qrt1+0.01f;

    Adapter adapter;


    Map<View,Float> amounts = new HashMap<View, Float>();
    AnimatorSet animation;
    float animDiff;

    Queue<View> freeQeue = new ArrayDeque<View>();

    public ThreadedViewComponent(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, new int[]{}, 0, 0);

        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_threaded_view, this, true);

        layout = (FrameLayout) findViewById(R.id.frameLayout);


        mGestureDectector = new GestureDetector(context,this);
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return mGestureDectector.onTouchEvent(motionEvent);
            }
        });


        init();

    }

    public void calculateImagePosition(View image,float amount){
        if(disabled) return;

        //StoreAmount
        amounts.put(image,amount);

        float minSize = parentWidth - Utils.dpToPixel(100,getContext());
        float maxSize = parentWidth;
        float topOffset = 0;
        int dark = 255;
        int maxDark = 255;

        //0 up, 1 down

        //1 / 3 min -> max
        //2 / 3  max -> max
        //3 / 3 max->min
        float convertedAmount = 0;
        float size = parentWidth;


        if(amount < qrt1){
            convertedAmount = amount * (1/qrt1);
            size = (convertedAmount*(maxSize-minSize))+minSize;
            dark = (int) (maxDark * (convertedAmount));
        }else if(amount>=qrt1 && amount <= qrt2){
            size=parentWidth;
        }else if (amount > qrt2){
            convertedAmount = (amount-qrt2) * (1/qrt1);
            size = ((1-convertedAmount)*(maxSize-minSize))+minSize;
            topOffset = convertedAmount * Utils.dpToPixel(200,getContext());

            dark = (int) (maxDark * (1-convertedAmount));
        }

        if(size < minSize){
            size = minSize;
        }
        FrameLayout.LayoutParams params = (LayoutParams) image.getLayoutParams();
        if(params != null) {
            params.height = (int) size;
            params.width = (int) size;
        }else{
            params = new LayoutParams((int)size,(int)size);
        }
        //params.leftMargin = (int) ((parentWidth - size)/2);
        //params.topMargin= (int) ((parentHeight*amount)+topOffset);

        image.setLayoutParams(params);

        image.setTranslationX((parentWidth - size)/2);
        image.setTranslationY((parentHeight * amount) + topOffset);

        if(dark<100){
            dark = 100;
        }
        if(dark>255){
            dark = 255;
        }
        if(image instanceof ImageView) {
            ((ImageView) image).setColorFilter(Color.rgb(dark, dark, dark), PorterDuff.Mode.MULTIPLY);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int lastParentWidth = parentWidth;
        int lastParentHeight = parentHeight;

        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);

        if(lastParentHeight != parentHeight || lastParentWidth != parentWidth) {
            //Change
            //Recalculate children

            Utils.debug(getClass(),"Recalculate");
            for (int i = 0; i < layout.getChildCount(); i++) {
                View view = layout.getChildAt(i);
                float amount = amounts.get(view);
                calculateImagePosition(view, amount);
            }
        }

    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        if(disabled) return false;

        if(animation != null){
            animation.cancel();
        }
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
    synchronized public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float diffAmount = distanceY / parentHeight;

        scroll(diffAmount);

        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        final float distanceTimeFactor = 0.4f;
        //final float totalDx = (distanceTimeFactor * velocityX/2);
        final float totalDy = (distanceTimeFactor * velocityY/2);

        float diffAmount = totalDy / parentHeight;
        animDiff = 0;
        if(animation != null){
            animation.cancel();
        }
        animation = new AnimatorSet();
        animation.play(ObjectAnimator.ofFloat(this, "scrollTotal", 0, diffAmount));

        animation.setDuration((long) (1000 * distanceTimeFactor));

        animation.setInterpolator(new DecelerateInterpolator());

        animation.start();
        return true;
    }

    public void setScrollTotal(float totalAmount) {
        scroll(animDiff-totalAmount);
        animDiff = totalAmount;
    }

    public void scroll(float diffAmount){
        //Utils.debug(getClass(),"Scroll"+disabled);
        if(disabled) return;


        boolean topRemoved = false;
        boolean bottomRemoved = false;

        //Pre check to check limits
        //TOP - first element
        float firstChildPosition = amounts.get(layout.getChildAt(0)) - diffAmount;
        if (minDisplayedItem == 0 && firstChildPosition > INIT_POSITION && diffAmount < 0) {
            diffAmount = 0;
            amounts.put(layout.getChildAt(0),INIT_POSITION);
            if(animation != null){
                animation.cancel();
            }
        }
        //Bottom - last element
        float lastChildPosition = amounts.get(layout.getChildAt(layout.getChildCount()-1)) - diffAmount;
        if (maxDisplayedItem == nItems && lastChildPosition < INIT_POSITION && diffAmount > 0) {
            diffAmount = 0;
            int nChild = layout.getChildCount();
            float totalDiff = (nChild-1)*ITEMS_DIFF;
            float firstItemPosition = INIT_POSITION - totalDiff;
            amounts.put(layout.getChildAt(0),firstItemPosition);
            if(animation != null){
                animation.cancel();
            }
        }

        for(int i = 0;i<layout.getChildCount();i++){
            View child = layout.getChildAt(i);
            float amount = amounts.get(layout.getChildAt(0));
            amount -= diffAmount;
            amount+=(i*ITEMS_DIFF);//This will avoid items from overlapping

            //check if out of screen
            if(amount > 1){
                //Yes, remove it and add it to the free qeue
                freeQeue.add(child);
                layout.removeViewAt(i);
                amounts.remove(child);
                maxDisplayedItem --;
                bottomRemoved = true;
            }else if(amount < 0){
                //Yes, remove it and add it to the free qeue
                freeQeue.add(child);
                layout.removeViewAt(i);
                amounts.remove(child);
                minDisplayedItem++;
                topRemoved = true;
            }else {
                //No, just display it
                calculateImagePosition(child, amount);
            }
        }


        if(layout.getChildCount() > 0) {
            //Now, lets check what we need to add
            //Top
            Float amount = amounts.get(layout.getChildAt(0));
            while (!topRemoved && amount > ITEMS_DIFF && minDisplayedItem > 0) {

                minDisplayedItem--;
                View view = reuseView(minDisplayedItem);
                //Fill view with.. minDisplayedItem
                amount -= ITEMS_DIFF;
                calculateImagePosition(view, amount);
                layout.addView(view, 0);

            }


            //Bottom
            int lastIndex = layout.getChildCount() - 1;
            View lastView = layout.getChildAt(lastIndex);
            Float lastAmount = amounts.get(lastView);
            lastAmount = lastAmount == null ? 1 : lastAmount;
            amount = lastAmount;
            while (!bottomRemoved && amount < 1 && (1 - amount) > ITEMS_DIFF && maxDisplayedItem < nItems) {

                maxDisplayedItem++;
                View view = reuseView(maxDisplayedItem);
                //Fill view with.. maxDisplayedItem
                amount += ITEMS_DIFF;
                calculateImagePosition(view, amount);
                layout.addView(view);

            }
        }
    }

    private void init(){
        Utils.debug(getClass(),"INIT");
        layout.removeAllViews();
        if(adapter == null){
            disabled = true;
            return;
        }
        if(adapter.getCount() == 0){
            disabled = true;
            return;
        }
        disabled = false;
        nItems = adapter.getCount();
        freeQeue.clear();
        minDisplayedItem = 0;
        maxDisplayedItem = 0;
        amounts.clear();

        float positions = INIT_POSITION;
        minDisplayedItem = 0;
        maxDisplayedItem = 0;
        for(int i = 0;i < nItems && positions < 1;i++){

            View view = reuseView(i);

            layout.addView(view);

            calculateImagePosition(view,positions);
            maxDisplayedItem++;
            positions+= ITEMS_DIFF;
        }
    }

    View reuseView(int position){
        View view = freeQeue.poll();
        view = adapter.getView(position,view,layout);
        return view;
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        //reset things
        init();

    }
}