package com.seem.android.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seem.android.GlobalVars;
import com.seem.android.MyApplication;
import com.seem.android.R;
import com.seem.android.adapters.ItemViewAdapter;
import com.seem.android.adapters.ThreadedV6Adapter;
import com.seem.android.adapters.ThreadedViewAdapter;
import com.seem.android.adapters.ThumbnailAdapterV5;
import com.seem.android.customviews.ItemView;
import com.seem.android.customviews.ThreadedViewComponent;
import com.seem.android.model.Item;
import com.seem.android.model.Seem;
import com.seem.android.service.Api;
import com.seem.android.service.ItemService;
import com.seem.android.service.SeemService;
import com.seem.android.util.ActivityFactory;
import com.seem.android.util.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.widget.ExpandableHListView;
import it.sephiroth.android.library.widget.HListView;


public class ItemFragmentV6 extends Fragment {

    public static ItemFragmentV6 newInstance(String seemId,String itemId,boolean showFirst) {
        ItemFragmentV6 f = new ItemFragmentV6();
        Bundle args = new Bundle();
        args.putString(GlobalVars.EXTRA_ITEM_ID, itemId);
        args.putString(GlobalVars.EXTRA_SEEM_ID, seemId);
        args.putBoolean(GlobalVars.EXTRA_SHOW_FIRST, showFirst);
        f.setArguments(args);
        return f;
    }


    private boolean refresh = true;

    private List<Item> replies = new ArrayList<Item>();
    private List<Item> parents = new ArrayList<Item>();
    private Item item;
    private Seem seem = null;


    private ImageView animationImage;
    private ListView listView;
    ItemViewAdapter itemViewAdapter;
    HListView threadedView;
    ThreadedV6Adapter threadedV6Adapter;
    boolean showedThreadedView = true;
    boolean loadThreadView = true;
    private int lastTop;
    private int lastItem;
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
        return inflater.inflate(R.layout.fragment_item_view_v6, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        animationImage = (ImageView) view.findViewById(R.id.animationImage);

        threadedView = (HListView) view.findViewById(R.id.threadedView);
        threadedView.setOnItemClickListener(new it.sephiroth.android.library.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> adapterView, View view, int i, long l) {
                Item clicked = parents.get(i);
                onItemClickListener.onClick(clicked.getSeemId(),clicked.getId());
            }
        });

        listView = (ListView) view.findViewById(R.id.listView);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                //
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean scrolledUp = false;
                boolean scrolledDown = false;
                if(view != null && view.getChildCount() > 0) {

                    //Utils.debug(getClass(), "Scroll: " + view.getChildAt(0).getTop());
                    int currentTop =view.getChildAt(0).getTop();

                    if(lastItem > firstVisibleItem){
                        scrolledUp = true;
                    } else if (lastItem < firstVisibleItem){
                        scrolledDown = true;
                    } else {
                        if(lastTop > currentTop){
                            scrolledDown = true;
                        } else if(lastTop < currentTop) {
                            scrolledUp = true;
                        } else{
                            //Utils.debug(getClass(),"Stop");
                        }
                    }

                    lastTop = currentTop;
                    lastItem = firstVisibleItem;
                }


                if(item != null && firstVisibleItem == 0 && item.getDepth() == 0 ){
                    hideThreadedView();
                } else {
                    if(scrolledUp){
                        showThreadedView();
                    }else if(scrolledDown){
                        hideThreadedView();
                    }
                }

            }
        });



        new GetItems().execute();



    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.recoverFromSavedState(savedInstanceState);
        /*if (getActivity() != null &&
                getActivity().getActionBar() != null  &&
                getActivity().getActionBar().isShowing()){

            getActivity().getActionBar().hide();
        }*/
        Utils.debug(this.getClass(),"ItemActivity OnCreate - Seem: "+getSeemId()+" Item: "+getItemId());


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
    public Boolean getShowFirst(){
        return getArguments().getBoolean(GlobalVars.EXTRA_SHOW_FIRST);
    }


    private class GetItems extends AsyncTask<Void,Void,Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... id) {
            //seem = SeemService.getInstance().findSeemById(getSeemId());

            item = ItemService.getInstance().findItemById(getItemId(),refresh,true);
            Utils.debug(this.getClass(),"This is the item:" + item);
            if(loadThreadView) {
                parents.clear();
                parents.add(item);

                Item parentItem = item;
                while (parentItem.getReplyTo() != null) {
                    parentItem = ItemService.getInstance().findItemById(parentItem.getReplyTo());
                    parents.add(0, parentItem);
                }

            }

            replies.clear();
            //replies.addAll(ItemService.getInstance().findItemReplies(item.getId(),0, true,true));
            replies.add(item);
            replies.addAll(Api.getReplies(item.getId(),0,MyApplication.getToken()));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if(seem != null) {
                getActivity().getActionBar().setTitle(seem.getTitle());
            }

            itemViewAdapter = new ItemViewAdapter(replies,getActivity(), new ItemView.OnItemClickListener() {
                @Override
                public void onClick(final Item item,ItemView itemView) {


                    final float imageTopPosition = itemView.getTop();
                    replies.clear();
                    itemViewAdapter.setShowFirst(getShowFirst());
                    itemViewAdapter.notifyDataSetChanged();
                    getArguments().putString(GlobalVars.EXTRA_ITEM_ID, item.getId());
                    getArguments().putBoolean(GlobalVars.EXTRA_SHOW_FIRST, false);
                    new GetItems().execute();

                    //Todo animation
                    showThreadedView();

                    int lastVisiblePosition = threadedView.getLastVisiblePosition();

                    Utils.debug(getClass(),"lastVisiblePosition:"+lastVisiblePosition+" "+parents.size());


                    RelativeLayout.LayoutParams layoutParams  = (RelativeLayout.LayoutParams) animationImage.getLayoutParams();
                    layoutParams.height = GlobalVars.SCREEN_WIDTH;
                    layoutParams.width = GlobalVars.SCREEN_WIDTH;


                    Utils.loadBitmap(item.getMediaId(), Api.ImageFormat.LARGE,animationImage,GlobalVars.SCREEN_WIDTH,GlobalVars.SCREEN_WIDTH,getActivity());
                    animationImage.setTop(0);
                    animationImage.setY(imageTopPosition);
                    animationImage.setVisibility(View.VISIBLE);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.play(ObjectAnimator.ofFloat(ItemFragmentV6.this,"animationImageSize",GlobalVars.SCREEN_WIDTH,Utils.dpToPixel(80,getActivity())));
                    animatorSet.setDuration(250);
                    animatorSet.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animator) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            AnimatorSet animatorSet = new AnimatorSet();
                            animatorSet.play(ObjectAnimator.ofFloat(animationImage,View.Y,animationImage.getY(),-GlobalVars.SCREEN_WIDTH));
                            animatorSet.setDuration(250);
                            animatorSet.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animator) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    parents.add(item);
                                    threadedV6Adapter.notifyDataSetChanged();
                                    threadedView.smoothScrollToPosition(parents.size() - 1);
                                    threadedView.setOnItemSelectedListener(new it.sephiroth.android.library.widget.AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(it.sephiroth.android.library.widget.AdapterView<?> adapterView, View view, int i, long l) {
                                            threadedView.setOnItemSelectedListener(null);

                                        }

                                        @Override
                                        public void onNothingSelected(it.sephiroth.android.library.widget.AdapterView<?> adapterView) {

                                        }
                                    });
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

                        @Override
                        public void onAnimationCancel(Animator animator) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animator) {

                        }
                    });
                    animatorSet.start();

                    //onItemClickListener.onClick(seemId,itemId);
                }

                @Override
                public void onProfileClick(String username) {
                    onItemClickListener.onProfileClick(username);
                }

                @Override
                public void onReplyFromCamera(String itemId) {
                    ActivityFactory.startReplyItemActivity(getActivity(), itemId, GlobalVars.PhotoSource.CAMERA);
                }

                @Override
                public void onReplyFromGallery(String itemId) {
                    ActivityFactory.startReplyItemActivity(getActivity(),itemId, GlobalVars.PhotoSource.GALLERY);
                }
            });
            itemViewAdapter.setShowFirst(getShowFirst());
            listView.setAdapter(itemViewAdapter);
            if(item.getDepth() == 0){
                showedThreadedView = false;
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) threadedView.getLayoutParams();
                layoutParams.topMargin=-layoutParams.height;

            }

            if(loadThreadView) {
                threadedV6Adapter = new ThreadedV6Adapter(parents, getActivity());
                threadedView.setAdapter(threadedV6Adapter);
                threadedView.smoothScrollToPosition(parents.size()-1);
                loadThreadView = false;
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

    private void showThreadedView(){
        if(!showedThreadedView) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(ObjectAnimator.ofFloat(threadedView, View.Y, -threadedView.getLayoutParams().height,0));
            animatorSet.setDuration(500);
            animatorSet.start();
            showedThreadedView = true;
        }
    }

    private  void hideThreadedView(){
        if(showedThreadedView) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(ObjectAnimator.ofFloat(threadedView, View.Y, 0,-threadedView.getLayoutParams().height));
            animatorSet.setDuration(500);
            animatorSet.start();
            showedThreadedView = false;
        }
    }


    public void setAnimationImageSize(float size){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) animationImage.getLayoutParams();
        layoutParams.height = (int) size;
        layoutParams.width = (int) size;
        layoutParams.leftMargin = (int) ((GlobalVars.SCREEN_WIDTH-size) / 2);
        layoutParams.topMargin = (int) ((GlobalVars.SCREEN_WIDTH-size) / 2);
        animationImage.setLayoutParams(layoutParams);
    }
}
