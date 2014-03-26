package com.seem.android.mockup1.service;

import com.seem.android.mockup1.model.Item;
import com.seem.android.mockup1.model.Seem;
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
public class SeemService {
    private static SeemService singleton = null;

    public static synchronized SeemService getInstance(){
        if(singleton == null){
            Utils.debug("Creating a new singleton... ");
            singleton = new SeemService();
        }
        return singleton;
    }
    private SeemService(){

    }


    private Map<String,Seem> seemsDB = new HashMap<String, Seem>();

    public Seem findSeemById(String id){
        Seem seem = seemsDB.get(id);
        if(seem == null){
            Utils.debug("Cache miss item: "+id);
            saveSeems(Api.getSeems());
            seem = seemsDB.get(id);
            if(seem == null){
                return null;
            }
        }
        return seem;
    }

    public List<Seem> findSeems() {
        if(seemsDB.size() == 0){
            saveSeems(Api.getSeems());
        }
        List<Seem> collection = new ArrayList<Seem>(this.seemsDB.values());
        Collections.sort(collection, new Comparator<Seem>() {
            @Override
            public int compare(Seem item, Seem item2) {
                return item.getCreated().compareTo(item2.getCreated()) * -1;
            }
        });
        return collection;
    }

    public Seem save(String title,String caption, String mediaId){
        Seem seem = Api.createSeem(title,caption,mediaId);
        this.saveSeem(seem);
        return seem;
    }

    private void saveSeems(List<Seem> seems){
        for(Seem seem:seems){
            this.saveSeem(seem);
        }
    }
    private void saveSeem(Seem seem){
        seemsDB.put(seem.getId(),seem);
    }

}
