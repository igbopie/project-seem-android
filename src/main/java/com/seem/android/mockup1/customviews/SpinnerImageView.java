package com.seem.android.mockup1.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.seem.android.mockup1.R;

import org.w3c.dom.Text;

/**
 * Created by igbopie on 20/03/14.
 */
public class SpinnerImageView extends LinearLayout {
    SquareImageView imageView;
    ProgressBar progressBar;
    TextView textView;

    public SpinnerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, new int[]{}, 0, 0);

        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.square_image_component, this, true);
        imageView = (SquareImageView) findViewById(R.id.componentImageView);
        imageView.setBackgroundColor(Color.LTGRAY);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.textView);
        textView.setVisibility(INVISIBLE);

    }

    public void setLoading(boolean loading){
        if(loading){
            progressBar.setVisibility(VISIBLE);
        }else{
            progressBar.setVisibility(INVISIBLE);
        }
    }
    public ImageView getImageView(){
        return imageView;
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
}
