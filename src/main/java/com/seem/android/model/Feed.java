package com.seem.android.model;

import java.util.Date;

/**
 * Created by igbopie on 10/04/14.
 */
public class Feed {

    public enum FeedAction {
        REPLY_TO("replyTo"),
        CREATE_SEEM("createSeem"),
        FAVOURITE("favourite")
        ;
        /**
         * @param text
         */
        private FeedAction(final String text) {
            this.text = text;
        }

        private final String text;

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }

        public static FeedAction getEnum(String value) {
            for(FeedAction v : values())
                if(v.toString().equalsIgnoreCase(value.toString())) return v;
            throw new IllegalArgumentException();
        }
    }

    private String id;
    private Date created;
    private String itemId;
    private String itemMediaId;
    private String itemCaption;
    private  String replyToId;
    private String replyToMediaId;
    private String replyToCaption;
    private String replyToUsername;
    private String replyToUserId;
    private String seemId;
    private String seemTitle;
    private FeedAction action;
    private String userId;
    private String username;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemMediaId() {
        return itemMediaId;
    }

    public void setItemMediaId(String itemMediaId) {
        this.itemMediaId = itemMediaId;
    }

    public String getItemCaption() {
        return itemCaption;
    }

    public void setItemCaption(String itemCaption) {
        this.itemCaption = itemCaption;
    }

    public String getReplyToId() {
        return replyToId;
    }

    public void setReplyToId(String replyToId) {
        this.replyToId = replyToId;
    }

    public String getReplyToMediaId() {
        return replyToMediaId;
    }

    public void setReplyToMediaId(String replyToMediaId) {
        this.replyToMediaId = replyToMediaId;
    }

    public String getReplyToCaption() {
        return replyToCaption;
    }

    public void setReplyToCaption(String replyToCaption) {
        this.replyToCaption = replyToCaption;
    }

    public String getReplyToUsername() {
        return replyToUsername;
    }

    public void setReplyToUsername(String replyToUsername) {
        this.replyToUsername = replyToUsername;
    }

    public String getReplyToUserId() {
        return replyToUserId;
    }

    public void setReplyToUserId(String replyToUserId) {
        this.replyToUserId = replyToUserId;
    }

    public String getSeemId() {
        return seemId;
    }

    public void setSeemId(String seemId) {
        this.seemId = seemId;
    }

    public String getSeemTitle() {
        return seemTitle;
    }

    public void setSeemTitle(String seemTitle) {
        this.seemTitle = seemTitle;
    }

    public FeedAction getAction() {
        return action;
    }

    public void setAction(FeedAction action) {
        this.action = action;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Feed feed = (Feed) o;

        if (!id.equals(feed.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
