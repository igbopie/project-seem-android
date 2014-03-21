package com.seem.android.mockup1.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.seem.android.mockup1.Api;
import com.seem.android.mockup1.AppSingleton;
import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.customviews.SquareImageView;
import com.seem.android.mockup1.model.Item;
import com.seem.android.mockup1.util.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by igbopie on 20/03/14.
 */
public class ItemFullScreenFragment extends Fragment {
    Handler h=new Handler();

    public static ItemFullScreenFragment newInstance(String itemId) {
        ItemFullScreenFragment f = new ItemFullScreenFragment();
        Bundle args = new Bundle();
        args.putString(GlobalVars.EXTRA_ITEM_ID, itemId);
        f.setArguments(args);
        return f;
    }

    ImageView image;
    ProgressBar progressBar;
    TextView captionTextView;

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
        Utils.debug("OnActivityCreated");
        image = (ImageView) getView().findViewById(R.id.imageView);
        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        captionTextView = (TextView) getView().findViewById(R.id.captionTextView);

        String itemId = getItemId();

        new GetItem(itemId).execute();

        Timer myTimer = new Timer();
        myTimer.schedule(new HideActionBar(), 3000);
        super.onActivityCreated(savedInstanceState);
    }

    public String getItemId() {
        return getArguments().getString(GlobalVars.EXTRA_ITEM_ID, null);
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

                    if (getActivity().getActionBar().isShowing()){
                        getActivity().getActionBar().hide();
                    }
                }
            });

        }
    }
}
