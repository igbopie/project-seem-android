package com.seem.android.mockup1.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.seem.android.mockup1.Api;
import com.seem.android.mockup1.AppSingleton;
import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.activities.ReplyFlowActivity;
import com.seem.android.mockup1.model.Item;
import com.seem.android.mockup1.util.ActivityFactory;
import com.seem.android.mockup1.util.Utils;

import java.io.IOException;
import java.util.TimerTask;

/**
 * Created by igbopie on 20/03/14.
 */
public class ItemFullScreenFragment extends Fragment {
    Handler h=new Handler();


    public static ItemFullScreenFragment newInstance(String seemId,String itemId) {
        ItemFullScreenFragment f = new ItemFullScreenFragment();
        Bundle args = new Bundle();
        args.putString(GlobalVars.EXTRA_ITEM_ID, itemId);
        args.putString(GlobalVars.EXTRA_SEEM_ID, seemId);
        f.setArguments(args);
        return f;
    }

    //Timer myTimer = new Timer();
    ImageView image;
    ProgressBar progressBar;
    TextView captionTextView;
    ImageView nestedRepliesIndicator;
    TextView nestedRepliesIndicatorText;
    ImageView replyButton;
    //final int HIDE_TIMEOUT = 3000;
    private int mSystemUiVisibility =  View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utils.debug("onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_fullscreen_view, container, false);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getView().setSystemUiVisibility(mSystemUiVisibility);

        String itemId = getItemId();
        final Item item = AppSingleton.getInstance().findItemById(itemId);
        /*getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!getActivity().getActionBar().isShowing()){
                    getActivity().getActionBar().show();
                    myTimer.cancel();
                    myTimer = new Timer();
                    myTimer.schedule(new HideActionBar(), HIDE_TIMEOUT);
                }
            }
        });*/

        Utils.debug("OnActivityCreated");
        if (getActivity() != null &&
                getActivity().getActionBar() != null  &&
                getActivity().getActionBar().isShowing()){

            getActivity().getActionBar().hide();
        }

        image = (ImageView) getView().findViewById(R.id.imageView);
        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        captionTextView = (TextView) getView().findViewById(R.id.captionTextView);
        nestedRepliesIndicator = (ImageView) getView().findViewById(R.id.repliesIndicator);
        nestedRepliesIndicatorText = (TextView) getView().findViewById(R.id.repliesIndicatorNumber);
        nestedRepliesIndicatorText.setVisibility(View.INVISIBLE);
        nestedRepliesIndicator.setVisibility(View.INVISIBLE);
        nestedRepliesIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityFactory.startItemActivity(ItemFullScreenFragment.this.getActivity(), getSeemId(), getItemId());
            }
        });
        replyButton = (ImageView) getView().findViewById(R.id.cameraButton);
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityFactory.startReplyItemActivity(ItemFullScreenFragment.this.getActivity(),getItemId());
            }
        });

        //myTimer.schedule(new HideActionBar(), HIDE_TIMEOUT);

        //TODO don't show this on main items!
        if(item.getReplyCount() > 0){
            nestedRepliesIndicator.setVisibility(View.VISIBLE);
            nestedRepliesIndicatorText.setVisibility(View.VISIBLE);
            nestedRepliesIndicatorText.setText(item.getReplyCount()+"");
        }

        if(item != null && item.getImageThumb() != null){
            image.setImageDrawable(item.getImageThumb());
        }
        if(item != null){
            captionTextView.setText(item.getCaption());
        }


        new GetItem(itemId).execute();
        super.onActivityCreated(savedInstanceState);
    }

    public String getItemId() {
        return getArguments().getString(GlobalVars.EXTRA_ITEM_ID, null);
    }
    public String getSeemId() {
        return getArguments().getString(GlobalVars.EXTRA_SEEM_ID, null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.debug("ItemFullScreen - onActivityResult");
        if (requestCode == GlobalVars.RETURN_CODE_REPLY_TO_ITEM && resultCode == Activity.RESULT_OK) {
            Utils.debug("ItemFullScreen - Pic taken");
        }
    }

    private class GetItem extends AsyncTask<Void,Void,Item> {
        private final ProgressDialog dialog = new ProgressDialog(ItemFullScreenFragment.this.getView().getContext());

        private String itemId;

        public GetItem(String itemId){
            this.itemId = itemId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Item doInBackground(Void... voids) {
            try {
                Item item = AppSingleton.getInstance().findItemById(itemId);
                if(item == null) {
                    item = Api.getItem(itemId);
                }

                if(item.getImageLarge() == null) {
                    Api.downloadLargeImage(item);
                }
                return item;
            } catch (IOException e) {
                Utils.debug("Error",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Item result) {
            super.onPostExecute(result);
            image.setImageDrawable(result.getImageLarge());
            progressBar.setVisibility(View.INVISIBLE);
            captionTextView.setText(result.getCaption());

        }
    }

    class HideActionBar extends TimerTask {
        public void run() {
            h.post(new Runnable() {

                public void run() {

                    if (getActivity() != null &&
                        getActivity().getActionBar() != null  &&
                        getActivity().getActionBar().isShowing()){

                        getActivity().getActionBar().hide();
                    }
                }
            });

        }
    }


}
