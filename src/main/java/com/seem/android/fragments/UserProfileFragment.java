package com.seem.android.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.seem.android.GlobalVars;
import com.seem.android.MyApplication;
import com.seem.android.R;
import com.seem.android.model.UserProfile;
import com.seem.android.service.Api;
import com.seem.android.util.ActivityFactory;
import com.seem.android.util.Utils;

public class UserProfileFragment extends Fragment {

    public static UserProfileFragment newInstance(String username) {
        UserProfileFragment f = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(GlobalVars.EXTRA_USERNAME, username);
        f.setArguments(args);
        return f;
    }
    public String getUsername(){
        if(getArguments() != null) {
            String username = getArguments().getString(GlobalVars.EXTRA_USERNAME);
            return username;
        }else {
            return null;
        }
    }


    TextView name;
    TextView bio;
    TextView usernameTextView;
    TextView publishedCountTextView;
    TextView favsCountTextView;
    TextView followingCountTextView;
    TextView followersCountTextView;
    TextView isFollowingMeTextView;

    Button followButton;

    UserProfile userProfile;

    private ImageView imageView;

    OnProfileListener listener;


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
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        name = (TextView) view.findViewById(R.id.name);
        bio = (TextView) view.findViewById(R.id.bio);
        usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
        followingCountTextView = (TextView) view.findViewById(R.id.followingCountTextView);
        followersCountTextView = (TextView) view.findViewById(R.id.followersCountTextView);
        publishedCountTextView = (TextView) view.findViewById(R.id.publishedCountTextView);
        favsCountTextView = (TextView) view.findViewById(R.id.favsCountTextView);
        isFollowingMeTextView  = (TextView) view.findViewById(R.id.isFollowingYouTextView);

        imageView =(ImageView) view.findViewById(R.id.imageView);

        followButton = (Button) view.findViewById(R.id.followButton);
        isFollowingMeTextView.setVisibility(View.INVISIBLE);
        if(!MyApplication.isLoggedIn()){
            followButton.setVisibility(View.INVISIBLE);
        }
        if(MyApplication.isLoggedIn()) {

            followButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MyApplication.getUsername().equals(userProfile.getUsername())){
                        //DO SOMETHING HERE
                        listener.editProfile();
                    }else {
                        new FollowAction().execute();
                    }
                }
            });

        }

        usernameTextView.setText("@"+getUsername()+"");

        new GetUserprofileTask().execute();
    }

    /*
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
    }*/


    private class GetUserprofileTask extends AsyncTask<Void,Void,Void> {
        private final ProgressDialog dialog = new ProgressDialog(UserProfileFragment.this.getActivity());

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

                usernameTextView.setText("@"+userProfile.getUsername());
                name.setText(userProfile.getName());
                bio.setText(userProfile.getBio());

                followersCountTextView.setText(userProfile.getFollowers()+"");
                followingCountTextView.setText(userProfile.getFollowing()+"");
                favsCountTextView.setText(userProfile.getFavourites()+"");
                publishedCountTextView.setText(userProfile.getPublished()+"");

                if(MyApplication.getUsername().equals(userProfile.getUsername())) {
                    followButton.setText("Edit profile");
                } else if(userProfile.getIsFollowedByMe() != null && userProfile.getIsFollowedByMe()){
                    followButton.setText("Unfollow");
                } else if(userProfile.getIsFollowedByMe() != null){
                    followButton.setText("Follow");
                } else{
                    followButton.setVisibility(View.INVISIBLE);
                }
                if(userProfile.getMediaId() != null){
                    Utils.loadBitmap(userProfile.getMediaId(), Api.ImageFormat.THUMB,imageView,imageView.getWidth(),imageView.getWidth(),getActivity());
                }

                if(userProfile.getIsFollowingMe() != null && userProfile.getIsFollowingMe()) {
                    isFollowingMeTextView.setVisibility(View.VISIBLE);
                }

            }

            dialog.dismiss();
        }
    }

    private class FollowAction extends  AsyncTask<Void,Void,Boolean> {

        private final ProgressDialog dialog = new ProgressDialog(UserProfileFragment.this.getActivity());

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
            if(MyApplication.getUsername().equals(userProfile.getUsername())) {
                followButton.setText("Edit profile");
            }else if(userProfile.getIsFollowedByMe()){
                followButton.setText("Unfollow");
            } else {
                followButton.setText("Follow");
            }

            dialog.dismiss();
        }
    }

    public interface OnProfileListener {
        public void editProfile();

        public void hasLoggedOut();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnProfileListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement UserProfileInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.user_profile, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        if(id == R.id.signOut){
            MyApplication.logout();
            listener.hasLoggedOut();
            return true;
        }


        return super.onOptionsItemSelected(menuItem);
    }



}
