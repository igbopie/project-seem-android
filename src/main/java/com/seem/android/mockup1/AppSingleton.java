package com.seem.android.mockup1;

import com.seem.android.mockup1.model.Item;
import com.seem.android.mockup1.model.Seem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by igbopie on 13/03/14.
 */
public class AppSingleton {
    private static AppSingleton singleton = null;

    public static AppSingleton getInstance(){
        if(singleton == null){
            singleton = new AppSingleton();
        }
        return singleton;
    }


    private Item itemInProgress;

    public Item getItemInProgress() {
        return itemInProgress;
    }

    public void setItemInProgress(Item itemInProgress) {
        this.itemInProgress = itemInProgress;
    }

    private Map<String,Item> itemsDB = new HashMap<String, Item>();

    private Map<String,Seem> seemsDB = new HashMap<String, Seem>();

    private AppSingleton(){

    }

    public List<Item> findItemReplies(String parentItemId){
        List<Item> replies = new ArrayList<Item>();
        for(Item item:itemsDB.values()){
            if(item.getReplyTo() != null && item.getReplyTo().equals(parentItemId)){
                replies.add(item);
            }
        }
        Collections.sort(replies,new Comparator<Item>() {
            @Override
            public int compare(Item item, Item item2) {
                return item.getCreated().compareTo(item2.getCreated())*-1;
            }
        });
        return replies;
    }

    public Item findItemById(String id){
        return itemsDB.get(id);
    }

    public void saveItem(Item item){
        Item cached = itemsDB.get(item.getId());
        if(cached != null && cached.getImageLarge() != null){
            item.setImageLarge(cached.getImageLarge());
        }
        if(cached != null && cached.getImageLarge() != null){
            item.setImageThumb(cached.getImageThumb());
        }
        itemsDB.put(item.getId(),item);
    }

    public Seem findSeemById(String id){
        return seemsDB.get(id);
    }

    public void saveSeem(Seem seem){
        seemsDB.put(seem.getId(),seem);
    }

    public List<Seem> findSeems() {
        return new ArrayList<Seem>(this.seemsDB.values());
    }
}
