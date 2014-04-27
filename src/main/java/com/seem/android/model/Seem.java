package com.seem.android.model;

import java.util.Date;

/**
 * Created by igbopie on 13/03/14.
 */
public class Seem {
    private String id;
    private String title;
    private String itemId;
    private Date created;
    private int itemCount;
    private Date updated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    @Override
    public String toString() {
        return "Seem{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", itemId='" + itemId + '\'' +
                ", created=" + created +
                '}';
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getUpdated() {
        return updated;
    }
}
