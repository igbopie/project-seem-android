package com.seem.android.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.RelativeLayout;

import com.seem.android.GlobalVars;
import com.seem.android.MyApplication;
import com.seem.android.R;
import com.seem.android.adapters.ThreadedViewAdapter;
import com.seem.android.adapters.ThumbnailAdapterV4;
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


public class ItemFragmentV5 extends Fragment implements View.OnClickListener{

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


    ThumbnailAdapterV4 thumbnailAdapter;
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
        return inflater.inflate(R.layout.fragment_item_view_v5, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetItems().execute();
        threadedViewComponent = (ThreadedViewComponent) view.findViewById(R.id.threadedViewComponent);
        itemMainImage = (ImageView) view.findViewById(R.id.itemMainImage);
        gridView = (GridView) view.findViewById(R.id.gridview);
        gridviewmask = view.findViewById(R.id.gridviewmask);
        mainImageBackground = view.findViewById(R.id.mainImageBackground);
        fromGridViewToMainImage = (ImageView) view.findViewById(R.id.fromGridViewToMainImage);
        threadedViewComponent.setOnItemClickListener(new ThreadedViewComponent.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                if(position == parents.size()-1){
                    //It's me, just go back
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

                            gridviewmask.setVisibility(View.VISIBLE);
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.play(ObjectAnimator.ofFloat(gridView, View.ALPHA, 0.0f, 1.0f))
                                    .with(ObjectAnimator.ofFloat(itemMainImage, View.ALPHA, 0.0f, 1.0f))
                                    .with(ObjectAnimator.ofFloat(mainImageBackground, View.ALPHA, 0.0f, 1.0f))
                                    .with(ObjectAnimator.ofFloat(gridviewmask, View.ALPHA, 0.0f, 1.0f));


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
                } else {
                    Item changeItem = parents.get(position);
                    getArguments().putString(GlobalVars.EXTRA_ITEM_ID, changeItem.getId());
                    //init
                    new GetItems().execute();
                    Utils.loadBitmap(changeItem.getMediaId(), Api.ImageFormat.LARGE,itemMainImage,getActivity());
                    setImageScroll(1f);
                    thumbnailAdapter.clear();
                    thumbnailAdapter.notifyDataSetChanged();

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
                            gridviewmask.setVisibility(View.VISIBLE);
                            itemMainImage.setVisibility(View.VISIBLE);
                            threadedViewComponentMask.setVisibility(View.VISIBLE);
                            mainImageBackground.setVisibility(View.VISIBLE);
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.play(ObjectAnimator.ofFloat(gridView, View.ALPHA, 0.0f, 1.0f))
                                    .with(ObjectAnimator.ofFloat(itemMainImage, View.ALPHA, 0.0f, 1.0f))
                                    .with(ObjectAnimator.ofFloat(mainImageBackground, View.ALPHA, 0.0f, 1.0f))
                                    .with(ObjectAnimator.ofFloat(gridviewmask, View.ALPHA, 0.0f, 1.0f));


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
                        .with(ObjectAnimator.ofFloat(gridviewmask, View.ALPHA, 1.0f, 0.0f));


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
                if(isMax){
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
                params.topMargin = view.getTop() + gridView.getTop();
                params.leftMargin = view.getLeft();
                params.width = GlobalVars.SCREEN_WIDTH / 3;
                params.height = params.width;
                fromGridViewToMainImage.setLayoutParams(params);
                Utils.loadBitmap(replies.get(position).getMediaId(), Api.ImageFormat.THUMB, fromGridViewToMainImage, getActivity());
                fromGridViewToMainImage.setVisibility(View.VISIBLE);

                view.setVisibility(View.INVISIBLE);

                AnimatorSet animator = new AnimatorSet();
                animator.play(ObjectAnimator.ofFloat(fromGridViewToMainImage,View.Y,params.topMargin,itemMainImage.getTop()))
                .with(ObjectAnimator.ofFloat(ItemFragmentV5.this,"fromGridViewToMainImageSize",params.width,GlobalVars.SCREEN_WIDTH))
                .with(ObjectAnimator.ofFloat(fromGridViewToMainImage,View.X,params.leftMargin,0));
                animator.setDuration(500);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        Item changeItem = replies.get(position);
                        getArguments().putString(GlobalVars.EXTRA_ITEM_ID, changeItem.getId());
                        //init
                        new GetItems().execute();
                        Utils.loadBitmap(changeItem.getMediaId(), Api.ImageFormat.LARGE,itemMainImage,getActivity());
                        setImageScroll(1f);
                        thumbnailAdapter.clear();
                        thumbnailAdapter.notifyDataSetChanged();
                        fromGridViewToMainImage.setVisibility(View.INVISIBLE);
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

        thumbnailAdapter = new ThumbnailAdapterV4(getActivity(),
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
            ViewGroup.LayoutParams layout = itemMainImage.getLayoutParams();
            layout.height = GlobalVars.SCREEN_WIDTH;
            layout.width = GlobalVars.SCREEN_WIDTH;
            itemMainImage.setLayoutParams(layout);

            Utils.loadBitmap(item.getMediaId(), Api.ImageFormat.LARGE,itemMainImage,getActivity());

            thumbnailAdapter.clear();

            for(Item item:replies) {
                thumbnailAdapter.addItem(item);
            }
            thumbnailAdapter.notifyDataSetChanged();
            gridView.setAdapter(thumbnailAdapter);


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




    }
}
