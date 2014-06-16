package com.seem.android.model;

import android.net.Uri;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by igbopie on 18/03/14.
 */
public class Item implements Serializable{

    private String id;
    private String caption;
    private String mediaId;
    private Date created;
    private String seemId;
    private String userId;
    private String replyTo;

    private UserProfile userProfile;

    private Uri tempLocalFile;

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


    public String getSeemId() {
        return seemId;
    }

    public void setSeemId(String seemId) {
        this.seemId = seemId;
    }


    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", caption='" + caption + '\'' +
                ", mediaId='" + mediaId + '\'' +
                ", created=" + created +
                ", seemId='" + seemId + '\'' +
                ", userId='" + userId + '\'' +
                ", replyTo='" + replyTo + '\'' +
                ", userProfile=" + userProfile +
                ", tempLocalFile=" + tempLocalFile +
                '}';
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        if(!replyTo.equalsIgnoreCase("null")) {
            this.replyTo = replyTo;
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public Uri getTempLocalFile() {
        return tempLocalFile;
    }

    public void setTempLocalFile(Uri tempLocalFile) {
        this.tempLocalFile = tempLocalFile;
    }


    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Item && ((Item) o).getId() != null && this.getId() != null){
            Item other = (Item)o;
            return this.getId().equals(other.getId());

        } else {
            return super.equals(o);
        }
    }

}
