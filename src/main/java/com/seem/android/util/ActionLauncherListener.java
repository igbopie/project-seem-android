package com.seem.android.util;

import com.seem.android.model.Item;
import com.seem.android.model.UserProfile;

/**
 * Created by igbopie on 16/06/14.
 */
public interface ActionLauncherListener {

    public void launchViewProfile(UserProfile userProfile);

    public void launchSeeConversation(Item item);
}
