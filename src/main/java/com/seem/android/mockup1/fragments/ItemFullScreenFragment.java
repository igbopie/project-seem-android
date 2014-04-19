package com.seem.android.mockup1.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.seem.android.mockup1.asynctask.DownloadAsyncTask;
import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.MyApplication;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.customviews.IconTextView;
import com.seem.android.mockup1.model.Item;
import com.seem.android.mockup1.service.ItemService;
import com.seem.android.mockup1.util.ActivityFactory;
import com.seem.android.mockup1.util.Utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by igbopie on 20/03/14.
 */
public class ItemFullScreenFragment extends Fragment {
    Handler h=new Handler();


    public static ItemFullScreenFragment newInstance(String seemId,String itemId,boolean isMainItem) {
        ItemFullScreenFragment f = new ItemFullScreenFragment();
        Bundle args = new Bundle();
        args.putString(GlobalVars.EXTRA_ITEM_ID, itemId);
        args.putString(GlobalVars.EXTRA_SEEM_ID, seemId);
        args.putBoolean(GlobalVars.EXTRA_IS_MAIN_ITEM,isMainItem);
        f.setArguments(args);
        return f;
    }

    //Bars
    View topBar;
    View userBar;
    View captionBar;
    boolean showingBars = true;
    Timer myTimer = new Timer();
    final int HIDE_TIMEOUT = 3000;



    //
    ImageView image;
    ProgressBar progressBar;

    TextView captionTextView;
    TextView datePostedTextView;

    ImageView replyButton;
    Item item;

    IconTextView replyIconTextView;
    IconTextView depthIconTextView;

    IconTextView userIconTextView;
    IconTextView closeIconTextView;


    GetItem getItemTask;
    DownloadAsyncTask downloadAsyncTask;

    private int mSystemUiVisibility =  View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utils.debug(this.getClass(),"onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_fullscreen_view, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getView().setSystemUiVisibility(mSystemUiVisibility);

        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!showingBars){
                    showBars();
                    myTimer.cancel();
                    myTimer = new Timer();
                    myTimer.schedule(new HideActionBar(), HIDE_TIMEOUT);
                }
            }
        });

        Utils.debug(this.getClass(),"OnActivityCreated");
        if (getActivity() != null &&
                getActivity().getActionBar() != null  &&
                getActivity().getActionBar().isShowing()){

            getActivity().getActionBar().hide();
        }

        image = (ImageView) getView().findViewById(R.id.imageView);
        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        captionTextView = (TextView) getView().findViewById(R.id.captionTextView);
        replyIconTextView = (IconTextView) getView().findViewById(R.id.replyIconTextView);

        depthIconTextView = (IconTextView) getView().findViewById(R.id.depthIconTextView);
        userIconTextView = (IconTextView) getView().findViewById(R.id.userIconTextView);
        datePostedTextView = (TextView) getView().findViewById(R.id.datePostedTextView);
        closeIconTextView =(IconTextView) getView().findViewById(R.id.closeIconTextView);


        topBar = getView().findViewById(R.id.topBar);
        captionBar = getView().findViewById(R.id.captionBar);
        userBar = getView().findViewById(R.id.userBar);


        closeIconTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityFactory.finishActivity(getActivity(),Activity.RESULT_OK);
            }
        });


        userIconTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityFactory.startUserProfileActivity(ItemFullScreenFragment.this,userIconTextView.getText().toString());
            }
        });

        replyIconTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityFactory.startItemActivity(ItemFullScreenFragment.this.getActivity(), getSeemId(), getItemId());
            }
        });
        replyButton = (ImageView) getView().findViewById(R.id.cameraButton);
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.debug(this.getClass(),"Action camera!");
                PopupMenu popup = new PopupMenu(ItemFullScreenFragment.this.getActivity(), view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.camera_popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.actionPopupCamera:
                                Utils.debug(this.getClass(), "NEW SEEM!");
                                ActivityFactory.startReplyItemActivity(ItemFullScreenFragment.this,item.getId(), GlobalVars.PhotoSource.CAMERA);
                                return true;
                            case R.id.actionPopupGallery:
                                Utils.debug(this.getClass(), "NEW SEEM!");
                                ActivityFactory.startReplyItemActivity(ItemFullScreenFragment.this,item.getId(), GlobalVars.PhotoSource.GALLERY);
                                return true;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
        if(!MyApplication.isLoggedIn()){
            replyButton.setVisibility(View.INVISIBLE);
        }

        myTimer.schedule(new HideActionBar(), HIDE_TIMEOUT);
        getItemTask = new GetItem();
        getItemTask.execute();
        super.onActivityCreated(savedInstanceState);
    }

    public String getItemId() {
        return getArguments().getString(GlobalVars.EXTRA_ITEM_ID, null);
    }
    public String getSeemId() {
        return getArguments().getString(GlobalVars.EXTRA_SEEM_ID, null);
    }
    public boolean isMainItem() {
        return getArguments().getBoolean(GlobalVars.EXTRA_IS_MAIN_ITEM, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.debug(this.getClass(),"ItemFullScreen - onActivityResult");
        if (requestCode == GlobalVars.RETURN_CODE_REPLY_TO_ITEM && resultCode == Activity.RESULT_OK) {
            Utils.debug(this.getClass(),"ItemFullScreen - Pic taken");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.debug(getClass(),"OnDestroy");
        if(getItemTask != null){
            getItemTask.cancel(true);
        }
        if(downloadAsyncTask != null) {
            downloadAsyncTask.cancel(true);
        }
    }

    private class GetItem extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            item = ItemService.getInstance().findItemById(getItemId());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (item != null)
            {
                replyIconTextView.setText(item.getReplyCount() + "");
                depthIconTextView.setText(item.getDepth()+"");
                if(item.getDepth() > 0){
                    depthIconTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityFactory.startThreadedActivity(getActivity(),item.getId());
                        }
                    });
                }

                if(item.getUsername() != null){
                    userIconTextView.setText(item.getUsername());
                }else{
                    userIconTextView.setText("Anonymous");
                }
                captionTextView.setText(item.getCaption());
                datePostedTextView.setText(Utils.getRelativeTime(item.getCreated()));

                progressBar.setVisibility(View.VISIBLE);
                //First Thumb
                downloadAsyncTask = new DownloadAsyncTask(item,image,true){
                    @Override
                    protected void onPostExecute(Void result) {
                        super.onPostExecute(result);
                        if(!this.isCancelled()) {
                            //Then large
                            downloadAsyncTask = new DownloadAsyncTask(item, image, false) {
                                @Override
                                protected void onPostExecute(Void result) {
                                    super.onPostExecute(result);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    downloadAsyncTask = null;
                                }
                            };
                            downloadAsyncTask.execute();
                        }
                    }
                };
                downloadAsyncTask.execute();
            }
            getItemTask = null;
        }
    }

    private void hideBars(){
        if(showingBars) {
            showingBars = false;

            Animation fadeoutAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_fade_out);

            topBar.setVisibility(View.INVISIBLE);
            userBar.setVisibility(View.INVISIBLE);
            captionBar.setVisibility(View.INVISIBLE);

            topBar.startAnimation(fadeoutAnim);
            userBar.startAnimation(fadeoutAnim);
            captionBar.startAnimation(fadeoutAnim);

        }

    }

    private void showBars(){
        showingBars = true;
        topBar.setVisibility(View.VISIBLE);
        userBar.setVisibility(View.VISIBLE);
        captionBar.setVisibility(View.VISIBLE);
    }

    class HideActionBar extends TimerTask {
        public void run() {
            h.post(new Runnable() {

                public void run() {

                    if (getActivity() != null){
                        hideBars();
                    }
                }
            });

        }
    }




}
