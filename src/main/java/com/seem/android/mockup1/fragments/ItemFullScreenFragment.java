package com.seem.android.mockup1.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Fragment;
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
import com.seem.android.mockup1.service.Api;
import com.seem.android.mockup1.service.ItemService;
import com.seem.android.mockup1.util.ActivityFactory;
import com.seem.android.mockup1.util.Utils;

import java.util.HashMap;
import java.util.Map;
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
    ProgressBar progressBar;

    TextView captionTextView;
    TextView datePostedTextView;

    Item item;


    IconTextView thumbUpIconTextView;
    IconTextView thumbDownIconTextView;
    IconTextView favouritesIconTextView;
    IconTextView replyIconTextView;
    IconTextView depthIconTextView;

    IconTextView userIconTextView;
    IconTextView closeIconTextView;


    GetItem getItemTask;
    DownloadAsyncTask downloadAsyncTask;

    //Actions
    ImageView replyButton;
    ImageView favActionImageView;
    ImageView thumbUpActionImageView;
    ImageView thumbDownActionImageView;
    ImageView shareActionImageView;

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

        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        captionTextView = (TextView) getView().findViewById(R.id.captionTextView);

        favouritesIconTextView = (IconTextView) getView().findViewById(R.id.favouritesIconTextView);
        replyIconTextView = (IconTextView) getView().findViewById(R.id.replyIconTextView);
        depthIconTextView = (IconTextView) getView().findViewById(R.id.depthIconTextView);
        userIconTextView = (IconTextView) getView().findViewById(R.id.userIconTextView);
        datePostedTextView = (TextView) getView().findViewById(R.id.datePostedTextView);
        closeIconTextView =(IconTextView) getView().findViewById(R.id.closeIconTextView);
        thumbDownIconTextView =(IconTextView) getView().findViewById(R.id.thumbsDownIconTextView);
        thumbUpIconTextView =(IconTextView) getView().findViewById(R.id.thumbsUpIconTextView);

        favActionImageView = (ImageView) getView().findViewById(R.id.favActionImageView);
        thumbUpActionImageView = (ImageView) getView().findViewById(R.id.thumbUpIconView);
        thumbDownActionImageView = (ImageView) getView().findViewById(R.id.thumbDownIconView);
        shareActionImageView= (ImageView) getView().findViewById(R.id.shareActionImageView);
        replyButton = (ImageView) getView().findViewById(R.id.cameraButton);

        topBar = getView().findViewById(R.id.topBar);
        captionBar = getView().findViewById(R.id.captionBar);
        userBar = getView().findViewById(R.id.userBar);

        favActionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.isFavourited() != null && item.isFavourited()) {
                    new UnfavouriteTask().execute();
                }else{
                    new FavouriteTask().execute();
                }
            }
        });

        thumbUpActionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.getThumbedUp() != null && item.getThumbedUp()) {
                    new ThumbClearTask().execute();
                }else{
                    new ThumbUpTask().execute();
                }
            }
        });

        thumbDownActionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.getThumbedDown() != null && item.getThumbedDown()) {
                    new ThumbClearTask().execute();
                }else{
                    new ThumbDownTask().execute();
                }
            }
        });


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
                Map<String,String>data = new HashMap<String, String>();
                data.put(GlobalVars.EXTRA_SEEM_ID,getSeemId());
                data.put(GlobalVars.EXTRA_ITEM_ID,getItemId());
                ActivityFactory.finishActivityWithData(getActivity(),data,Activity.RESULT_OK);
            }
        });
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(ItemFullScreenFragment.this.getActivity(), view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.camera_popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.actionPopupCamera:
                                ActivityFactory.startReplyItemActivity(ItemFullScreenFragment.this,item.getId(), GlobalVars.PhotoSource.CAMERA);
                                return true;
                            case R.id.actionPopupGallery:
                                ActivityFactory.startReplyItemActivity(ItemFullScreenFragment.this,item.getId(), GlobalVars.PhotoSource.GALLERY);
                                return true;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
        shareActionImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(ItemFullScreenFragment.this.getActivity(), view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.share_popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.actionCopyLink:
                                String link = "http://seem-test.herokuapp.com/item/"+item.getId();

                                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", link);
                                clipboard.setPrimaryClip(clip);
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

    private ImageView getImageView(){
        return (ImageView) getView().findViewById(R.id.imageView);
    }
    private class GetItem extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            //For now lets ask for the real thing
            item = ItemService.getInstance().findItemById(getItemId(),true,true);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (item != null)
            {
                thumbDownIconTextView.setText(item.getThumbDownCount() + "");
                thumbUpIconTextView.setText(item.getThumbUpCount() + "");
                favouritesIconTextView.setText(item.getFavouriteCount() + "");

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

                if(item.isFavourited() != null && item.isFavourited()){
                    favActionImageView.setImageResource(R.drawable.star);
                } else {
                    favActionImageView.setImageResource(R.drawable.star_o);
                }

                if(item.getThumbedUp() != null && item.getThumbedUp()){
                    thumbUpActionImageView.setImageResource(R.drawable.thumbs_up);
                } else {
                    thumbUpActionImageView.setImageResource(R.drawable.thumbs_o_up);
                }

                if(item.getThumbedDown() != null && item.getThumbedDown()){
                    thumbDownActionImageView.setImageResource(R.drawable.thumbs_down);
                } else {
                    thumbDownActionImageView.setImageResource(R.drawable.thumbs_o_down);
                }

                //First Thumb
                downloadAsyncTask = new DownloadAsyncTask(item,getImageView(),true){
                    @Override
                    protected void onPostExecute(Void result) {
                        super.onPostExecute(result);
                        if(!this.isCancelled()) {
                            //Then large
                            downloadAsyncTask = new DownloadAsyncTask(item, getImageView(), false) {
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


    class FavouriteTask extends ActionTask{
        @Override
        protected Boolean doAction() {
            return Api.favourite(item.getId(),MyApplication.getToken());
        }


    }

    class UnfavouriteTask extends ActionTask{

        @Override
        protected Boolean doAction() {
            return Api.unfavourite(item.getId(),MyApplication.getToken());
        }

    }
    class ThumbUpTask extends ActionTask{

        @Override
        protected Boolean doAction() {
            return Api.thumbUp(item.getId(),MyApplication.getToken());
        }

    }
    class ThumbDownTask extends ActionTask{

        @Override
        protected Boolean doAction() {
            return Api.thumbDown(item.getId(),MyApplication.getToken());
        }

    }
    class ThumbClearTask extends ActionTask{

        @Override
        protected Boolean doAction() {
            return Api.thumbClear(item.getId(),MyApplication.getToken());
        }

    }

    abstract class ActionTask extends AsyncTask<Void,Void,Boolean>{


        private final ProgressDialog dialog = new ProgressDialog(getActivity());

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            if(!success){
                dialog.dismiss();
                Utils.dialog("Error","Action could not be completed, check connection and try again later.",getActivity());
            }else{
                getItemTask = new GetItem(){
                    @Override
                    protected void onPostExecute(Void result) {
                        super.onPostExecute(result);
                        dialog.dismiss();
                    }
                };
                getItemTask.execute();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return doAction();
        }

        abstract protected Boolean doAction();


    }



}
