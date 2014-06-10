package com.seem.android.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by igbopie on 13/03/14.
 */
public class Seem {
    private String id;
    private String title;
    private Date created;
    private int itemCount;
    private Date updated;
    private Date expire;

    private List<Item> lastestItems = new ArrayList<Item>();

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


    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public Date getUpdated() {
        return updated;
    }

    public List<Item> getLastestItems() {
        return lastestItems;
    }

    public void setLastestItems(List<Item> lastestItems) {
        this.lastestItems = lastestItems;
    }

    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
    }

    @Override
    public String toString() {
        return "Seem{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", created=" + created +
                ", itemCount=" + itemCount +
                ", updated=" + updated +
                ", expire=" + expire +
                ", lastestItems=" + lastestItems +
                '}';
    }
}
