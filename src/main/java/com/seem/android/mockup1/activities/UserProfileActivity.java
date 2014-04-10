package com.seem.android.mockup1.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.seem.android.mockup1.GlobalVars;
import com.seem.android.mockup1.MyApplication;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.model.Seem;
import com.seem.android.mockup1.model.UserProfile;
import com.seem.android.mockup1.service.Api;
import com.seem.android.mockup1.service.SeemService;
import com.seem.android.mockup1.util.Utils;

import java.util.List;

public class UserProfileActivity extends Activity {

    TextView usernameTextView;
    TextView followingCountTextView;
    TextView followersCountTextView;
    TextView isFollowingMeTextView;

    Button followButton;

    UserProfile userProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        usernameTextView = (TextView) findViewById(R.id.usernameTextView);
        followingCountTextView = (TextView) findViewById(R.id.followingCountTextView);
        followersCountTextView = (TextView) findViewById(R.id.followersCountTextView);
        isFollowingMeTextView  = (TextView) findViewById(R.id.isFollowingYouTextView);

        followButton = (Button) findViewById(R.id.followButton);
        isFollowingMeTextView.setVisibility(View.INVISIBLE);
        if(!MyApplication.isLoggedIn()){
            followButton.setVisibility(View.INVISIBLE);
        }
        if(MyApplication.isLoggedIn()) {

            followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new FollowAction().execute();
                }
            });

        }

        usernameTextView.setText("@"+getUsername()+"");

        new GetUserprofileTask().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getUsername(){
        return getIntent().getStringExtra(GlobalVars.EXTRA_USERNAME);
    }
    private class GetUserprofileTask extends AsyncTask<Void,Void,Void> {
        private final ProgressDialog dialog = new ProgressDialog(UserProfileActivity.this);

        private GetUserprofileTask() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Loading...");
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            userProfile = Api.getUserProfile(getUsername(),MyApplication.getToken());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if(userProfile != null){
                followersCountTextView.setText(userProfile.getFollowers()+"");
                followingCountTextView.setText(userProfile.getFollowing()+"");
                if(userProfile.getIsFollowedByMe() != null && userProfile.getIsFollowedByMe()){
                    followButton.setText("Unfollow");
                } else if(userProfile.getIsFollowedByMe() != null){
                    followButton.setText("Follow");
                } else{
                    followButton.setVisibility(View.INVISIBLE);
                }

                if(userProfile.getIsFollowingMe() != null && userProfile.getIsFollowingMe()) {
                    isFollowingMeTextView.setVisibility(View.VISIBLE);
                }
            }

            dialog.dismiss();
        }
    }

    private class FollowAction extends  AsyncTask<Void,Void,Boolean> {

        private final ProgressDialog dialog = new ProgressDialog(UserProfileActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Wait...");
            dialog.show();
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            if(userProfile.getIsFollowedByMe() != null &&
                    userProfile.getIsFollowedByMe()     ) {
                return Api.unfollow(getUsername(), MyApplication.getToken());
            } else {
                return Api.follow(getUsername(), MyApplication.getToken());
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if(aBoolean){
                userProfile.setIsFollowedByMe(!userProfile.getIsFollowedByMe());
            }

            if(userProfile.getIsFollowedByMe()){
                followButton.setText("Unfollow");
            } else {
                followButton.setText("Follow");
            }

            dialog.dismiss();
        }
    }


}
