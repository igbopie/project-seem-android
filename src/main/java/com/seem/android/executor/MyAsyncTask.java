package com.seem.android.executor;

import android.os.AsyncTask;

/**
 * Created by igbopie on 26/03/14.
 */
public abstract class MyAsyncTask extends AsyncTask<Void,Void,Void> {

    enum MyStatus{PENDING,RUNNING}
    private MyStatus myStatus = MyStatus.PENDING;
    private AsyncExecutor parent;


    public AsyncExecutor getParent() {
        return parent;
    }

    public void setParent(AsyncExecutor parent) {
        this.parent = parent;
    }

    public MyStatus getMyStatus() {
        return myStatus;
    }

    public void setMyStatus(MyStatus myStatus) {
        this.myStatus = myStatus;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        parent.finished(this);
    }
}
