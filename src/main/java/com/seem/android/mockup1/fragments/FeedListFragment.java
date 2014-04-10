package com.seem.android.mockup1.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupMenu;

import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.MyApplication;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.adapters.FeedAdapter;
import com.seem.android.mockup1.model.Feed;
import com.seem.android.mockup1.model.Seem;
import com.seem.android.mockup1.service.Api;
import com.seem.android.mockup1.service.SeemService;
import com.seem.android.mockup1.util.ActivityFactory;
import com.seem.android.mockup1.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igbopie on 03/04/14.
 */
public class FeedListFragment extends ListFragment {


    private FeedAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utils.debug(this.getClass(), "onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        new GetFeedTask().execute();
        adapter = new FeedAdapter(new ArrayList<Feed>(),this.getActivity());
        setListAdapter(adapter);


        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Do something when a list item is clicked
        //Seem seem = adapter.getItem(position);
        //Utils.debug(this.getClass(),"Item Clicked! seem "+seem);
        //ActivityFactory.startItemActivity(this.getActivity(), seem.getId(), seem.getItemId());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.seem_view, menu);
        MenuItem menuItem = menu.findItem(R.id.action_camera);
        if(!MyApplication.isLoggedIn()){
            menuItem.setVisible(false);
        }

        super.onCreateOptionsMenu(menu,inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_camera:
                PopupMenu popup = new PopupMenu(this.getActivity(), getActivity().findViewById(R.id.action_camera));
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.camera_popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.actionPopupCamera:
                                Utils.debug(this.getClass(), "NEW SEEM!");
                                ActivityFactory.startCreateSeemActivity(FeedListFragment.this.getActivity(), GlobalVars.PhotoSource.CAMERA);
                                return true;
                            case R.id.actionPopupGallery:
                                Utils.debug(this.getClass(), "NEW SEEM!");
                                ActivityFactory.startCreateSeemActivity(FeedListFragment.this.getActivity(), GlobalVars.PhotoSource.GALLERY);
                                return true;
                        }
                        return false;
                    }
                });
                popup.show();
                return true;

            case R.id.action_refresh:
                //newGame();
                Utils.debug(this.getClass(),"Refresh Seems!");
                new GetFeedTask().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GlobalVars.RETURN_CODE_CREATE_SEEM && resultCode == Activity.RESULT_OK) {
            //Utils.debug(this.getClass(),"Seem created!");
            new GetFeedTask().execute();
        }
    }



    private class GetFeedTask extends AsyncTask<Void,Void,List<Feed>> {
        private final ProgressDialog dialog = new ProgressDialog(FeedListFragment.this.getActivity());

        private GetFeedTask() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Downloading seems...");
            dialog.show();
        }

        @Override
        protected List<Feed> doInBackground(Void... voids) {
            List<Feed> feed = Api.getFeeds(MyApplication.getToken(),0);
            return feed;
        }

        @Override
        protected void onPostExecute(List<Feed> result) {
            super.onPostExecute(result);
            adapter.setItemList(result);
            adapter.notifyDataSetChanged();
            dialog.dismiss();
        }
    }
}
