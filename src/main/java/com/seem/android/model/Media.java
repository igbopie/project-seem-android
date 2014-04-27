package com.seem.android.model;

import android.graphics.drawable.Drawable;

/**
 * Created by igbopie on 02/04/14.
 */
public class Media {
    private String id;


    private Drawable imageLarge;
    private Drawable imageThumb;

    public Media(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Drawable getImageLarge() {
        return imageLarge;
    }

    public void setImageLarge(Drawable imageLarge) {
        this.imageLarge = imageLarge;
    }

    public Drawable getImageThumb() {
        return imageThumb;
    }

    public void setImageThumb(Drawable imageThumb) {
        this.imageThumb = imageThumb;
    }
}
