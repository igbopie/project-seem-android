package com.seem.android.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.seem.android.customviews.ItemView;
import com.seem.android.model.Item;
import com.seem.android.model.UserProfile;
import com.seem.android.util.ActionLauncherListener;

import java.util.List;

/**
 * Created by igbopie on 28/05/14.
 */
public class ItemViewAdapter  extends BaseAdapter {

    private List<Item> itemList;
    private Context context;
    private ActionLauncherListener actionLauncherListener;

    public ItemViewAdapter(List<Item> itemList, Context ctx) {
        super();
        this.itemList = itemList;
        this.context = ctx;;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int i) {
        return itemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return itemList.get(i).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if(convertView == null || convertView.getTag() == null || !convertView.getTag().equals(position)) {
            ItemView view;
            if (convertView == null) {
                view = new ItemView(context, null);
                view.setActionLauncherListener(new ActionLauncherListener() {
                    @Override
                    public void launchViewProfile(UserProfile userProfile) {
                        actionLauncherListener.launchViewProfile(userProfile);
                    }

                    @Override
                    public void launchSeeConversation(Item item) {
                        actionLauncherListener.launchSeeConversation(item);
                    }
                });
            } else {
                //Reusing views...
                view = (ItemView) convertView;
            }
            final Item item = (Item) getItem(position);
            ItemView.Theme theme= ItemView.Theme.REPLY;
            view.setItem(item,theme);


            return view;
        }

        return convertView;
    }

    public ActionLauncherListener getActionLauncherListener() {
        return actionLauncherListener;
    }

    public void setActionLauncherListener(ActionLauncherListener actionLauncherListener) {
        this.actionLauncherListener = actionLauncherListener;
    }
}
