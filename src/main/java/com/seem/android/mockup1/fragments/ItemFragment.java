package com.seem.android.mockup1.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import com.jess.ui.TwoWayAdapterView;
import com.jess.ui.TwoWayGridView;
import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.MyApplication;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.adapters.ThumbnailAdapter;
import com.seem.android.mockup1.asynctask.DownloadAsyncTask;
import com.seem.android.mockup1.customviews.SpinnerImageView;
import com.seem.android.mockup1.model.Item;
import com.seem.android.mockup1.model.Seem;
import com.seem.android.mockup1.service.ItemService;
import com.seem.android.mockup1.service.SeemService;
import com.seem.android.mockup1.util.ActivityFactory;
import com.seem.android.mockup1.util.ItemSelectedListener;
import com.seem.android.mockup1.util.Utils;

import java.util.ArrayList;
import java.util.List;


public class ItemFragment extends Fragment {

    public static ItemFragment newInstance(String seemId,String itemId) {
        ItemFragment f = new ItemFragment();
        Bundle args = new Bundle();
        args.putString(GlobalVars.EXTRA_ITEM_ID, itemId);
        args.putString(GlobalVars.EXTRA_SEEM_ID, seemId);
        f.setArguments(args);
        return f;
    }


    private boolean refresh = false;
    private SpinnerImageView image;
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
    Seem seem = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View findViewById(int id){
        return getActivity().findViewById(id);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_seem_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.recoverFromSavedState(savedInstanceState);

        Utils.debug(this.getClass(),"ItemActivity OnCreate - Seem: "+getSeemId()+" Item: "+getItemId());

        image = (SpinnerImageView) findViewById(R.id.itemMainImage);
        image.setLayoutParams(new RelativeLayout.LayoutParams(GlobalVars.GRID_SIZE, GlobalVars.GRID_SIZE));

        new GetItemAndPaintTask().execute(getItemId());

        twoWayGridView = (TwoWayGridView) findViewById(R.id.gridview);
        twoWayGridView.setColumnWidth(GlobalVars.GRID_SIZE);
        twoWayGridView.setRowHeight(GlobalVars.GRID_SIZE);
        thumbnailAdapter = new ThumbnailAdapter(getActivity(),
                new ItemSelectedListener() {
                    @Override
                    public void itemSelected(Item item) {
                        onItemClickListener.onClick(getSeemId(), item.getId());
                    }
                },
                new ItemSelectedListener() {
                    @Override
                    public void itemSelected(Item item) {
                        ActivityFactory.startThreadedActivity(getActivity(),item.getId());
                    }
                }
        );
        twoWayGridView.setAdapter(thumbnailAdapter);
        twoWayGridView.setOnItemClickListener(new TwoWayAdapterView.OnItemClickListener() {
            public void onItemClick(TwoWayAdapterView parent, View v, int position, long id) {
                Utils.debug(this.getClass(),"ItemClicked:"+position);
                Item item = (Item)thumbnailAdapter.getItem(position);
                Utils.debug(this.getClass(),"Item:"+item.getId());
                zoom = new ZoomUtil(item,((SpinnerImageView)v).getImageView());
                zoom.startZoom();
            }
        });

    }

    public void paintReply(){
        thumbnailAdapter.clear();


        image.setDepthNumber(item.getDepth());
        if(item.getDepth() > 0) {
            image.setViewThreadOnClick(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityFactory.startThreadedActivity(getActivity(), item.getId());
                }
            });
        }
        //FIND replies
        if(item.getReplyCount() > 0 ){
            new GetRepliesTask(item).execute();
        } else {
            image.setLoading(false);
        }


        new DownloadAsyncTask(item,image.getImageView(),true).execute();

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view instanceof SpinnerImageView) {
                    zoom = new ZoomUtil(item, image);
                    zoom.startZoom();
                }
            }
        });


        image.setText(item.getCaption());
        getActivity().setTitle(item.getCaption());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Utils.debug(this.getClass(),"ItemActivity - onSaveInstanceState");
        outState.putString(GlobalVars.EXTRA_SEEM_ID, getSeemId());
        outState.putString(GlobalVars.EXTRA_ITEM_ID,getItemId());
    }

    protected void recoverFromSavedState(Bundle savedInstanceState) {
        if(savedInstanceState != null && savedInstanceState.containsKey(GlobalVars.EXTRA_SEEM_ID)){
            getArguments().putString(GlobalVars.EXTRA_SEEM_ID,savedInstanceState.getString(GlobalVars.EXTRA_SEEM_ID));
            getArguments().putString(GlobalVars.EXTRA_ITEM_ID,savedInstanceState.getString(GlobalVars.EXTRA_ITEM_ID));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.debug(this.getClass(),"ItemFragment onActivityResult requestCode="+requestCode+" resultCode="+resultCode);
        if (requestCode == GlobalVars.RETURN_CODE_REPLY_TO_ITEM && resultCode == Activity.RESULT_OK) {
            Utils.debug(this.getClass(),"Pic taken");
            new GetItemAndPaintTask().execute(getItemId());
        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.seem_view, menu);
        MenuItem menuItem = menu.findItem(R.id.action_camera);
        if(!MyApplication.isLoggedIn()){
            menuItem.setVisible(false);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if(id == R.id.action_camera && item != null){
            Utils.debug(this.getClass(),"Action camera!");
            PopupMenu popup = new PopupMenu(getActivity(), findViewById(R.id.action_camera));
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.camera_popup_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.actionPopupCamera:
                            Utils.debug(this.getClass(), "NEW SEEM!");
                            ActivityFactory.startReplyItemActivity(getActivity(),item.getId(), GlobalVars.PhotoSource.CAMERA);
                            return true;
                        case R.id.actionPopupGallery:
                            Utils.debug(this.getClass(), "NEW SEEM!");
                            ActivityFactory.startReplyItemActivity(getActivity(),item.getId(), GlobalVars.PhotoSource.GALLERY);
                            return true;
                    }
                    return false;
                }
            });
            popup.show();
            return true;
        }
        if(id == R.id.action_refresh) {
            //newGame();
            Utils.debug(this.getClass(),"Refresh Main Item and Replies");
            this.refresh = true;
            new GetItemAndPaintTask().execute(getItemId());
            return true;
        }

        return super.onOptionsItemSelected(menuItem);
    }


    private void addToGrid(Item item){
        thumbnailAdapter.addItem(item);
    }


    @Override
    public void onResume() {
        super.onResume();
        Utils.debug(this.getClass(),"Item Fragment - On resume");
        if(zoom != null) {
            zoom.endZoom();
            zoom = null;
        }
    }



    public String getSeemId(){
        String seemId = getArguments().getString(GlobalVars.EXTRA_SEEM_ID);
        return  seemId;
    }
    public String getItemId(){
        String itemId =getArguments().getString(GlobalVars.EXTRA_ITEM_ID);
        return  itemId;
    }

    public class InitAsyncTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            seem = SeemService.getInstance().findSeemById(getSeemId());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getActivity().setTitle(seem.getTitle());
        }
    }


    private class GetItemAndPaintTask extends AsyncTask<String,Void,Item> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            image.setLoading(true);
        }

        @Override
        protected Item doInBackground(String... id) {
            item = ItemService.getInstance().findItemById(id[0],refresh,false);
            Utils.debug(this.getClass(),"This is the item:" + item);
            return item;
        }

        @Override
        protected void onPostExecute(Item result) {
            super.onPostExecute(result);
            paintReply();


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
            List<Item> items = ItemService.getInstance().findItemReplies(item.getId(),refresh);
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



    class ZoomUtil {
        View thumbView;
        ImageView expandedImageView;
        Rect startBounds = new Rect();
        Rect finalBounds = new Rect();
        Point globalOffset = new Point();
        float startScaleFinal;
        Item item;


        ZoomUtil(Item item,View thumbView) {
            this.thumbView = thumbView;
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
            expandedImageView = (ImageView) findViewById(
                    R.id.expandedImage);
            expandedImageView.setImageBitmap(thumbView.getDrawingCache());

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

            twoWayGridView.getGlobalVisibleRect(finalBounds, globalOffset);
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

                    Item parentItem = ItemFragment.this.item;
                    ActivityFactory.startItemFullscreenActivity(getActivity(), getSeemId(), parentItem.getId(), item.getId());
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
