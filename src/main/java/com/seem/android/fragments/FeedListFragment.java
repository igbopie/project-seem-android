package com.seem.android.fragments;

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

import com.seem.android.GlobalVars;
import com.seem.android.MyApplication;
import com.seem.android.R;
import com.seem.android.adapters.FeedAdapter;
import com.seem.android.model.Feed;
import com.seem.android.service.Api;
import com.seem.android.util.ActivityFactory;
import com.seem.android.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igbopie on 03/04/14.
 */
public class FeedListFragment extends ListFragment {

    private List<Feed> feedList = new ArrayList<Feed>();
    private FeedAdapter adapter;
    private int page = 0;
    private boolean waiting = false;
    private boolean moreItems = true;

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

        adapter = new FeedAdapter(feedList,this.getActivity());
        setListAdapter(adapter);

        adapter.setFetchLastItemListener(new FeedAdapter.FetchLastItemListener() {
            @Override
            public void lastItemFetched() {
                //fetchmore...
                if(!waiting && moreItems) {
                    waiting = true;
                    new GetFeedTask(page).execute();
                    page++;
                }
            }
        });


        super.onActivityCreated(savedInstanceState);


        new GetFeedTask(page).execute();

        page++;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        // Do something when a list item is clicked
        Feed feed = adapter.getItem(position);
        if(feed.getAction() == Feed.FeedAction.CREATE_SEEM){
            onItemClickListener.onClick(feed.getSeemId(), feed.getItemId());
        }else {
            ActivityFactory.startThreadedActivity(FeedListFragment.this.getActivity(), feed.getItemId());
        }
        //Utils.debug(this.getClass(),"Item Clicked! seem "+seem);
        //ActivityFactory.startItemActivity(this.getActivity(), seem.getId(), seem.getItemId());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
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
                new GetFeedTask(0).execute();
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
            new GetFeedTask(0).execute();
        }
    }




    private class GetFeedTask extends AsyncTask<Void,Void,List<Feed>> {
        private final ProgressDialog dialog = new ProgressDialog(FeedListFragment.this.getActivity());
        private int page = 0;

        private GetFeedTask(int page) {
            this.page = page;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Loading...");
            dialog.show();
            //Descomentar para hacer que rote el refresh...
            /* Attach a rotating ImageView to the refresh item as an ActionView */
            /*LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ImageView iv = (ImageView) inflater.inflate(R.layout.component_refresh_action_view, null);

            Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
            rotation.setRepeatCount(Animation.INFINITE);
            iv.startAnimation(rotation);

            refreshItem.setActionView(iv);*/
        }

        @Override
        protected List<Feed> doInBackground(Void... voids) {
            List<Feed> feed = Api.getFeeds(MyApplication.getToken(),page);
            return feed;
        }

        @Override
        protected void onPostExecute(List<Feed> result) {
            super.onPostExecute(result);

            dialog.dismiss();
            if(result != null) {
                for (Feed feed : result) {
                    if (!feedList.contains(feed)) {
                        feedList.add(feed);
                    }
                }
                if (result.size() == 0) {
                    moreItems = false;
                }
                waiting = false;
                adapter.notifyDataSetChanged();
            }else{
                Utils.dialog("Conection problems?","Try again",getActivity());
            }

            /*
            refreshItem.getActionView().clearAnimation();
            refreshItem.setActionView(null);
            */
            //getListView().setSelection(0);

        }
    }


    private OnItemClickListener onItemClickListener;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            onItemClickListener = (OnItemClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement UserProfileInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onItemClickListener = null;
    }

    public interface OnItemClickListener {
        public void onClick(String seemId,String itemId);
    }

}