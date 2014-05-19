package com.seem.android.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
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
    int parentWidth;
    int parentHeight;
    GestureDetector mGestureDectector;

    //ImageView image1;
    //ImageView image2;
    //ImageView image3;
    FrameLayout layout;

    int minDisplayedItem = 0;
    int maxDisplayedItem = 0;
    int nItems = 10;
    final float ITEMS_DIFF = .33f;
    final float INIT_POSITION = 0.2f;

    Map<View,Float> amounts = new HashMap<View, Float>();

    //List<ImageView> views = new ArrayList<ImageView>();
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
        //StoreAmount
        amounts.put(image,amount);

        float qrt1 =5f/100f;
        float qrt2 =40f/100f;
        //float qrt3 =3f/3f;

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
        params.leftMargin = (int) ((parentWidth - size)/2);
        params.topMargin= (int) ((parentHeight*amount)+topOffset);

        image.setLayoutParams(params);

        if(dark<50){
            dark = 50;
        }
        if(dark>255){
            dark = 255;
        }
        ((ImageView)image).setColorFilter(Color.rgb(dark, dark, dark), PorterDuff.Mode.MULTIPLY);

    }
    public void fontImageMove(){

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
        /*if(amounts.containsKey(gestureCurrentAction)){
            gestureInitialAmount = amounts.get(gestureCurrentAction);
        }else{
            gestureInitialAmount = 0;
        }*/
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

        //Limits
        /*if(maxDisplayedItem == nItems && amount < INIT_POSITION){
            amount = INIT_POSITION;
        }
        if(minDisplayedItem == 0 && amount > INIT_POSITION){
            amount = INIT_POSITION;
        }*/
        boolean topRemoved = false;
        boolean bottomRemoved = false;
        for(int i = 0;i<layout.getChildCount();i++){
            View child = layout.getChildAt(i);
            float amount = amounts.get(layout.getChildAt(0));
            amount -= diffAmount;
            amount+=(i*ITEMS_DIFF);//This will avoid items from overlapping


            if(minDisplayedItem == 0 && i == 0 && amount > INIT_POSITION) {
                amount = INIT_POSITION;
            }
            if(minDisplayedItem == (nItems-1) && i == 0 && amount < INIT_POSITION) {
                amount = INIT_POSITION;
            }


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

        //Now, lets check what we need to add
        //Top
        Float amount = amounts.get(layout.getChildAt(0));
        Utils.debug(getClass(),""+amount);
        while(!topRemoved && amount > ITEMS_DIFF && minDisplayedItem > 0){

            View view = reuseView();
            //Fill view with.. minDisplayedItem
            amount -= ITEMS_DIFF;
            calculateImagePosition(view,amount);
            layout.addView(view,0);

            minDisplayedItem--;
        }

        //Bottom
        int lastIndex = layout.getChildCount()-1;
        View lastView = layout.getChildAt(lastIndex);
        Float lastAmount = amounts.get(lastView);
        lastAmount = lastAmount == null?1:lastAmount;
        amount = lastAmount;
        while(!bottomRemoved && amount < 1 && (1-amount) > ITEMS_DIFF && maxDisplayedItem < nItems){
            View view = reuseView();
            //Fill view with.. maxDisplayedItem
            amount += ITEMS_DIFF;
            calculateImagePosition(view,amount);
            layout.addView(view);

            maxDisplayedItem++;
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


    private void init(){
        Utils.debug(getClass(),"INIT");

        float positions = INIT_POSITION;

        int elements = nItems;

        minDisplayedItem = 0;
        maxDisplayedItem = 0;
        while(elements > 0 && positions < 1){

            View view = reuseView();

            layout.addView(view);

            calculateImagePosition(view,positions);
            maxDisplayedItem++;
            elements--;
            positions+= ITEMS_DIFF;
        }
    }

    View reuseView(){
        View view = freeQeue.poll();
        if(view == null){
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(R.drawable.boton);
            view  = imageView;
        }

        return view;
    }
}