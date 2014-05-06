package com.seem.android.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seem.android.R;

/**
 * Created by igbopie on 20/03/14.
 */
public class SpinnerImageView extends RelativeLayout {

    ProgressBar progressBar;
    TextView textView;
    ImageView repliesIndicator;
    TextView repliesIndicatorNumber;
    ImageView depthIcon;
    TextView depthNumber;


    public SpinnerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, new int[]{}, 0, 0);

        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_square_image, this, true);

        getImageView().setBackgroundColor(Color.LTGRAY);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.textView);
        textView.setVisibility(INVISIBLE);
        repliesIndicator = (ImageView) findViewById(R.id.repliesIndicator);
        repliesIndicatorNumber = (TextView) findViewById(R.id.repliesIndicatorNumber);
        repliesIndicatorNumber.setVisibility(INVISIBLE);
        repliesIndicator.setVisibility(INVISIBLE);
        depthIcon = (ImageView) findViewById(R.id.depthIconView);
        depthNumber = (TextView) findViewById(R.id.depthNumber);
        depthIcon.setVisibility(INVISIBLE);
        depthNumber.setVisibility(INVISIBLE);
    }

    public void setLoading(boolean loading){
        if(loading){
            progressBar.setVisibility(VISIBLE);
        }else{
            progressBar.setVisibility(INVISIBLE);
        }
    }
    public ImageView getImageView(){
        return (ImageView) findViewById(R.id.componentImageView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }
    public void setText(String text){
        if(text == null || text.trim().length() == 0){
            this.textView.setVisibility(INVISIBLE);
        } else {
            this.textView.setText(text);
            this.textView.setVisibility(VISIBLE);
        }
    }

    public void setRepliesNumber(int hasReplies){
        if(hasReplies>0){
            this.repliesIndicatorNumber.setText(""+hasReplies);
            this.repliesIndicatorNumber.setVisibility(VISIBLE);
            this.repliesIndicator.setVisibility(VISIBLE);
        } else {
            this.repliesIndicatorNumber.setVisibility(INVISIBLE);
            this.repliesIndicator.setVisibility(INVISIBLE);
        }
    }

    public void setDepthNumber(int depth){
        if(depth>0){
            this.depthNumber.setText(""+depth);
            this.depthNumber.setVisibility(VISIBLE);
            this.depthIcon.setVisibility(VISIBLE);
        } else {
            this.depthNumber.setVisibility(INVISIBLE);
            this.depthIcon.setVisibility(INVISIBLE);
        }
    }
    public void setViewRepliesOnClick(OnClickListener l){
        this.repliesIndicator.setOnClickListener(l);
    }

    public void setViewThreadOnClick(OnClickListener l){
        this.depthIcon.setOnClickListener(l);
    }



}
