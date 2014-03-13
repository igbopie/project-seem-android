package com.seem.android.mockup1.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.seem.android.mockup1.AppSingleton;
import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.Utils;
import com.seem.android.mockup1.model.Reply;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by igbopie on 13/03/14.
 */
public class ReplyFragment extends android.support.v4.app.Fragment implements Observer{

    private ReplyFragmentSelectedListener mCallback;
    /**
     * Create a new instance of ReplyFragment
     */
    public static ReplyFragment newInstance(){
        return newInstance(-1,0);
    }
    public static ReplyFragment newInstance(int replyId,int depth) {
        ReplyFragment f = new ReplyFragment();

        Bundle args = new Bundle();

        args.putInt(GlobalVars.EXTRA_REPLY_ID, replyId);
        args.putInt(GlobalVars.EXTRA_DEPTH,depth);

        f.setArguments(args);

        return f;
    }


    private static ViewGroup.LayoutParams layoutParamsForSmallReplies;

    private ImageView image;
    private Map<ImageView,Reply> images = new HashMap<ImageView,Reply>();

    private ImageView currentImage;
    private GridLayout gridLayout;

    private Reply reply;
    private Reply replyInProgress;

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

        getView().findViewById(R.id.gridLayout).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(layoutParamsForSmallReplies == null) {
                    int parentWidth = gridLayout.getWidth();
                    Utils.debug("Width:"+parentWidth);

                    layoutParamsForSmallReplies = new LinearLayout.LayoutParams(parentWidth / 4, parentWidth / 4);
                }
            }
        });


        currentImage = image;

        paintReply();

        for(Reply replyReply:reply.getReplyList()) {
            replyReply.addObserver(this);
        }

        super.onActivityCreated(savedInstanceState);
    }

    public void paintReply(){
        gridLayout.removeAllViews();

        if(reply == null){
            reply = AppSingleton.getInstance().findReplyById(getReplyId());
        }

        if(reply.getImageUri() == null) {
            currentImage.setOnClickListener(new TakeAPictureClickHandler());
        }else{
            currentImage.setImageBitmap(reply.getImageBitmap());
            for(Reply replyReply:reply.getReplyList()){

                currentImage = new ImageView(getView().getContext());

                if(replyReply.getReplyList().size()>0) {
                    Resources r = getResources();
                    Drawable[] layers = new Drawable[2];
                    Drawable d = new BitmapDrawable(getResources(), replyReply.getImageBitmap());
                    layers[1] = r.getDrawable(R.drawable.withreplies);
                    layers[0] = d;
                    LayerDrawable layerDrawable = new LayerDrawable(layers);
                    currentImage.setImageDrawable(layerDrawable);
                }else {
                    currentImage.setImageBitmap(replyReply.getImageBitmap());
                }
                currentImage.setOnClickListener(new GoToReplyClickHandler());
                currentImage.setLayoutParams(layoutParamsForSmallReplies);
                Utils.debug("Adding image to grid:" + replyReply.getId());
                gridLayout.addView(currentImage, 0);

                images.put(currentImage, replyReply);
            }

            currentImage = new ImageView(getView().getContext());
            currentImage.setImageResource(R.drawable.boton);
            currentImage.setOnClickListener(new TakeAPictureClickHandler());
            currentImage.setLayoutParams(layoutParamsForSmallReplies);
            gridLayout.addView(currentImage,0);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (ReplyFragmentSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ReplyFragmentSelectedListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroy() {
        for(Reply replyReply:reply.getReplyList()) {
            replyReply.deleteObserver(this);
        }

        super.onDestroy();
        Utils.debug("Destroing:"+getReplyId());

    }

    public int getReplyId() {
        int defaultValue = -1;
        if(getArguments() == null){
            return defaultValue;
        }
        return getArguments().getInt(GlobalVars.EXTRA_REPLY_ID, defaultValue);
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
        Utils.debug("I am notified! I have to refresh:"+reply.getId());
        paintReply();
    }

    class TakeAPictureClickHandler implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if(reply.getImageUri() != null) {
                replyInProgress = new Reply();
                replyInProgress.setId(AppSingleton.getInstance().getNewImageId());
            }else {
                replyInProgress = reply;
            }
            replyInProgress.setImageUri(Utils.getNewFileUri(replyInProgress.getId()));

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, replyInProgress.getImageUri());

            startActivityForResult(cameraIntent, GlobalVars.TAKE_PHOTO_CODE);


        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GlobalVars.TAKE_PHOTO_CODE && resultCode == Activity.RESULT_OK) {
            Utils.debug("Pic taken");

            //Controller Logic
            replyInProgress.setImageBitmap(Utils.shrinkBitmap(replyInProgress.getImageUri().getPath()));
            if(reply != null && reply != replyInProgress){
                replyInProgress.addObserver(this);
                reply.addReply(replyInProgress);
            }
            images.put(currentImage,replyInProgress);
            AppSingleton.getInstance().saveReply(replyInProgress);


            //View Logic
            currentImage.setImageBitmap(replyInProgress.getImageBitmap());

            if(currentImage == image) {
                currentImage.setOnClickListener(null);//
            }else {
                currentImage.setOnClickListener(new GoToReplyClickHandler());//
            }

            currentImage = new ImageView(getView().getContext());
            currentImage.setImageResource(R.drawable.boton);
            currentImage.setOnClickListener(new TakeAPictureClickHandler());
            currentImage.setLayoutParams(layoutParamsForSmallReplies);
            gridLayout.addView(currentImage,0);


            replyInProgress = null;
        }
    }


    class GoToReplyClickHandler implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(view instanceof ImageView) {
                ImageView imageView = (ImageView)view;
                Reply reply = images.get(imageView);

                mCallback.replySelected(reply.getId(),getDepth()+1);
            }

        }

    }
}
