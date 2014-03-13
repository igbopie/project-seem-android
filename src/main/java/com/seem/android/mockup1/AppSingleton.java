package com.seem.android.mockup1;

import com.seem.android.mockup1.model.Reply;

import java.util.ArrayList;
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



    private int count=0;
    private Map<Integer,Reply> repliesDB = new HashMap<Integer, Reply>();

    private AppSingleton(){

    }

    public int getNewImageId(){
        int countAux = count;
        count++;
        return count;
    }


    public Reply findReplyById(int id){
        return repliesDB.get(id);
    }

    public void saveReply(Reply reply){
        repliesDB.put(reply.getId(),reply);
    }

}
