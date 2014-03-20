package com.seem.android.mockup1.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by igbopie on 18/03/14.
 */
public class Item {

    private int replyCount;
    private String id;
    private String caption;
    private String mediaId;
    private Date created;
    private String replyTo;

    private Uri tempLocalFile;
    private Bitmap tempLocalBitmap;


    private Drawable imageLarge;
    private Drawable imageThumb;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }


    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
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



    public Uri getTempLocalFile() {
        return tempLocalFile;
    }

    public void setTempLocalFile(Uri tempLocalFile) {
        this.tempLocalFile = tempLocalFile;
    }


    public Bitmap getTempLocalBitmap() {
        return tempLocalBitmap;
    }

    public void setTempLocalBitmap(Bitmap tempLocalBitmap) {
        this.tempLocalBitmap = tempLocalBitmap;
    }

    @Override
    public String toString() {
        return "Item{" +
                "created=" + created +
                ", mediaId='" + mediaId + '\'' +
                ", caption='" + caption + '\'' +
                ", id='" + id + '\'' +
                ", replyCount=" + replyCount +
                '}';
    }
}
