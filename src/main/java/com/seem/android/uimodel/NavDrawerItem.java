package com.seem.android.uimodel;

import com.seem.android.model.Item;

/**
 * Created by igbopie on 03/04/14.
 */
public class NavDrawerItem {

    private String title;
    private int icon;
    private String count = "0";
    // boolean to set visiblity of the counter
    private boolean isCounterVisible = false;
    private boolean isSectionTitle;
    private Item item;

    public NavDrawerItem(){}

    public NavDrawerItem(Item item){
        this.item = item;
    }
    public NavDrawerItem(String title,boolean isSectionTitle){
        this.title = title;
        this.isSectionTitle = isSectionTitle;
    }
    public NavDrawerItem(String title, int icon){
        this.title = title;
        this.icon = icon;
    }

    public NavDrawerItem(String title, int icon, boolean isCounterVisible, String count){
        this.title = title;
        this.icon = icon;
        this.isCounterVisible = isCounterVisible;
        this.count = count;
    }

    public String getTitle(){
        return this.title;
    }

    public int getIcon(){
        return this.icon;
    }

    public String getCount(){
        return this.count;
    }

    public boolean getCounterVisibility(){
        return this.isCounterVisible;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setIcon(int icon){
        this.icon = icon;
    }

    public void setCount(String count){
        this.count = count;
    }

    public void setCounterVisibility(boolean isCounterVisible){
        this.isCounterVisible = isCounterVisible;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public boolean isSectionTitle() {
        return isSectionTitle;
    }

    public void setSectionTitle(boolean isSectionTitle) {
        this.isSectionTitle = isSectionTitle;
    }
}