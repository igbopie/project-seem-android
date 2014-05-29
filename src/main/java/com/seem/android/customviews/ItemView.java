package com.seem.android.customviews;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seem.android.GlobalVars;
import com.seem.android.MyApplication;
import com.seem.android.R;
import com.seem.android.adapters.ItemViewAdapter;
import com.seem.android.adapters.ThreadedV6Adapter;
import com.seem.android.model.Item;
import com.seem.android.service.Api;
import com.seem.android.service.ItemService;
import com.seem.android.util.ActivityFactory;
import com.seem.android.util.Utils;

import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.widget.AdapterView;
import it.sephiroth.android.library.widget.HListView;

/**
 * Created by igbopie on 20/03/14.
 */
public class ItemView extends RelativeLayout implements View.OnClickListener, PopupMenu.OnMenuItemClickListener{

    public enum Theme{MAIN,REPLY};

    Theme theme = Theme.REPLY;
    ImageView itemMainImage;

    ImageView thumbUpIcon;
    ImageView thumbDownIcon;
    ImageView favIcon;
    TextView name;
    ImageView userImage;
    TextView caption;
    TextView username;
    TextView date;
    TextView thumbsUpScore;
    TextView thumbsDownScore;
    TextView favsScore;
    TextView commentsNumber;
    TextView commentsNumberBig;
    ImageView replyIcon;
    ImageView moreOptionsIcon;
    ImageView commentsIcon;

    View bigCommentsSection;
    OnItemClickListener onItemClickListener;

    HListView threadedView;
    ThreadedV6Adapter threadedV6Adapter;

    Item item;
    List<Item> parents = new ArrayList<Item>();

    public ItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, new int[]{}, 0, 0);

        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.component_item_view, this, true);

        caption = (TextView) findViewById(R.id.caption);
        username = (TextView) findViewById(R.id.username);
        date = (TextView) findViewById(R.id.date);
        thumbsUpScore = (TextView) findViewById(R.id.thumbsUpScore);
        thumbsDownScore = (TextView) findViewById(R.id.thumbsDownScore);
        favsScore = (TextView) findViewById(R.id.favsScore);
        thumbUpIcon = (ImageView) findViewById(R.id.thumbUpIcon);
        thumbDownIcon = (ImageView) findViewById(R.id.thumbDownIcon);
        favIcon = (ImageView) findViewById(R.id.favIcon);
        name = (TextView) findViewById(R.id.name);
        userImage = (ImageView) findViewById(R.id.userImage);
        commentsNumber = (TextView) findViewById(R.id.commentsNumber);
        replyIcon = (ImageView) findViewById(R.id.replyIcon);
        moreOptionsIcon = (ImageView) findViewById(R.id.moreOptionsIcon);
        commentsIcon = (ImageView) findViewById(R.id.commentsIcon);
        commentsNumberBig = (TextView) findViewById(R.id.commentsNumberBig);


        threadedView = (HListView) findViewById(R.id.threadedView);

        itemMainImage = (ImageView) findViewById(R.id.itemMainImage);
        ViewGroup.LayoutParams layout = itemMainImage.getLayoutParams();
        layout.height = GlobalVars.SCREEN_WIDTH;
        layout.width = GlobalVars.SCREEN_WIDTH;
        itemMainImage.setLayoutParams(layout);


        bigCommentsSection = findViewById(R.id.bigCommentsSection);
        moreOptionsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getContext(), moreOptionsIcon);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.item_more_options, popup.getMenu());
                popup.setOnMenuItemClickListener(ItemView.this);
                popup.show();
            }
        });

        replyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getContext(), replyIcon);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.camera_popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(ItemView.this);
                popup.show();
            }
        });

        favIcon.setOnClickListener(this);
        thumbUpIcon.setOnClickListener(this);
        thumbDownIcon.setOnClickListener(this);

        threadedView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(onItemClickListener != null) {
                    Item clicked = parents.get(i);
                    onItemClickListener.onClick(clicked.getSeemId(), clicked.getId());
                }
            }
        });


    }


    public void setItem(final Item item,Theme theme){
        this.item = item;
        this.theme = theme;
        if(theme.equals(Theme.MAIN)){
            setBackgroundResource(R.color.SeemBlue);

            moreOptionsIcon.setImageResource(R.drawable.more_options);
            commentsIcon.setImageResource(R.drawable.comments);
            replyIcon.setImageResource(R.drawable.replybig);
            commentsNumber.setTextColor(Color.WHITE);
            caption.setTextColor(Color.WHITE);
            thumbsUpScore.setTextColor(Color.WHITE);
            thumbsDownScore.setTextColor(Color.WHITE);
            favsScore.setTextColor(Color.WHITE);
            name.setTextColor(Color.WHITE);
            username.setTextColor(Color.rgb(225, 225, 225));
            date.setTextColor(Color.WHITE);

            bigCommentsSection.setVisibility(VISIBLE);
            commentsIcon.setVisibility(INVISIBLE);
            commentsNumber.setVisibility(INVISIBLE);
            if(item.getDepth()>0) {
                threadedView.setVisibility(VISIBLE);
                loadThreadedView();
            }else{
                threadedView.setVisibility(GONE);
            }
        }else{
            setBackgroundColor(Color.WHITE);

            moreOptionsIcon.setImageResource(R.drawable.more_options_black);
            commentsIcon.setImageResource(R.drawable.comments_black);
            replyIcon.setImageResource(R.drawable.reply_black);
            commentsNumber.setTextColor(Color.BLACK);
            caption.setTextColor(Color.BLACK);
            thumbsUpScore.setTextColor(Color.BLACK);
            thumbsDownScore.setTextColor(Color.BLACK);
            favsScore.setTextColor(Color.BLACK);
            name.setTextColor(Color.BLACK);
            username.setTextColor(Color.rgb(80,80,80));
            date.setTextColor(Color.BLACK);


            threadedView.setVisibility(GONE);
            bigCommentsSection.setVisibility(GONE);
            commentsIcon.setVisibility(VISIBLE);
            commentsNumber.setVisibility(VISIBLE);
        }

        commentsNumber.setText(item.getReplyCount()+"");
        commentsNumberBig.setText(item.getReplyCount()+"");

        caption.setText(item.getCaption());
        
        date.setText(Utils.getRelativeTime(item.getCreated()));
        thumbsUpScore.setText(""+item.getThumbUpCount());
        thumbsDownScore.setText(""+item.getThumbDownCount());
        favsScore.setText(""+item.getFavouriteCount());
        if(item.isFavourited() && theme.equals(Theme.MAIN)){
            favIcon.setImageResource(R.drawable.star);
        }else if (!item.isFavourited() && theme.equals(Theme.MAIN)){
            favIcon.setImageResource(R.drawable.star_o);
        }else if(item.isFavourited() && theme.equals(Theme.REPLY)){
            favIcon.setImageResource(R.drawable.star_black);
        }else{
            favIcon.setImageResource(R.drawable.star_o_black);
        }

        if(item.getThumbedUp() && theme.equals(Theme.REPLY)){
            thumbUpIcon.setImageResource(R.drawable.thumbs_up_black);
        } else if(item.getThumbedUp() && theme.equals(Theme.MAIN)){
            thumbUpIcon.setImageResource(R.drawable.thumbs_up);
        } else if(!item.getThumbedUp() && theme.equals(Theme.MAIN)){
            thumbUpIcon.setImageResource(R.drawable.thumbs_o_up);
        } else{
            thumbUpIcon.setImageResource(R.drawable.thumbs_o_up_black);
        }

        if(item.getThumbedDown() && theme.equals(Theme.MAIN)){
            thumbDownIcon.setImageResource(R.drawable.thumbs_down);
        }else if(item.getThumbedDown() && theme.equals(Theme.REPLY)){
            thumbDownIcon.setImageResource(R.drawable.thumbs_down_black);
        }else if(!item.getThumbedDown() && theme.equals(Theme.MAIN)){
            thumbDownIcon.setImageResource(R.drawable.thumbs_o_down);
        }else{
            thumbDownIcon.setImageResource(R.drawable.thumbs_o_down_black);
        }

        if(item.getUserProfile() != null){
            name.setText(item.getUserProfile().getName());
            if(item.getUserProfile().getMediaId() != null) {
                Utils.loadBitmap(item.getUserProfile().getMediaId(), Api.ImageFormat.THUMB,userImage,getContext());
            }else{
                userImage.setImageResource(R.drawable.user_profile_nophoto);
            }
            username.setText("@"+item.getUserProfile().getUsername());
            View.OnClickListener  profileAction = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onItemClickListener!= null) {
                        onItemClickListener.onProfileClick(item.getUserProfile().getUsername());
                    }
                }
            };
            userImage.setOnClickListener(profileAction);
            name.setOnClickListener(profileAction);
        } else{
            name.setText("");
            username.setText("");
            userImage.setOnClickListener(null);
            username.setOnClickListener(null);
        }

        Utils.loadBitmap(item.getMediaId(), Api.ImageFormat.LARGE,itemMainImage,getContext());

    }


    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onClick(String seemId, String itemId);

        public void onProfileClick(String username);

        public void onReplyFromCamera(String itemId);

        public void onReplyFromGallery(String itemId);
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

    abstract class ActionTask extends AsyncTask<Void,Void,Boolean> {


        private final ProgressDialog dialog = new ProgressDialog(getContext());

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            if(!success){
                dialog.dismiss();
                Utils.dialog("Error","Action could not be completed, check connection and try again later.",getContext());
            }else{
                RefreshItem refreshItem = new RefreshItem(){
                    @Override
                    protected void onPostExecute(Void result) {
                        super.onPostExecute(result);
                        dialog.dismiss();
                    }
                };
                refreshItem.execute();
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



    class RefreshItem extends AsyncTask<Void,Void,Void> {

        Item item;
        @Override
        protected Void doInBackground(Void... voids) {
            item = ItemService.getInstance().findItemById(ItemView.this.item.getId(),true,true);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            setItem(item,theme);
        }
    }
    @Override
    public void onClick(View view) {
        if (view.equals(favIcon)) {
            if (item.isFavourited() != null && item.isFavourited()) {
                new UnfavouriteTask().execute();
            } else {
                new FavouriteTask().execute();
            }
        } else if (view.equals(thumbUpIcon)) {
            if (item.getThumbedUp() != null && item.getThumbedUp()) {
                new ThumbClearTask().execute();
            } else {
                new ThumbUpTask().execute();
            }
        } else if (view.equals(thumbDownIcon)) {
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
                onItemClickListener.onReplyFromCamera(item.getId());
                return true;
            case R.id.actionPopupGallery:
                onItemClickListener.onReplyFromGallery(item.getId());
                return true;
            case R.id.actionCopyLink:
                String link = "http://seem-test.herokuapp.com/item/"+item.getId();
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", link);
                clipboard.setPrimaryClip(clip);
                return true;
            case R.id.actionRefresh:
                new RefreshItem().execute();
                return true;
        }
        return false;
    }

    private void loadThreadedView(){
        new LoadThreadView().execute();

    }
    private class LoadThreadView extends AsyncTask<Void,Void,Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... id) {
            Utils.debug(this.getClass(), "This is the item:" + item);
            parents.clear();
            Item parentItem = item;
            while(parentItem.getReplyTo() != null){
                parentItem = ItemService.getInstance().findItemById(parentItem.getReplyTo());
                parents.add(0,parentItem);
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            threadedV6Adapter = new ThreadedV6Adapter(parents,getContext());
            threadedView.setAdapter(threadedV6Adapter);

        }
    }
}
