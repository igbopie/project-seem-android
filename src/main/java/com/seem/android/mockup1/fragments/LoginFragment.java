package com.seem.android.mockup1.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.seem.android.mockup1.MyApplication;
import com.seem.android.mockup1.R;
import com.seem.android.mockup1.service.Api;
import com.seem.android.mockup1.util.Utils;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.seem.android.mockup1.fragments.LoginFragment.OnLoggedInInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class LoginFragment extends Fragment {

    private Button loginButton;
    private EditText username;
    private EditText password;

    private OnLoggedInInteractionListener mListener;

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public LoginFragment() {
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loginButton = (Button) getView().findViewById(R.id.loginButton);
        username = (EditText) getView().findViewById(R.id.usernameEditText);
        password = (EditText) getView().findViewById(R.id.passwordEditText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoginAction(username.getText().toString(),password.getText().toString()).execute();
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLoggedInInteractionListener) activity;
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
    public interface OnLoggedInInteractionListener {
        public void hasLoggedIn();
    }

    class LoginAction extends AsyncTask<Void,Void,String>{

        private final ProgressDialog dialog = new ProgressDialog(LoginFragment.this.getActivity());
        private String username;
        private String password;

        LoginAction(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Logging in...");
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                //TODO store in account manager
                String token = Api.login(username,password);
                Utils.debug(getClass(),"Auth:"+token);
                return token;

            } catch (Exception e) {
                Utils.debug(getClass(),"Error athenticating...",e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String token) {
            super.onPostExecute(token);

            dialog.dismiss();
            if(token != null) {
                MyApplication.login(username, password, token);
                mListener.hasLoggedIn();
            } else {
                MyApplication.logout();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginFragment.this.getActivity());

                // set title
                alertDialogBuilder.setTitle("Ah ah ah, you didn't say the magic word");

                // set dialog message
                alertDialogBuilder
                        .setMessage("Invalid username or password")
                        .setCancelable(false)
                        .setNeutralButton("Oookey", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close
                                // current activity
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }

        }
    }

}
