package com.seem.android.mockup1.model;

import android.graphics.Bitmap;
import android.net.Uri;

import com.seem.android.mockup1.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by igbopie on 12/03/14.
 */
public class Reply extends Observable {
    private int id;
    private Uri imageUri;
    private Bitmap imageBitmap;
    private List<Reply> replyList = new ArrayList<Reply>();



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public List<Reply> getReplyList() {
        return replyList;
    }

    public void setReplyList(List<Reply> replyList) {
        this.replyList = replyList;
    }

    public void addReply(Reply reply){
        Utils.debug("Should notify observers from:"+reply.getId());
        this.getReplyList().add(reply);
        this.setChanged();
        this.notifyObservers();
    }



}
