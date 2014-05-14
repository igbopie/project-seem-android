package com.seem.android.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.seem.android.GlobalVars;
import com.seem.android.MyApplication;
import com.seem.android.R;
import com.seem.android.model.UserProfile;
import com.seem.android.service.Api;
import com.seem.android.util.ActivityFactory;
import com.seem.android.util.Utils;

import java.io.FileNotFoundException;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.seem.android.fragments.UserProfileFragment.UserProfileInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class UserProfileFragment extends Fragment {

    private UserProfile userProfile;
    private EditText name;
    private EditText bio;
    private EditText email;
    private ImageView imageView;

    Uri localTempFile;


    private UserProfileInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserProfileFragment.
     */
    public static UserProfileFragment newInstance() {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button logoutButton = (Button)getView().findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApplication.logout();
                mListener.hasLoggedOut();
            }
        });

        Button saveButton = (Button)getView().findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Save().execute();
            }
        });

        name = (EditText)view.findViewById(R.id.name);
        bio = (EditText)view.findViewById(R.id.bio);
        email = (EditText)view.findViewById(R.id.email);

        imageView = (ImageView)view.findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(UserProfileFragment.this.getActivity(), view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.camera_popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.actionPopupCamera:
                                localTempFile = Utils.getNewFileUri();
                                ActivityFactory.startCamera(UserProfileFragment.this, localTempFile);
                                //ActivityFactory.startReplyItemActivity(UserProfileFragment.this, item.getId(), GlobalVars.PhotoSource.CAMERA);
                                return true;
                            case R.id.actionPopupGallery:
                                ActivityFactory.startGallery(UserProfileFragment.this);
                                //ActivityFactory.startReplyItemActivity(UserProfileFragment.this,item.getId(), GlobalVars.PhotoSource.GALLERY);
                                return true;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        new GetUserProfile().execute();

        if(savedInstanceState != null && savedInstanceState.containsKey(GlobalVars.SAVED_BUNDLE_CAMERA_OUT_FILE)) {
            localTempFile = Uri.parse(savedInstanceState.getString(GlobalVars.SAVED_BUNDLE_CAMERA_OUT_FILE));
        }



    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Utils.debug(this.getClass(), "onSaveInstanceState");
        if(localTempFile != null) {
            outState.putString(GlobalVars.SAVED_BUNDLE_CAMERA_OUT_FILE, localTempFile.getPath());
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (UserProfileInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement UserProfileInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface UserProfileInteractionListener {
        public void hasLoggedOut();
    }


    class GetUserProfile extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            userProfile = Api.getUserProfile(MyApplication.getUsername(),MyApplication.getToken());
            Utils.debug(getClass(),"UserProgile:"+ userProfile);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(userProfile != null){
                name.setText(userProfile.getName());
                bio.setText(userProfile.getBio());
                email.setText(userProfile.getEmail());
                if(userProfile.getMediaId() != null){
                    Utils.loadBitmap(userProfile.getMediaId(), Api.ImageFormat.THUMB,imageView,getActivity());
                }
            }

        }
    }

    class Save extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            String mediaId = null;
            if(localTempFile != null){
                try {
                    mediaId = Api.createMedia(getActivity().getContentResolver().openInputStream(localTempFile));
                } catch (FileNotFoundException e) {
                    Utils.debug(getClass(),"Error uploading profile image:"+e.getMessage());
                }
            }

            boolean saved = Api.updateUser(null,email.getText().toString(),name.getText().toString(),bio.getText().toString(),mediaId,MyApplication.getToken());
            Utils.debug(getClass(),"Saved:"+saved);
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Utils.debug(this.getClass(),"Create Seem Flow Activity OnActivityResult");
        if (requestCode == GlobalVars.RETURN_CODE_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            //Do nothing
        } else if(requestCode == GlobalVars.RETURN_CODE_GALLERY && resultCode == Activity.RESULT_OK){
            localTempFile =data.getData();
        }

        if(resultCode == Activity.RESULT_OK){
            Utils.loadStream(localTempFile,imageView,this.getActivity());

        }

    }
}
