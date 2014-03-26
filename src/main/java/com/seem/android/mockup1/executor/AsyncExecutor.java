package com.seem.android.mockup1.executor;

import com.seem.android.mockup1.util.Utils;

import java.util.Stack;

/**
 * Created by igbopie on 26/03/14.
 */
public class AsyncExecutor  {

    private static AsyncExecutor instance = null;
    public static synchronized AsyncExecutor getInstance(){
        if(instance == null){
            instance = new AsyncExecutor();
        }
        return instance;
    }

    private Stack<MyAsyncTask> qeue = new Stack<MyAsyncTask>();

    private MyAsyncTask currentTask;

    public synchronized void cancelTask(MyAsyncTask task){
        qeue.remove(task);
    }

    public synchronized void next(){
        if(currentTask == null && qeue.size() > 0) {
            Utils.debug("Executing a new task");
            currentTask = qeue.pop();
            currentTask.setParent(this);
            currentTask.execute();
        }
    }
    public void add(MyAsyncTask myAsyncTask){
        Utils.debug("Adding a new task");
        qeue.push(myAsyncTask);
        next();
    }
    protected synchronized void finished(MyAsyncTask myAsyncTask) {
        Utils.debug("Finished");
        if(myAsyncTask == currentTask){
            currentTask.setParent(null);
            currentTask = null;
        }
        next();
    }


}
