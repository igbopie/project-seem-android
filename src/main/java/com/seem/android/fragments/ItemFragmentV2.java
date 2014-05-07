package com.seem.android.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seem.android.GlobalVars;
import com.seem.android.MyApplication;
import com.seem.android.R;
import com.seem.android.adapters.ThumbnailAdapterV2;
import com.seem.android.asynctask.DownloadAsyncTask;
import com.seem.android.customviews.SpinnerImageView;
import com.seem.android.model.Item;
import com.seem.android.model.Seem;
import com.seem.android.service.Api;
import com.seem.android.service.ItemService;
import com.seem.android.service.SeemService;
import com.seem.android.util.ActivityFactory;
import com.seem.android.util.ItemSelectedListener;
import com.seem.android.util.Utils;

import java.util.ArrayList;
import java.util.List;


public class ItemFragmentV2 extends Fragment implements View.OnClickListener{

    public static ItemFragmentV2 newInstance(String seemId,String itemId) {
        ItemFragmentV2 f = new ItemFragmentV2();
        Bundle args = new Bundle();
        args.putString(GlobalVars.EXTRA_ITEM_ID, itemId);
        args.putString(GlobalVars.EXTRA_SEEM_ID, seemId);
        f.setArguments(args);
        return f;
    }


    AnimatorSet animatorSet;
    private boolean refresh = true;
    private SpinnerImageView image;
    private Item item;

    private List<Item> replies = new ArrayList<Item>();

    GridView gridView;
    ThumbnailAdapterV2 thumbnailAdapter;
    Seem seem = null;
    boolean isMin = false;
    boolean isMax = true;
    int minSize = -1;
    int maxSize =-1;
    int currentSize;
    View gridviewmask;
    View miniActionPanel;
    View bigActionPanel;


    TextView captionBig;
    TextView usernameBig;
    TextView dateBig;
    TextView thumbsUpScoreBig;
    TextView thumbsDownScoreBig;
    TextView favsScoreBig;

    TextView captionSmall;
    TextView usernameSmall;
    TextView dateSmall;
    TextView thumbsUpScoreSmall;
    TextView thumbsDownScoreSmall;
    TextView favsScoreSmall;


    ImageView thumbUpIconBig;
    ImageView thumbDownIconBig;
    ImageView favIconBig;

    ImageView thumbUpIconSmall;
    ImageView thumbDownIconSmall;
    ImageView favIconBigSmall;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);



    }

    public View findViewById(int id){
        return getActivity().findViewById(id);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_view_v2, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        image = (SpinnerImageView) findViewById(R.id.itemMainImage);
        gridView = (GridView) view.findViewById(R.id.gridview);
        gridviewmask= view.findViewById(R.id.gridviewmask);
        miniActionPanel = view.findViewById(R.id.miniActionPanel);
        bigActionPanel= view.findViewById(R.id.bigActionPanel);


        captionBig = (TextView) findViewById(R.id.captionBig);
        usernameBig = (TextView) findViewById(R.id.usernameBig);
        dateBig = (TextView) findViewById(R.id.dateBig);
        thumbsUpScoreBig = (TextView) findViewById(R.id.thumbsUpScoreBig);
        thumbsDownScoreBig = (TextView) findViewById(R.id.thumbsDownScoreBig);
        favsScoreBig = (TextView) findViewById(R.id.favsScoreBig);

        captionSmall = (TextView) findViewById(R.id.captionSmall);
        usernameSmall = (TextView) findViewById(R.id.usernameSmall);
        dateSmall = (TextView) findViewById(R.id.dateSmall);
        thumbsUpScoreSmall = (TextView) findViewById(R.id.thumbsUpScoreSmall);
        thumbsDownScoreSmall = (TextView) findViewById(R.id.thumbsDownScoreSmall);
        favsScoreSmall = (TextView) findViewById(R.id.favsScoreSmall);


        thumbUpIconBig = (ImageView) findViewById(R.id.thumbUpIconBig);
        thumbDownIconBig = (ImageView) findViewById(R.id.thumbDownIconBig);
        favIconBig = (ImageView) findViewById(R.id.favIconBig);

        thumbUpIconSmall = (ImageView) findViewById(R.id.thumbUpIconSmall);
        thumbDownIconSmall = (ImageView) findViewById(R.id.thumbDownIconSmall);
        favIconBigSmall = (ImageView) findViewById(R.id.favIconBigSmall);


        favIconBig.setOnClickListener(this);
        thumbUpIconBig.setOnClickListener(this);
        thumbDownIconBig.setOnClickListener(this);

        favIconBigSmall.setOnClickListener(this);
        thumbUpIconSmall.setOnClickListener(this);
        thumbDownIconSmall.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.recoverFromSavedState(savedInstanceState);



        Utils.debug(this.getClass(),"ItemActivity OnCreate - Seem: "+getSeemId()+" Item: "+getItemId());

        minSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
        maxSize = GlobalVars.GRID_SIZE_V2*GlobalVars.GRID_NUMBER_OF_PHOTOS_V2;
        currentSize = maxSize;
        image.setLayoutParams(new RelativeLayout.LayoutParams(maxSize, maxSize));
        isMax = true;
        isMin = false;

        miniActionPanel.setAlpha(0);
        ViewGroup.LayoutParams layoutParams = miniActionPanel.getLayoutParams();
        layoutParams.height = minSize;
        miniActionPanel.setLayoutParams(layoutParams);
        final GestureDetectorCompat mDetectorMask = new GestureDetectorCompat(getActivity(), new ScrollUpOnMaskDetector());
        gridviewmask.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mDetectorMask.onTouchEvent(motionEvent);
                return gridviewmask.onTouchEvent(motionEvent);
            }
        });
        gridviewmask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this is needed so the gridview doesn't work while the main image is big
            }
        });
        final GestureDetectorCompat mDetectorImage = new GestureDetectorCompat(getActivity(), new ScrollDownOnImageDetector());
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mDetectorImage.onTouchEvent(motionEvent);
                return gridviewmask.onTouchEvent(motionEvent);
            }
        });
        new GetItemAndPaintTask().execute(getItemId());

        thumbnailAdapter = new ThumbnailAdapterV2(getActivity(),
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
        gridView.setAdapter(thumbnailAdapter);
        gridView.setOnItemClickListener(new GridView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Utils.debug(this.getClass(), "ItemClicked:" + position);
                Item item = (Item) thumbnailAdapter.getItem(position);
                Utils.debug(this.getClass(), "Item:" + item.getId());
                onItemClickListener.onClick(getSeemId(), item.getId());
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


        new DownloadAsyncTask(item,image.getImageView(),false).execute();
        getActivity().setTitle(item.getCaption());

        //
        captionBig.setText(item.getCaption());
        captionSmall.setText(item.getCaption());

        usernameBig.setText(item.getUsername());
        usernameSmall.setText(item.getUsername());

        dateBig.setText(Utils.getRelativeTime(item.getCreated()));
        dateSmall.setText(Utils.getRelativeTime(item.getCreated()));

        thumbsUpScoreBig.setText(""+item.getThumbUpCount());
        thumbsUpScoreSmall.setText(""+item.getThumbUpCount());

        thumbsDownScoreBig.setText(""+item.getThumbDownCount());
        thumbsDownScoreSmall.setText(""+item.getThumbDownCount());

        favsScoreBig.setText(""+item.getFavouriteCount());
        favsScoreSmall.setText(""+item.getFavouriteCount());

        if(item.isFavourited()){
            favIconBig.setImageResource(R.drawable.star);
            favIconBigSmall.setImageResource(R.drawable.star_black);
        }else{
            favIconBig.setImageResource(R.drawable.star_o);
            favIconBigSmall.setImageResource(R.drawable.star_o_black);
        }

        if(item.getThumbedUp()){
            thumbUpIconBig.setImageResource(R.drawable.thumbs_up);
            thumbUpIconSmall.setImageResource(R.drawable.thumbs_up_black);
        }else{
            thumbUpIconBig.setImageResource(R.drawable.thumbs_o_up);
            thumbUpIconSmall.setImageResource(R.drawable.thumbs_o_up_black);
        }

        if(item.getThumbedDown()){
            thumbDownIconBig.setImageResource(R.drawable.thumbs_down);
            thumbDownIconSmall.setImageResource(R.drawable.thumbs_down_black);
        }else{
            thumbDownIconBig.setImageResource(R.drawable.thumbs_o_down);
            thumbDownIconSmall.setImageResource(R.drawable.thumbs_o_down_black);
        }

        //
        if(item.getDepth() > 0){
            //upButton.setVisible(true);
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Utils.debug(this.getClass(), "ItemActivity - onSaveInstanceState");
        outState.putString(GlobalVars.EXTRA_SEEM_ID, getSeemId());
        outState.putString(GlobalVars.EXTRA_ITEM_ID,getItemId());
    }

    protected void recoverFromSavedState(Bundle savedInstanceState) {

        Utils.debug(this.getClass(), "ItemActivity - recoverFromSavedState");
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
        menu.clear();
        inflater.inflate(R.menu.seem_view, menu);
        MenuItem menuItem = menu.findItem(R.id.action_camera);
        if(!MyApplication.isLoggedIn()){
            menuItem.setVisible(false);
        }
        //upButton = menu.findItem(R.id.action_up);
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
            Utils.debug(this.getClass(), "Refresh Main Item and Replies");
            this.refresh = true;
            new GetItemAndPaintTask().execute(getItemId());
            return true;
        }

        if(id == R.id.action_up || id == android.R.id.home) {
            //newGame();
            Utils.debug(this.getClass(),"Action up");
            if(item.getReplyTo() != null) {
                this.onItemClickListener.onClick(item.getSeemId(), item.getReplyTo());
            } else {
                this.onItemClickListener.onFinish();
            }
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

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

    }



    public String getSeemId(){
        String seemId = getArguments().getString(GlobalVars.EXTRA_SEEM_ID);
        return  seemId;
    }
    public String getItemId(){
        String itemId =getArguments().getString(GlobalVars.EXTRA_ITEM_ID);
        return  itemId;
    }

    @Override
    public void onClick(View view) {
        if (view.equals(favIconBig) || view.equals(favIconBigSmall)) {
            if (item.isFavourited() != null && item.isFavourited()) {
                new UnfavouriteTask().execute();
            } else {
                new FavouriteTask().execute();
            }
        } else if (view.equals(thumbUpIconBig)|| view.equals(thumbUpIconSmall)) {
            if (item.getThumbedUp() != null && item.getThumbedUp()) {
                new ThumbClearTask().execute();
            } else {
                new ThumbUpTask().execute();
            }
        } else if (view.equals(thumbDownIconBig) || view.equals(thumbDownIconSmall) ) {
            if (item.getThumbedDown() != null && item.getThumbedDown()) {
                new ThumbClearTask().execute();
            } else {
                new ThumbDownTask().execute();
            }
        }
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
            item = ItemService.getInstance().findItemById(id[0],refresh,true);
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
        public void onClick(String seemId, String itemId);

        public void onFinish();
    }


    public void scrollToMax(){
        gridviewmask.setVisibility(View.VISIBLE);
        //restore image size
        animatorSet = new AnimatorSet();
        animatorSet.play(ObjectAnimator.ofInt(ItemFragmentV2.this, "imageScroll", minSize, maxSize));

        animatorSet.setDuration((long) 500);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                Utils.debug(getClass(),"onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Utils.debug(getClass(), "onAnimationEnd");
                isMax = true;
                isMin = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                Utils.debug(getClass(),"onAnimationCancel");
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                Utils.debug(getClass(),"onAnimationRepeat");
            }
        });
        animatorSet.start();
    }

    public void scrollToMin(){
        //restore image size
        animatorSet = new AnimatorSet();
        animatorSet.play(ObjectAnimator.ofInt(ItemFragmentV2.this, "imageScroll",currentSize, minSize));
        animatorSet.setDuration((long) 500);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                Utils.debug(getClass(),"onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Utils.debug(getClass(),"onAnimationEnd");
                gridviewmask.setVisibility(View.INVISIBLE);
                isMax = false;
                isMin = true;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                Utils.debug(getClass(),"onAnimationCancel");
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                Utils.debug(getClass(),"onAnimationRepeat");
            }
        });
        animatorSet.start();
    }


    public void setImageScroll(int scroll){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) image.getLayoutParams();
        params.height = scroll;
        if(params.height < minSize) {
            params.height =(int) minSize;
            isMin = true;
            if(animatorSet != null){
                animatorSet.cancel();
            }
        }else{
            isMin = false;
        }
        if(params.height > maxSize){
            params.height = maxSize;
            isMax = true;
            if(animatorSet != null){
                animatorSet.cancel();
            }
        }else{
            isMax = false;
        }
        params.width = params.height;
        image.setLayoutParams(params);
        currentSize = params.width;

        float maxTrimmed = maxSize - minSize;
        float currentSizeTrimmed = currentSize -minSize;
        float percCompleted = currentSizeTrimmed/maxTrimmed;

        gridviewmask.setAlpha(percCompleted);
        miniActionPanel.setAlpha(1-percCompleted);
        bigActionPanel.setAlpha(percCompleted);
    }

    public void goBack(){
        Utils.debug(getClass(),"Go Back!");
        animatorSet = new AnimatorSet();
        animatorSet.play(ObjectAnimator.ofFloat(image, View.Y,0, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics())))
        .with(ObjectAnimator.ofInt(ItemFragmentV2.this, "imageScroll", currentSize, minSize));
        animatorSet.setDuration((long) 250);
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                Utils.debug(getClass(),"onAnimationStart");
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Utils.debug(getClass(),"onAnimationEnd");
                if(item.getReplyTo() != null) {
                    ItemFragmentV2.this.onItemClickListener.onClick(item.getSeemId(), item.getReplyTo());
                } else {
                    ItemFragmentV2.this.onItemClickListener.onFinish();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                Utils.debug(getClass(),"onAnimationCancel");
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                Utils.debug(getClass(),"onAnimationRepeat");
            }
        });
        animatorSet.start();
    }


    class ScrollUpOnMaskDetector extends GestureDetector.SimpleOnGestureListener {
        public boolean onFling(MotionEvent start, MotionEvent finish, float xVelocity, float yVelocity) {
            if (isMax && start.getRawY() > finish.getRawY()) {
                scrollToMin();
            }
            Utils.debug(getClass(),"onfling");
            return true;
        }
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (isMax) {
                scrollToMin();
            }
            Utils.debug(getClass(),"onSingleTapUp");
            return false;
        }

    }

    class ScrollDownOnImageDetector extends GestureDetector.SimpleOnGestureListener {
        public boolean onFling(MotionEvent start, MotionEvent finish, float xVelocity, float yVelocity) {
            if (isMin && start.getRawY() < finish.getRawY()) {
                scrollToMax();
            }else if(start.getRawY() < finish.getRawY()){
                goBack();
            }else if(isMax && start.getRawY() > finish.getRawY()){
                scrollToMin();
            }
            Utils.debug(getClass(),"onfling");
            return true;
        }
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (isMin) {
                scrollToMax();
            } else {
                //FullScreen
                Utils.debug(this.getClass(), "Item:" + item.getId());
                Item parentItem = ItemFragmentV2.this.item;
                ActivityFactory.startItemFullscreenActivity(getActivity(), getSeemId(), parentItem.getId(),parentItem.getId());

            }
            Utils.debug(getClass(),"onSingleTapUp");
            return false;
        }
    }

    class FavouriteTask extends ActionTask{
        @Override
        protected Boolean doAction() {
            return Api.favourite(item.getId(), MyApplication.getToken());
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
                GetItemAndPaintTask getItemAndPaintTask = new GetItemAndPaintTask(){
                    @Override
                    protected void onPostExecute(Item result) {
                        super.onPostExecute(result);
                        dialog.dismiss();
                    }
                };
                getItemAndPaintTask.execute(getItemId());
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