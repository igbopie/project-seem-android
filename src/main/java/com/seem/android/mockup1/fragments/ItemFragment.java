package com.seem.android.mockup1.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.seem.android.mockup1.Api;
import com.seem.android.mockup1.AppSingleton;
import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.activities.SeemView;
import com.seem.android.mockup1.customviews.SpinnerImageView;
import com.seem.android.mockup1.customviews.SquareImageView;
import com.seem.android.mockup1.model.Item;
import com.seem.android.mockup1.util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by igbopie on 13/03/14.
 */
public class ItemFragment extends Fragment implements Observer{



    private ItemFragmentSelectedListener mCallback;
    /**
     * Create a new instance of ItemFragment
     */
    public static ItemFragment newInstance(String itemId,int depth) {
        ItemFragment f = new ItemFragment();

        Bundle args = new Bundle();

        args.putString(GlobalVars.EXTRA_ITEM_ID, itemId);
        args.putInt(GlobalVars.EXTRA_DEPTH,depth);

        f.setArguments(args);

        return f;
    }

    private SpinnerImageView image;
    private Map<SpinnerImageView,Item> images = new HashMap<SpinnerImageView,Item>();


    private Item item;
    private Item itemInProgress;

    private LinearLayout horizonalGrid;
    private LinearLayout currentVerticalGrid;
    private List<Item> replies = new ArrayList<Item>();


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
        return inflater.inflate(R.layout.fragment_item_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        Utils.debug("OnActivityCreated");

        horizonalGrid = (LinearLayout) getView().findViewById(R.id.linearLayout);
        image = (SpinnerImageView) getView().findViewById(R.id.itemMainImage);
        image.setLayoutParams(new RelativeLayout.LayoutParams(GlobalVars.GRID_SIZE, GlobalVars.GRID_SIZE));
        if (!getActivity().getActionBar().isShowing()){
            getActivity().getActionBar().show();
        }
        new GetItemTask().execute(getReplyId());


        /*
        for(Reply replyReply:reply.getReplyList()) {
            replyReply.addObserver(this);
        }*/

        super.onActivityCreated(savedInstanceState);
    }

    public void paintReply(){
        horizonalGrid.removeAllViews();
        images.clear();

        item = AppSingleton.getInstance().findItemById(getReplyId());
        //FIND replies
        if(item.getReplyCount() > 0 ){
            new GetRepliesTask(item).execute();
        } else {
            image.setLoading(false);
        }

        image.getImageView().setImageDrawable(item.getImageThumb());
        image.setOnClickListener(new GoToItemClickHandler());
        image.setText(item.getCaption());
        images.put(image,item);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (ItemFragmentSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ItemFragmentSelectedListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroy() {
        /*for(Reply replyReply:reply.getReplyList()) {
            replyReply.deleteObserver(this);
        }*/

        super.onDestroy();
        Utils.debug("Destroing:"+getReplyId());

    }

    public String getReplyId() {
        return getArguments().getString(GlobalVars.EXTRA_ITEM_ID, null);
    }
    public int getDepth() {
        int defaultValue = 0;
        if(getArguments() == null){
            return defaultValue;
        }
        return getArguments().getInt(GlobalVars.EXTRA_DEPTH, defaultValue);
    }

    @Override
    public void update(Observable observable, Object o) {
        Utils.debug("I am notified! I have to refresh:" + item.getId());
        paintReply();
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GlobalVars.TAKE_PHOTO_CODE && resultCode == Activity.RESULT_OK) {
            Utils.debug("Pic taken");

            //Controller Logic
            itemInProgress.setTempLocalBitmap(Utils.shrinkBitmap(itemInProgress.getTempLocalFile().getPath()));

            SpinnerImageView iv = addToGrid(itemInProgress);
            iv.getImageView().setImageBitmap(itemInProgress.getTempLocalBitmap());

            new UploadMedia(iv).execute(itemInProgress);

            itemInProgress = null;
        }

    }

    private SpinnerImageView addToGrid(Item item){
        SpinnerImageView thumb = new SpinnerImageView(getView().getContext(),null);

        thumb.setText(item.getCaption());
        thumb.setOnClickListener(new GoToItemClickHandler());
        thumb.setLayoutParams(new LinearLayout.LayoutParams(GlobalVars.GRID_SIZE, GlobalVars.GRID_SIZE));
        Utils.debug("Adding image to grid:" + item.getId());

        int childCount = horizonalGrid.getChildCount();
        if(childCount == 0){
            //first time
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            currentVerticalGrid = new LinearLayout(getActivity());
            currentVerticalGrid.setLayoutParams(params);
            currentVerticalGrid.setOrientation(LinearLayout.VERTICAL);
            horizonalGrid.addView(currentVerticalGrid);
            //On The first time we add a fake main image that is going to be behind
            SpinnerImageView fake = new SpinnerImageView(getView().getContext(),null);
            fake.setLayoutParams(new LinearLayout.LayoutParams(GlobalVars.GRID_SIZE, GlobalVars.GRID_SIZE));

            currentVerticalGrid.addView(fake);
        }else if(currentVerticalGrid.getChildCount() >= GlobalVars.GRID_NUMBER_OF_PHOTOS){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            currentVerticalGrid = new LinearLayout(getActivity());
            currentVerticalGrid.setLayoutParams(params);
            currentVerticalGrid.setOrientation(LinearLayout.VERTICAL);
            horizonalGrid.addView(currentVerticalGrid);
        }
        currentVerticalGrid.addView(thumb);

        images.put(thumb, item);

        return thumb;

    }
    private class GetItemTask extends AsyncTask<String,Void,Item> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Item doInBackground(String... id) {
            Item item = AppSingleton.getInstance().findItemById(id[0]);
            if(item == null) {
                item = Api.getItem(id[0]);
                AppSingleton.getInstance().saveItem(item);
            }
            Utils.debug("This is the item:" + item);

            return item;
        }

        @Override
        protected void onPostExecute(Item result) {
            super.onPostExecute(result);
            /*adapter.setItemList(result);
            adapter.notifyDataSetChanged();*/
            AppSingleton.getInstance().saveItem(result);
            paintReply();
        }
    }
    class GoToItemClickHandler implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(view instanceof SpinnerImageView) {
                SpinnerImageView imageView = (SpinnerImageView)view;
                Item item = images.get(imageView);
                mCallback.itemSelected(item.getId(), 0);
            }

        }

    }

    private class GetRepliesTask extends AsyncTask<Void,Void,List<Item>> {

        private Item item;

        public GetRepliesTask(Item item){
            this.item = item;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Item> doInBackground(Void... voids) {
            List<Item> items = AppSingleton.getInstance().findItemReplies(item.getId());
            if(items.size() != item.getReplyCount()){
                //DIRTY! We need to load more
                items = Api.getReplies(item.getId());
                for(Item item:items){
                    AppSingleton.getInstance().saveItem(item);
                }
            }

            return items;
        }

        @Override
        protected void onPostExecute(List<Item> result) {
            super.onPostExecute(result);

            replies = result;

            for(Item item:replies) {
                SpinnerImageView thumb = addToGrid(item);
                new FetchThumbs(thumb).execute(item);
            }

            image.setLoading(false);
        }
    }

    private class UploadMedia extends AsyncTask<Item,Void,Item> {
        private final SpinnerImageView iv;

        public UploadMedia(SpinnerImageView iv) {
            this.iv = iv;
        }

        @Override
        protected Item doInBackground(Item... items) {

            try {
                String mediaId = Api.createMedia(items[0].getTempLocalBitmap());
                if(mediaId != null){
                    items[0].setMediaId(mediaId);
                    Item reply = Api.reply("Hardcoded caption",items[0].getMediaId(),item.getId());
                    AppSingleton.getInstance().saveItem(reply);

                    item.setReplyCount(item.getReplyCount() + 1);

                    return item;
                }else {
                    Utils.debug("Error uploading");
                }
            }catch (Exception e) {
                Utils.debug("Pete al crear la imagen",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Item item) {
            super.onPostExecute(item);

            images.put(iv,item);
            iv.setLoading(false);
        }
    }
    private class FetchThumbs extends AsyncTask<Item,Void,Item> {
        private SpinnerImageView imageView;

        public FetchThumbs(SpinnerImageView imageView){
            this.imageView = imageView;

        }

        @Override
        protected Item doInBackground(Item... items) {

            try {
                if(items[0].getImageThumb() == null) {
                    Api.downloadThumbImage(items[0]);
                }
                return items[0];
            } catch (IOException e) {
                Utils.debug("Pete al bajar la imagen",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Item item) {
            if(item != null) {
                if(item.getReplyCount() > 0) {
                    Resources r = getResources();
                    Drawable[] layers = new Drawable[2];
                    layers[1] = r.getDrawable(R.drawable.withreplies);
                    layers[0] =  item.getImageThumb();
                    LayerDrawable layerDrawable = new LayerDrawable(layers);
                    imageView.getImageView().setImageDrawable(layerDrawable);
                }else {
                    imageView.getImageView().setImageDrawable(item.getImageThumb());
                }
                imageView.setLoading(false);

            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.seem_view, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_camera){
            Utils.debug("Action camera!");

            itemInProgress = new Item();

            itemInProgress.setTempLocalFile(Utils.getNewFileUri());

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, itemInProgress.getTempLocalFile());

            startActivityForResult(cameraIntent, GlobalVars.TAKE_PHOTO_CODE);

            return true;

        }
        return super.onOptionsItemSelected(item);
    }

}
