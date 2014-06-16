package com.seem.android.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seem.android.GlobalVars;
import com.seem.android.R;
import com.seem.android.model.Item;
import com.seem.android.service.Api;
import com.seem.android.util.ActionLauncherListener;
import com.seem.android.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by igbopie on 20/03/14.
 */
public class ItemView extends RelativeLayout implements View.OnClickListener, PopupMenu.OnMenuItemClickListener{

    public enum Theme{MAIN,MINIMAL_MAIN,REPLY};

    Theme theme = Theme.REPLY;
    ImageView itemMainImage;

    TextView name;
    ImageView userImage;
    TextView caption;
    TextView username;
    TextView date;
    ImageView moreOptionsIcon;
    TextView seeConversationText;
    ImageView seeConversationIcon;

    ActionLauncherListener actionLauncherListener;

    View mainContent;

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
        name = (TextView) findViewById(R.id.name);
        userImage = (ImageView) findViewById(R.id.userImage);
        moreOptionsIcon = (ImageView) findViewById(R.id.moreOptionsIcon);
        mainContent = findViewById(R.id.mainContent);


        itemMainImage = (ImageView) findViewById(R.id.itemMainImage);
        ViewGroup.LayoutParams layout = itemMainImage.getLayoutParams();
        layout.height = GlobalVars.SCREEN_WIDTH;
        layout.width = GlobalVars.SCREEN_WIDTH;
        itemMainImage.setLayoutParams(layout);


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


        seeConversationText = (TextView) findViewById(R.id.seeConversationText);
        seeConversationIcon = (ImageView) findViewById(R.id.seeConversationIcon);

        seeConversationText.setOnClickListener(this);
        seeConversationIcon.setOnClickListener(this);


    }


    public void setItem(final Item item,Theme theme){
        this.item = item;
        this.theme = theme;

        if(theme.equals(Theme.MAIN)){
            setBackgroundResource(R.color.SeemBlue);

            moreOptionsIcon.setImageResource(R.drawable.more_options);
            caption.setTextColor(Color.WHITE);
            name.setTextColor(Color.WHITE);
            username.setTextColor(Color.rgb(225, 225, 225));
            date.setTextColor(Color.WHITE);


            mainContent.setVisibility(VISIBLE);
        }else if (theme.equals(Theme.MINIMAL_MAIN)){

            mainContent.setVisibility(GONE);

        }else{
            setBackgroundColor(Color.WHITE);

            moreOptionsIcon.setImageResource(R.drawable.more_options_black);
            caption.setTextColor(Color.BLACK);
            name.setTextColor(Color.BLACK);
            username.setTextColor(Color.rgb(80,80,80));
            date.setTextColor(Color.BLACK);


            mainContent.setVisibility(VISIBLE);
        }

        caption.setText(item.getCaption());
        
        date.setText(Utils.getRelativeTime(item.getCreated()));

        if(item.getUserProfile() != null){
            name.setText(item.getUserProfile().getName());
            if(item.getUserProfile().getMediaId() != null) {
                Utils.loadBitmap(item.getUserProfile().getMediaId(), Api.ImageFormat.THUMB,userImage,userImage.getLayoutParams().width,userImage.getLayoutParams().width,getContext());
            }else{
                userImage.setImageResource(R.drawable.user_profile_nophoto);
            }
            username.setText("@"+item.getUserProfile().getUsername());
            View.OnClickListener  profileAction = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(actionLauncherListener != null) {
                        actionLauncherListener.launchViewProfile(item.getUserProfile());
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

        if(item.getReplyTo() != null){
            seeConversationIcon.setVisibility(VISIBLE);
            seeConversationText.setVisibility(VISIBLE);
        } else {
            seeConversationIcon.setVisibility(INVISIBLE);
            seeConversationText.setVisibility(INVISIBLE);
        }

        Utils.loadBitmap(item.getMediaId(), Api.ImageFormat.LARGE,itemMainImage,GlobalVars.SCREEN_WIDTH,GlobalVars.SCREEN_WIDTH,getContext());

    }


    public ActionLauncherListener getActionLauncherListener() {
        return actionLauncherListener;
    }

    public void setActionLauncherListener(ActionLauncherListener actionLauncherListener) {
        this.actionLauncherListener = actionLauncherListener;
    }








    @Override
    public void onClick(View view) {
        if((view.getId() == R.id.seeConversationIcon || view.getId() == R.id.seeConversationText) &&
                actionLauncherListener != null){
            actionLauncherListener.launchSeeConversation(item);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.actionCopyLink:
                String link = "http://seem-test.herokuapp.com/item/"+item.getId();
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", link);
                clipboard.setPrimaryClip(clip);
                return true;
            case R.id.actionRefresh:
                //new RefreshItem().execute();
                return true;
        }
        return false;
    }


}
