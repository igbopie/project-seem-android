package com.seem.android.mockup1.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import com.seem.android.mockup1.Api;
import com.seem.android.mockup1.AppSingleton;
import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.customviews.SquareImageView;
import com.seem.android.mockup1.model.Item;
import com.seem.android.mockup1.model.Seem;
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
public class ItemFragment extends android.support.v4.app.Fragment implements Observer{



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

    private ImageView image;
    private Map<ImageView,Item> images = new HashMap<ImageView,Item>();

    private GridLayout gridLayout;

    private Item item;
    private Item itemInProgress;
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
        return inflater.inflate(R.layout.reply_fragment_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        Utils.debug("OnActivityCreated");

        gridLayout = (GridLayout) getView().findViewById(R.id.gridLayout);
        image = (ImageView) getView().findViewById(R.id.imageView);

        paintReply();

        /*
        for(Reply replyReply:reply.getReplyList()) {
            replyReply.addObserver(this);
        }*/

        super.onActivityCreated(savedInstanceState);
    }

    public void paintReply(){
        gridLayout.removeAllViews();

        item = AppSingleton.getInstance().findItemById(getReplyId());
        //FIND replies
        if(item.getReplyCount() > 0 ){
            new GetRepliesTask(item.getId()).execute();
        }

        image.setImageDrawable(item.getImageLarge());
        /*if(item.getImageLarge() == null) {
            currentImage.setOnClickListener(new TakeAPictureClickHandler());
        }else{*/

        //}
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

            SquareImageView iv = addToGrid(itemInProgress);
            iv.setImageBitmap(itemInProgress.getTempLocalBitmap());

            //TODO api save item
            new UploadMedia().execute(itemInProgress);


            itemInProgress = null;
        }

    }

    private SquareImageView addToGrid(Item item){
        SquareImageView thumb = new SquareImageView(getView().getContext());

        thumb.setOnClickListener(new GoToReplyClickHandler());
        thumb.setLayoutParams(GlobalVars.layoutParamsForSmallReplies);
        Utils.debug("Adding image to grid:" + item.getId());
        gridLayout.addView(thumb, 0);

        images.put(thumb, item);

        return thumb;

    }

    class GoToReplyClickHandler implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            /*
            if(view instanceof ImageView) {
                ImageView imageView = (ImageView)view;
                Reply reply = images.get(imageView);

                mCallback.itemSelected(reply.getId(), getDepth() + 1);
            }
            */

        }

    }

    private class GetRepliesTask extends AsyncTask<Void,Void,List<Item>> {

        private String itemId;

        public GetRepliesTask(String itemId){
            this.itemId = itemId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Item> doInBackground(Void... voids) {
            List<Item> items = Api.getReplies(itemId);
            return items;
        }

        @Override
        protected void onPostExecute(List<Item> result) {
            super.onPostExecute(result);

            replies = result;

            for(Item item:replies) {
                SquareImageView thumb = addToGrid(item);
                new FetchThumbs(thumb).execute(item);
            }
        }
    }

    private class UploadMedia extends AsyncTask<Item,Void,Item> {
        @Override
        protected Item doInBackground(Item... items) {

            try {
                String mediaId = Api.createMedia(items[0].getTempLocalBitmap());
                if(mediaId != null){
                    items[0].setMediaId(mediaId);
                    Api.reply("Hardcoded caption",items[0].getMediaId(),item.getId());

                }else {
                    Utils.debug("Error uploading");
                }
            }catch (Exception e) {
                Utils.debug("Pete al crear la imagen",e);
            }
            return null;
        }

    }
    private class FetchThumbs extends AsyncTask<Item,Void,Item> {
        private ImageView imageView;

        public FetchThumbs(ImageView imageView){
            this.imageView = imageView;

        }

        @Override
        protected Item doInBackground(Item... items) {

            try {
                Api.downloadThumbImage(items[0]);
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
                    imageView.setImageDrawable(layerDrawable);
                }else {
                    imageView.setImageDrawable(item.getImageThumb());
                }

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
