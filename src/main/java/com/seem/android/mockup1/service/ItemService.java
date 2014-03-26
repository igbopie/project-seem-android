package com.seem.android.mockup1.service;

import com.seem.android.mockup1.model.Item;
import com.seem.android.mockup1.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by igbopie on 26/03/14.
 */
public class ItemService {

    //TODO implement paging properly
    private final int MAX_ITEMS_PER_QUERY = 100;

    private static ItemService singleton = null;

    public static synchronized ItemService getInstance(){
        if(singleton == null){
            Utils.debug("Creating a new singleton... ");
            singleton = new ItemService();
        }
        return singleton;
    }
    private ItemService(){

    }

    private Map<String,Item> itemsDB = new HashMap<String, Item>();

    public Item findItemById(String id){
        Item item = itemsDB.get(id);
        if(item == null){
            Utils.debug("Cache miss item: "+id);
            item = Api.getItem(id);
            if(item == null){
                return null;
            }
            this.saveItem(item);
        }
        return item;
    }

    public List<Item> findItemReplies(String parentItemId){
        return this.findItemReplies(parentItemId,0);
    }
    public List<Item> findItemReplies(String parentItemId, int page){

        Item parentItem = findItemById(parentItemId);
        if(parentItem == null){
            Utils.debug("Parent not found");
            return null;
        }
        int replyCount = parentItem.getReplyCount();

        int fromItem = page * MAX_ITEMS_PER_QUERY;
        int toItem = Math.min(fromItem + MAX_ITEMS_PER_QUERY,replyCount);

        if(replyCount < fromItem){
            Utils.debug("Invalid page number");
            return null;
        }

        int returnSize = 1;
        //TODO This should be done in a internal db by a query

        List<Item>replies = new ArrayList<Item>();
        for(Item item:itemsDB.values()){
            if(item.getReplyTo() != null && item.getReplyTo().equals(parentItemId)){
                replies.add(item);
            }
        }

        for(int pageNumber = 0; replies.size() < toItem && returnSize > 0;pageNumber++){
            //We need to load more from the server!
            List<Item> apiReplies = Api.getReplies(parentItemId,pageNumber);
            if(apiReplies == null){
                Utils.debug("There was an error and replies could be retrieve");
                return null;
            }
            returnSize = apiReplies.size();
            //save replies
            saveItems(apiReplies);

            replies.clear();
            for(Item item:itemsDB.values()){
                if(item.getReplyTo() != null && item.getReplyTo().equals(parentItemId)){
                    replies.add(item);
                }
            }
        }

        Collections.sort(replies, new Comparator<Item>() {
            @Override
            public int compare(Item item, Item item2) {
                return item.getCreated().compareTo(item2.getCreated()) * -1;
            }
        });
        //We have to discard
        return replies.subList(fromItem,Math.min(toItem, replies.size()));
    }

    public Item reply(Item item){
        Item parentReply = findItemById(item.getReplyTo());
        if(parentReply == null){
            return null;
        }
        //It is important to do this before the call, that way,
        // either the parent item was loaded locally or remotelly
        // it will be the same
        parentReply.setReplyCount(parentReply.getReplyCount() + 1);

        Item reply = Api.reply(item.getCaption(),item.getMediaId(),item.getReplyTo());
        this.saveItem(reply);
        return reply;
    }

    private void saveItems(List<Item> items){
        for(Item item:items){
            this.saveItem(item);
        }
    }
    private void saveItem(Item item){
        Item cached = itemsDB.get(item.getId());
        if(cached != null && cached.getImageLarge() != null){
            item.setImageLarge(cached.getImageLarge());
        }
        if(cached != null && cached.getImageLarge() != null){
            item.setImageThumb(cached.getImageThumb());
        }
        itemsDB.put(item.getId(),item);
    }
}
