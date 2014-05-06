package com.seem.android.model;

import android.net.Uri;

import java.util.Date;

/**
 * Created by igbopie on 18/03/14.
 */
public class Item {

    private String id;
    private String caption;
    private String mediaId;
    private Date created;
    private String replyTo;
    private String seemId;
    private int depth = 0;
    private int replyCount = 0;
    private String username;
    private String userId;
    private int favouriteCount = 0;
    private Boolean favourited;
    private int thumbUpCount = 0;
    private int thumbDownCount = 0;
    private int thumbScoreCount = 0;
    private Boolean thumbedUp;
    private Boolean thumbedDown;
    private Date favouritedDate;

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

    public String getSeemId() {
        return seemId;
    }

    public void setSeemId(String seemId) {
        this.seemId = seemId;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getFavouriteCount() {
        return favouriteCount;
    }

    public void setFavouriteCount(int favouriteCount) {
        this.favouriteCount = favouriteCount;
    }

    public Boolean isFavourited() {
        return favourited;
    }

    public void setFavourited(Boolean favourited) {
        this.favourited = favourited;
    }

    public Boolean getThumbedDown() {
        return thumbedDown;
    }

    public void setThumbedDown(Boolean thumbedDown) {
        this.thumbedDown = thumbedDown;
    }

    public Boolean getThumbedUp() {
        return thumbedUp;
    }

    public void setThumbedUp(Boolean thumbedUp) {
        this.thumbedUp = thumbedUp;
    }

    public int getThumbScoreCount() {
        return thumbScoreCount;
    }

    public void setThumbScoreCount(int thumbScoreCount) {
        this.thumbScoreCount = thumbScoreCount;
    }

    public int getThumbDownCount() {
        return thumbDownCount;
    }

    public void setThumbDownCount(int thumbDownCount) {
        this.thumbDownCount = thumbDownCount;
    }

    public int getThumbUpCount() {
        return thumbUpCount;
    }

    public void setThumbUpCount(int thumbUpCount) {
        this.thumbUpCount = thumbUpCount;
    }

    public Uri getTempLocalFile() {
        return tempLocalFile;
    }

    public void setTempLocalFile(Uri tempLocalFile) {
        this.tempLocalFile = tempLocalFile;
    }

    public Date getFavouritedDate() {
        return favouritedDate;
    }

    public void setFavouritedDate(Date favouritedDate) {
        this.favouritedDate = favouritedDate;
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

    @Override
    public String toString() {
        return "Item{" +
                "thumbedDown=" + thumbedDown +
                ", id='" + id + '\'' +
                ", caption='" + caption + '\'' +
                ", mediaId='" + mediaId + '\'' +
                ", created=" + created +
                ", replyTo='" + replyTo + '\'' +
                ", seemId='" + seemId + '\'' +
                ", depth=" + depth +
                ", replyCount=" + replyCount +
                ", username='" + username + '\'' +
                ", userId='" + userId + '\'' +
                ", favouriteCount=" + favouriteCount +
                ", favourited=" + favourited +
                ", thumbUpCount=" + thumbUpCount +
                ", thumbDownCount=" + thumbDownCount +
                ", thumbScoreCount=" + thumbScoreCount +
                ", thumbedUp=" + thumbedUp +
                '}';
    }
}
