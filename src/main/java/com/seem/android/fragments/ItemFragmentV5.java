package com.seem.android.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import com.seem.android.adapters.ThreadedViewAdapter;
import com.seem.android.adapters.ThumbnailAdapterV4;
import com.seem.android.adapters.ThumbnailAdapterV5;
import com.seem.android.customviews.ThreadedViewComponent;
import com.seem.android.model.Item;
import com.seem.android.model.Seem;
import com.seem.android.service.Api;
import com.seem.android.service.ItemService;
import com.seem.android.service.SeemService;
import com.seem.android.util.ActivityFactory;
import com.seem.android.util.ItemSelectedListener;
import com.seem.android.util.Utils;
import com.squareup.picasso.Callback;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class ItemFragmentV5 extends Fragment implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    public static ItemFragmentV5 newInstance(String seemId,String itemId) {
        ItemFragmentV5 f = new ItemFragmentV5();
        Bundle args = new Bundle();
        args.putString(GlobalVars.EXTRA_ITEM_ID, itemId);
        args.putString(GlobalVars.EXTRA_SEEM_ID, seemId);
        f.setArguments(args);
        return f;
    }


    AnimatorSet animatorSet;

    private boolean refresh = true;

    private Item item;

    private List<Item> replies = new ArrayList<Item>();
    private List<Item> parents = new ArrayList<Item>();

    GridView gridView;

    ThreadedViewAdapter threadedViewAdapter;
    ThreadedViewComponent threadedViewComponent;
    ImageView itemMainImage;
    Seem seem = null;
    View threadedViewComponentMask;
    View gridviewmask;
    View mainImageBackground;

    boolean isMin = false;
    boolean isMax = true;

    int minSize = -1;
    int maxSize =-1;

    ImageView fromGridViewToMainImage;
    ThumbnailAdapterV5 thumbnailAdapter;


    View bigActionPanel;
    ImageView thumbUpIconBig;
    ImageView thumbDownIconBig;
    ImageView favIconBig;
    TextView nameBig;
    ImageView userImageBig;
    TextView captionBig;
    TextView usernameBig;
    TextView dateBig;
    TextView thumbsUpScoreBig;
    TextView thumbsDownScoreBig;
    TextView favsScoreBig;
    TextView commentsNumberBig;
    ImageView replyIconBig;
    ImageView moreOptionsIconBig;


    View smallActionPanel;
    TextView captionSmall;
    TextView usernameSmall;
    TextView dateSmall;
    ImageView thumbUpIconSmall;
    ImageView thumbDownIconSmall;
    ImageView favIconBigSmall;
    ImageView replyIconSmall;
    ImageView moreOptionsIconSmall;

    TextView depthNumber;

    ImageView backButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_view_v5, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetItems().execute();

        depthNumber = (TextView)view.findViewById(R.id.depthNumber);

        backButton = (ImageView) view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO go back
                Utils.dialog("Work in progress","Not done yet. So, just open the drawer sliding your finger from left to right.",getActivity());
            }
        });

        smallActionPanel= (View) view.findViewById(R.id.smallActionPanel);
        captionSmall = (TextView) view.findViewById(R.id.captionSmall);
        usernameSmall = (TextView) view.findViewById(R.id.usernameSmall);
        dateSmall = (TextView) view.findViewById(R.id.dateSmall);
        thumbUpIconSmall = (ImageView) view.findViewById(R.id.thumbUpIconSmall);
        thumbDownIconSmall = (ImageView) view.findViewById(R.id.thumbDownIconSmall);
        favIconBigSmall = (ImageView) view.findViewById(R.id.favIconBigSmall);
        replyIconSmall =  (ImageView) view.findViewById(R.id.replyIconSmall);
        moreOptionsIconSmall =  (ImageView) view.findViewById(R.id.moreOptionsIconSmall);

        bigActionPanel = view.findViewById(R.id.bigActionPanel);
        captionBig = (TextView) view.findViewById(R.id.captionBig);
        usernameBig = (TextView) view.findViewById(R.id.usernameBig);
        dateBig = (TextView) view.findViewById(R.id.dateBig);
        thumbsUpScoreBig = (TextView) view.findViewById(R.id.thumbsUpScoreBig);
        thumbsDownScoreBig = (TextView) view.findViewById(R.id.thumbsDownScoreBig);
        favsScoreBig = (TextView) view.findViewById(R.id.favsScoreBig);
        thumbUpIconBig = (ImageView) view.findViewById(R.id.thumbUpIconBig);
        thumbDownIconBig = (ImageView) view.findViewById(R.id.thumbDownIconBig);
        favIconBig = (ImageView) view.findViewById(R.id.favIconBig);
        nameBig = (TextView) view.findViewById(R.id.nameBig);
        userImageBig = (ImageView) view.findViewById(R.id.userImageBig);
        commentsNumberBig = (TextView) view.findViewById(R.id.commentsNumberBig);
        replyIconBig = (ImageView) view.findViewById(R.id.replyIconBig);
        moreOptionsIconBig = (ImageView) view.findViewById(R.id.moreOptionsIconBig);


        moreOptionsIconSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getActivity(), moreOptionsIconSmall);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.item_more_options, popup.getMenu());
                popup.setOnMenuItemClickListener(ItemFragmentV5.this);
                popup.show();
            }
        });
        moreOptionsIconBig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getActivity(), moreOptionsIconBig);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.item_more_options, popup.getMenu());
                popup.setOnMenuItemClickListener(ItemFragmentV5.this);
                popup.show();
            }
        });
        replyIconSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getActivity(), replyIconSmall);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.camera_popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(ItemFragmentV5.this);
                popup.show();
            }
        });
        replyIconBig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getActivity(), replyIconBig);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.camera_popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(ItemFragmentV5.this);
                popup.show();
            }
        });


        favIconBig.setOnClickListener(this);
        thumbUpIconBig.setOnClickListener(this);
        thumbDownIconBig.setOnClickListener(this);


        favIconBigSmall.setOnClickListener(this);
        thumbUpIconSmall.setOnClickListener(this);
        thumbDownIconSmall.setOnClickListener(this);

        threadedViewComponent = (ThreadedViewComponent) view.findViewById(R.id.threadedViewComponent);
        itemMainImage = (ImageView) view.findViewById(R.id.itemMainImage);
        ViewGroup.LayoutParams layout = itemMainImage.getLayoutParams();
        layout.height = GlobalVars.SCREEN_WIDTH;
        layout.width = GlobalVars.SCREEN_WIDTH;
        itemMainImage.setLayoutParams(layout);

        gridView = (GridView) view.findViewById(R.id.gridview);
        gridviewmask = view.findViewById(R.id.gridviewmask);
        mainImageBackground = view.findViewById(R.id.mainImageBackground);
        fromGridViewToMainImage = (ImageView) view.findViewById(R.id.fromGridViewToMainImage);
        threadedViewComponent.setOnItemClickListener(new ThreadedViewComponent.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if(position != parents.size()-1){
                    Item changeItem = parents.get(position);
                    getArguments().putString(GlobalVars.EXTRA_ITEM_ID, changeItem.getId());
                    //init
                    new GetItems().execute();
                    Utils.loadBitmap(changeItem.getMediaId(), Api.ImageFormat.LARGE,itemMainImage,GlobalVars.SCREEN_WIDTH,GlobalVars.SCREEN_WIDTH,getActivity());
                    setImageScroll(1f);
                    thumbnailAdapter.clear();
                    thumbnailAdapter.notifyDataSetChanged();
                }

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(ObjectAnimator.ofFloat(ItemFragmentV5.this, "threadViewSize", 0.0f, 1.0f));
                animatorSet.setDuration((long) 150);
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {

                        gridView.setVisibility(View.VISIBLE);
                        itemMainImage.setVisibility(View.VISIBLE);
                        threadedViewComponentMask.setVisibility(View.VISIBLE);
                        mainImageBackground.setVisibility(View.VISIBLE);
                        if(isMax) {
                            bigActionPanel.setVisibility(View.VISIBLE);
                            gridviewmask.setVisibility(View.VISIBLE);
                        }
                        smallActionPanel.setVisibility(View.VISIBLE);
                        depthNumber.setVisibility(View.VISIBLE);
                        AnimatorSet animatorSet = new AnimatorSet();
                        AnimatorSet.Builder builder = animatorSet.play(ObjectAnimator.ofFloat(gridView, View.ALPHA, 0.0f, 1.0f))
                                .with(ObjectAnimator.ofFloat(itemMainImage, View.ALPHA, 0.0f, 1.0f))
                                .with(ObjectAnimator.ofFloat(mainImageBackground, View.ALPHA, 0.0f, 1.0f))
                                .with(ObjectAnimator.ofFloat(smallActionPanel, View.ALPHA, 0.0f, 1.0f))
                                .with(ObjectAnimator.ofFloat(depthNumber, View.ALPHA, 0.0f, 1.0f));

                        if(isMax) {
                            builder.with(ObjectAnimator.ofFloat(bigActionPanel, View.ALPHA, 0.0f, 1.0f))
                                    .with(ObjectAnimator.ofFloat(gridviewmask, View.ALPHA, 0.0f, 1.0f));
                        }


                        animatorSet.setDuration((long) 150);
                        animatorSet.setInterpolator(new DecelerateInterpolator());
                        animatorSet.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                animatorSet.start();
            }
        });

        threadedViewComponentMask = (View) view.findViewById(R.id.threadedViewComponentMask);

        threadedViewComponentMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.debug(getClass(),"Click");
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(ObjectAnimator.ofFloat(gridView, View.ALPHA, 1.0f, 0.0f))
                        .with(ObjectAnimator.ofFloat(itemMainImage, View.ALPHA, 1.0f, 0.0f))
                        .with(ObjectAnimator.ofFloat(mainImageBackground, View.ALPHA, 1.0f, 0.0f))
                        .with(ObjectAnimator.ofFloat(gridviewmask, View.ALPHA, 1.0f, 0.0f))
                        .with(ObjectAnimator.ofFloat(bigActionPanel, View.ALPHA, 1.0f, 0.0f))
                        .with(ObjectAnimator.ofFloat(smallActionPanel, View.ALPHA, 1.0f, 0.0f))
                        .with(ObjectAnimator.ofFloat(depthNumber,View.ALPHA,1.0f,0.0f));


                animatorSet.setDuration((long) 150);
                animatorSet.setInterpolator(new DecelerateInterpolator());

                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        gridView.setVisibility(View.INVISIBLE);
                        itemMainImage.setVisibility(View.INVISIBLE);
                        threadedViewComponentMask.setVisibility(View.INVISIBLE);
                        mainImageBackground.setVisibility(View.INVISIBLE);
                        gridviewmask.setVisibility(View.INVISIBLE);
                        bigActionPanel.setVisibility(View.INVISIBLE);
                        smallActionPanel.setVisibility(View.INVISIBLE);
                        depthNumber.setVisibility(View.INVISIBLE);
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.play(ObjectAnimator.ofFloat(ItemFragmentV5.this, "threadViewSize", 1.0f, 0.0f));
                        animatorSet.setDuration((long) 150);
                        animatorSet.setInterpolator(new DecelerateInterpolator());
                        animatorSet.start();

                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                animatorSet.start();
            }
        });

        gridviewmask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isMax && item.getReplyCount() > 0){
                    scrollToMin();
                }
            }
        });
        itemMainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isMin){
                    scrollToMax();
                }
            }
        });
        mainImageBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DO NOTHING
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fromGridViewToMainImage.getLayoutParams();
                params.width = GlobalVars.SCREEN_WIDTH / 3;
                params.height = params.width;
                fromGridViewToMainImage.setLayoutParams(params);
                view.setX(view.getLeft());
                view.setY(view.getTop() + gridView.getTop());

                Utils.loadBitmap(replies.get(position).getMediaId(), Api.ImageFormat.THUMB, fromGridViewToMainImage,GlobalVars.SCREEN_WIDTH,GlobalVars.SCREEN_WIDTH,getActivity());
                fromGridViewToMainImage.setVisibility(View.VISIBLE);
                fromGridViewToMainImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                view.setVisibility(View.INVISIBLE);

                AnimatorSet animator = new AnimatorSet();
                animator.play(ObjectAnimator.ofFloat(fromGridViewToMainImage,View.Y,view.getY(),itemMainImage.getTop()))
                .with(ObjectAnimator.ofFloat(ItemFragmentV5.this,"fromGridViewToMainImageSize",params.width,GlobalVars.SCREEN_WIDTH))
                .with(ObjectAnimator.ofFloat(fromGridViewToMainImage,View.X,view.getX(),0));
                animator.setDuration(500);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        final Item changeItem = replies.get(position);
                        getArguments().putString(GlobalVars.EXTRA_ITEM_ID, changeItem.getId());
                        //init
                        new GetItems().execute();

                        fromGridViewToMainImage.destroyDrawingCache();
                        fromGridViewToMainImage.buildDrawingCache(false);
                        itemMainImage.setImageBitmap(fromGridViewToMainImage.getDrawingCache());
                        fromGridViewToMainImage.setVisibility(View.INVISIBLE);


                        setImageScroll(1f);
                        thumbnailAdapter.clear();
                        thumbnailAdapter.notifyDataSetChanged();
                        view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                animator.start();

                scrollToMax();

            }
        });

    }

    public void setFromGridViewToMainImageSize(float size){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) fromGridViewToMainImage.getLayoutParams();
        params.width = (int) size;
        params.height = (int) size;
        fromGridViewToMainImage.setLayoutParams(params);
    }

    //1 big, 0 small
    public void setThreadViewSize(float amount){
        float leftMargin = Utils.dpToPixel(20,getActivity());
        leftMargin*=(1-amount);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) threadedViewComponent.getLayoutParams();
        layoutParams.leftMargin = (int) leftMargin;
        layoutParams.rightMargin = (int) leftMargin;
        threadedViewComponent.setLayoutParams(layoutParams);

    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.recoverFromSavedState(savedInstanceState);
        if (getActivity() != null &&
                getActivity().getActionBar() != null  &&
                getActivity().getActionBar().isShowing()){

            getActivity().getActionBar().hide();
        }
        Utils.debug(this.getClass(),"ItemActivity OnCreate - Seem: "+getSeemId()+" Item: "+getItemId());

        thumbnailAdapter = new ThumbnailAdapterV5(getActivity());
        gridView.setAdapter(thumbnailAdapter);


        isMax = true;
        isMin = false;


        minSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
        maxSize = GlobalVars.SCREEN_WIDTH;


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
    public void onDestroyView() {
        super.onDestroyView();
        Utils.debug(getClass(),"onDestroyView");
        cleanMe();
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

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.actionPopupCamera:
                ActivityFactory.startReplyItemActivity(getActivity(),item.getId(), GlobalVars.PhotoSource.CAMERA);
                return true;
            case R.id.actionPopupGallery:
                ActivityFactory.startReplyItemActivity(getActivity(),item.getId(), GlobalVars.PhotoSource.GALLERY);
                return true;
            case R.id.actionCopyLink:
                String link = "http://seem-test.herokuapp.com/item/"+item.getId();
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", link);
                clipboard.setPrimaryClip(clip);
                return true;
            case R.id.actionRefresh:
                new GetItems().execute();
                return true;
        }
        return false;
    }


    private class GetItems extends AsyncTask<Void,Void,Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... id) {
            item = ItemService.getInstance().findItemById(getItemId(),refresh,true);
            Utils.debug(this.getClass(),"This is the item:" + item);
            parents.clear();
            parents.add(item);

            Item parentItem = item;
            while(parentItem.getReplyTo() != null){
                parentItem = ItemService.getInstance().findItemById(parentItem.getReplyTo());
                parents.add(0,parentItem);
            }

            replies.clear();
            replies.addAll(ItemService.getInstance().findItemReplies(item.getId(), refresh));

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //Threaded view
            threadedViewAdapter = new ThreadedViewAdapter(parents,getActivity());
            threadedViewComponent.setAdapter(threadedViewAdapter);

            //Normal view
            Utils.loadBitmap(item.getMediaId(), Api.ImageFormat.LARGE,itemMainImage,GlobalVars.SCREEN_WIDTH,GlobalVars.SCREEN_WIDTH,getActivity());

            thumbnailAdapter.clear();

            for(Item item:replies) {
                thumbnailAdapter.addItem(item);
            }
            thumbnailAdapter.notifyDataSetChanged();
            gridView.setAdapter(thumbnailAdapter);

            commentsNumberBig.setText(item.getReplyCount()+"");

            captionBig.setText(item.getCaption());
            captionSmall.setText(item.getCaption());


            dateBig.setText(Utils.getRelativeTime(item.getCreated()));
            dateSmall.setText(Utils.getRelativeTime(item.getCreated()));

            thumbsUpScoreBig.setText(""+item.getThumbUpCount());
            //thumbsUpScoreSmall.setText(""+item.getThumbUpCount());

            thumbsDownScoreBig.setText(""+item.getThumbDownCount());
            //thumbsDownScoreSmall.setText(""+item.getThumbDownCount());

            favsScoreBig.setText(""+item.getFavouriteCount());
            //favsScoreSmall.setText(""+item.getFavouriteCount());

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

            depthNumber.setText(""+item.getDepth());
            if(item.getUserProfile() != null){
                nameBig.setText(item.getUserProfile().getName());
                if(item.getUserProfile().getMediaId() != null) {
                    Utils.loadBitmap(item.getUserProfile().getMediaId(), Api.ImageFormat.THUMB,userImageBig,GlobalVars.SCREEN_WIDTH,GlobalVars.SCREEN_WIDTH,getActivity());
                }
                usernameBig.setText("@"+item.getUserProfile().getUsername());
                usernameSmall.setText("@"+item.getUserProfile().getUsername());

                View.OnClickListener  profileAction = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.onProfileClick(item.getUserProfile().getUsername());
                    }
                };
                userImageBig.setOnClickListener(profileAction);
                nameBig.setOnClickListener(profileAction);
            } else{
                nameBig.setText("");

                usernameBig.setText("");
                usernameSmall.setText("");
                userImageBig.setOnClickListener(null);
                usernameBig.setOnClickListener(null);
                usernameSmall.setText(null);
            }


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

        public void onProfileClick(String username);
    }




    private void cleanMe(){
        for(Field field:this.getClass().getFields()){
            try {
                if(!field.getType().isPrimitive()){
                    field.set(this,null);
                }
            } catch (IllegalAccessException e) {
                Utils.debug(getClass(),e.getMessage());
            }
        }
    }


    public void scrollToMax(){

        gridviewmask.setVisibility(View.VISIBLE);
        bigActionPanel.setVisibility(View.VISIBLE);
        //restore image size
        animatorSet = new AnimatorSet();
        animatorSet.play(ObjectAnimator.ofFloat(ItemFragmentV5.this, "imageScroll", 0, 1));

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
        animatorSet.play(ObjectAnimator.ofFloat(ItemFragmentV5.this, "imageScroll", 1, 0));
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
                bigActionPanel.setVisibility(View.INVISIBLE);
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

    public void setImageScroll(float scroll){
        // 1 (max) - 0 min

        float sizeAux = GlobalVars.SCREEN_WIDTH - minSize;

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) itemMainImage.getLayoutParams();
        params.height = (int) (sizeAux * scroll) + minSize;
        if(params.height < minSize) {
            params.height =(int) minSize;
            isMin = true;
        }else{
            isMin = false;
        }
        if(params.height >= GlobalVars.SCREEN_WIDTH){
            params.height = GlobalVars.SCREEN_WIDTH;
            isMax = true;
        }else{
            isMax = false;
        }
        params.width = (int) params.height;
        itemMainImage.setLayoutParams(params);

        gridviewmask.setAlpha(scroll);
        bigActionPanel.setAlpha(scroll);


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
                GetItems getItemAndPaintTask = new GetItems(){
                    @Override
                    protected void onPostExecute(Void result) {
                        super.onPostExecute(result);
                        dialog.dismiss();
                    }
                };
                getItemAndPaintTask.execute();
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
