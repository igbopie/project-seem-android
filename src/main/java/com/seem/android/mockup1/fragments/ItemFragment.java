package com.seem.android.mockup1.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
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
import android.view.animation.DecelerateInterpolator;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import com.seem.android.mockup1.Api;
import com.seem.android.mockup1.AppSingleton;
import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.activities.ItemsFullScreenActivity;
import com.seem.android.mockup1.activities.ReplyFlowActivity;
import com.seem.android.mockup1.activities.SeemView;
import com.seem.android.mockup1.adapters.ThumbnailAdapter;
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
    private ZoomUtil zoom;

    private List<Item> replies = new ArrayList<Item>();

    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration = 100;

    TwoWayGridView twoWayGridView;
    ThumbnailAdapter thumbnailAdapter;


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

        image = (SpinnerImageView) getView().findViewById(R.id.itemMainImage);
        image.setLayoutParams(new RelativeLayout.LayoutParams(GlobalVars.GRID_SIZE, GlobalVars.GRID_SIZE));
        if (!getActivity().getActionBar().isShowing()){
            getActivity().getActionBar().show();
        }
        new GetItemTask().execute(getReplyId());

        twoWayGridView = (TwoWayGridView) getView().findViewById(R.id.gridview);
        twoWayGridView.setColumnWidth(GlobalVars.GRID_SIZE);
        twoWayGridView.setRowHeight(GlobalVars.GRID_SIZE);
        thumbnailAdapter = new ThumbnailAdapter(this.getActivity());
        twoWayGridView.setAdapter(thumbnailAdapter);
        twoWayGridView.setOnItemClickListener(new TwoWayAdapterView.OnItemClickListener() {
            public void onItemClick(TwoWayAdapterView parent, View v, int position, long id) {
                Utils.debug("ItemClicked:"+position);
                Item item = (Item)thumbnailAdapter.getItem(position);
                Utils.debug("Item:"+item.getId());
                zoom = new ZoomUtil(item,((SpinnerImageView)v).getImageView());
                zoom.startZoom();
            }
        });

        super.onActivityCreated(savedInstanceState);
    }

    public void paintReply(){
        thumbnailAdapter.clear();
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
        Utils.debug("onActivityResult Fragment");
        if (requestCode == GlobalVars.TAKE_PHOTO_CODE && resultCode == Activity.RESULT_OK) {
            Utils.debug("Pic taken");
            this.paintReply();
        } else if (requestCode == GlobalVars.FULLSCREEN_BACK_CODE) {
            Utils.debug("Full Screen Back");
            if(zoom != null) {
                zoom.endZoom();
            }
        }
    }
    private void addToGrid(Item item){
        thumbnailAdapter.addItem(item);
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
            AppSingleton.getInstance().saveItem(result);
            paintReply();
        }
    }
    class GoToItemClickHandler implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(view instanceof SpinnerImageView) {
                SpinnerImageView imageView = (SpinnerImageView)view;
                zoom = new ZoomUtil(images.get(imageView),imageView);
                zoom.startZoom();
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
                addToGrid(item);
            }

            image.setLoading(false);
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
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if(id == R.id.action_camera && item != null){
            Utils.debug("Action camera!");

            Intent intent = new Intent(this.getActivity(), ReplyFlowActivity.class);
            intent.putExtra(GlobalVars.EXTRA_ITEM_ID,item.getId());
            startActivityForResult(intent,GlobalVars.TAKE_PHOTO_CODE);

            return true;

        }
        return super.onOptionsItemSelected(menuItem);
    }

    class ZoomUtil {
        View thumbView;
        Drawable imageResId;
        ImageView expandedImageView;
        Rect startBounds = new Rect();
        Rect finalBounds = new Rect();
        Point globalOffset = new Point();
        float startScaleFinal;
        Item item;

        ZoomUtil(Item item,View thumbView) {
            this.thumbView = thumbView;
            this.imageResId = item.getImageThumb();
            this.item = item;
        }

        private void endZoom() {
            if (mCurrentAnimator != null) {
                mCurrentAnimator.cancel();
            }

            // Animate the four positioning/sizing properties in parallel,
            // back to their original values.
            AnimatorSet set = new AnimatorSet();
            set.play(ObjectAnimator
                    .ofFloat(expandedImageView, View.X, startBounds.left))
                    .with(ObjectAnimator
                            .ofFloat(expandedImageView,
                                    View.Y, startBounds.top))
                    .with(ObjectAnimator
                            .ofFloat(expandedImageView,
                                    View.SCALE_X, startScaleFinal))
                    .with(ObjectAnimator
                            .ofFloat(expandedImageView,
                                    View.SCALE_Y, startScaleFinal));
            set.setDuration(mShortAnimationDuration);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    mCurrentAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    thumbView.setAlpha(1f);
                    expandedImageView.setVisibility(View.GONE);
                    mCurrentAnimator = null;
                }
            });
            set.start();
            mCurrentAnimator = set;
        }

        private void startZoom() {
            // If there's an animation in progress, cancel it
            // immediately and proceed with this one.
            if (mCurrentAnimator != null) {
                mCurrentAnimator.cancel();
            }

            // Load the high-resolution "zoomed-in" image.
            expandedImageView = (ImageView) getView().findViewById(
                    R.id.expandedImage);
            expandedImageView.setImageDrawable(imageResId);

            // Calculate the starting and ending bounds for the zoomed-in image.
            // This step involves lots of math. Yay, math.
            startBounds = new Rect();
            finalBounds = new Rect();
            globalOffset = new Point();

            // The start bounds are the global visible rectangle of the thumbnail,
            // and the final bounds are the global visible rectangle of the container
            // view. Also set the container view's offset as the origin for the
            // bounds, since that's the origin for the positioning animation
            // properties (X, Y).
            thumbView.getGlobalVisibleRect(startBounds);
            getView().getGlobalVisibleRect(finalBounds, globalOffset);
            startBounds.offset(-globalOffset.x, -globalOffset.y);
            finalBounds.offset(-globalOffset.x, -globalOffset.y);

            // Adjust the start bounds to be the same aspect ratio as the final
            // bounds using the "center crop" technique. This prevents undesirable
            // stretching during the animation. Also calculate the start scaling
            // factor (the end scaling factor is always 1.0).
            float startScale;
            if ((float) finalBounds.width() / finalBounds.height()
                    > (float) startBounds.width() / startBounds.height()) {
                // Extend start bounds horizontally
                startScale = (float) startBounds.height() / finalBounds.height();
                float startWidth = startScale * finalBounds.width();
                float deltaWidth = (startWidth - startBounds.width()) / 2;
                startBounds.left -= deltaWidth;
                startBounds.right += deltaWidth;
            } else {
                // Extend start bounds vertically
                startScale = (float) startBounds.width() / finalBounds.width();
                float startHeight = startScale * finalBounds.height();
                float deltaHeight = (startHeight - startBounds.height()) / 2;
                startBounds.top -= deltaHeight;
                startBounds.bottom += deltaHeight;
            }

            // Hide the thumbnail and show the zoomed-in view. When the animation
            // begins, it will position the zoomed-in view in the place of the
            // thumbnail.
            thumbView.setAlpha(0f);
            expandedImageView.setVisibility(View.VISIBLE);

            // Set the pivot point for SCALE_X and SCALE_Y transformations
            // to the top-left corner of the zoomed-in view (the default
            // is the center of the view).
            expandedImageView.setPivotX(0f);
            expandedImageView.setPivotY(0f);

            // Construct and run the parallel animation of the four translation and
            // scale properties (X, Y, SCALE_X, and SCALE_Y).
            AnimatorSet set = new AnimatorSet();
            set
                    .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                            startBounds.left, finalBounds.left))
                    .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                            startBounds.top, finalBounds.top))
                    .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                            startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                    View.SCALE_Y, startScale, 1f));
            set.setDuration(mShortAnimationDuration);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mCurrentAnimator = null;
                    mCallback.itemSelected(item.getId(), 0);

                    Item parentItem = ItemFragment.this.item;

                    Intent intent = new Intent(getActivity(), ItemsFullScreenActivity.class);
                    intent.putExtra(GlobalVars.EXTRA_CURRENT_ITEM_ID, item.getId());
                    intent.putExtra(GlobalVars.EXTRA_PARENT_ITEM_ID, parentItem.getId());
                    startActivityForResult(intent, GlobalVars.FULLSCREEN_BACK_CODE);

                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mCurrentAnimator = null;
                }
            });
            set.start();
            mCurrentAnimator = set;

            // Upon clicking the zoomed-in image, it should zoom back down
            // to the original bounds and show the thumbnail instead of
            // the expanded image.
            startScaleFinal = startScale;

        }
    }
}
