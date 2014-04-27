package com.seem.android.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seem.android.R;


/**
 * TODO: document your custom view class.
 */
public class IconTextView extends LinearLayout {

    TextView textView;
    ImageView imageView;


    public IconTextView(Context context) {
        super(context);
        initializeLayout(context);
    }

    public IconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeLayout(context);
        initValues(context, attrs);
    }

    public IconTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initializeLayout(context);
        initValues(context, attrs);
    }

    private void initializeLayout(Context context) {
        setOrientation(HORIZONTAL);
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_icon_text_view, this);

        this.textView = (TextView) findViewById(R.id.textView);
        this.imageView =(ImageView)findViewById(R.id.imageView);
    }

    private void initValues(Context context, AttributeSet attr) {
        //final TypedArray a = context.obtainStyledAttributes(attr, R.styleable.IconTextView);
        String text =  attr.getAttributeValue("http://schemas.android.com/apk/res/android", "text");
        int resource = attr.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "src", 0);

        this.setText(text);
        this.imageView.setImageResource(resource);
    }

    public void setText(CharSequence text){
        if(textView != null && text != null) {
            this.textView.setText(text);
        }
    }

    public CharSequence getText(){
        return this.textView.getText();
    }
}
