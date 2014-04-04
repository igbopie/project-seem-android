package com.seem.android.mockup1.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
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
import com.seem.android.mockup1.exceptions.EmailAlreadyExistsException;
import com.seem.android.mockup1.exceptions.UsernameAlreadyExistsException;
import com.seem.android.mockup1.service.Api;
import com.seem.android.mockup1.util.Utils;

import java.util.regex.Pattern;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.seem.android.mockup1.fragments.SignUpFragment.SignUpInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class SignUpFragment extends Fragment {

    EditText emailEditText;
    EditText usernameEditText;
    EditText passwordEditText;
    Button signUpButton;

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final String USERNAME_PATTERN ="[A-Za-z0-9_\\-]+";
    private Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
    private Pattern usernamePattern = Pattern.compile(USERNAME_PATTERN);

    private SignUpInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SignUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance() {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    public SignUpFragment() {
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
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailEditText = (EditText) getView().findViewById(R.id.emailEditText);
        usernameEditText = (EditText) getView().findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) getView().findViewById(R.id.passwordEditText);
        signUpButton =  (Button) getView().findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString().trim();
                String username = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                if(!emailPattern.matcher(email).matches()){
                    Utils.dialog("Invalid Email","Check the spelling of the email",getActivity());
                    emailEditText.requestFocus();
                } else if(!usernamePattern.matcher(username).matches()){
                    Utils.dialog("Invalid username","Check the spelling of the username",getActivity());
                    usernameEditText.requestFocus();
                } else {
                    new SignUp(username,password,email).execute();
                }
            }
        });
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SignUpInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SignUpInteractionListener");
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
    public interface SignUpInteractionListener {
        public void hasLoggedIn();
    }

    public class SignUp extends AsyncTask<Void,Void,String>{

        private final ProgressDialog dialog = new ProgressDialog(getActivity());
        private String username;
        private String password;
        private String email;
        private boolean usernameAlreadyExists;
        private boolean emailAlreadyExists;

        public SignUp(String username, String password, String email) {
            this.username = username;
            this.password = password;
            this.email = email;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Signing up...");
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            usernameAlreadyExists= false;
            emailAlreadyExists = false;
            try {
                String token = Api.signUp(username, password, email);
                return token;
            } catch(UsernameAlreadyExistsException e){
                usernameAlreadyExists = true;
            } catch(EmailAlreadyExistsException e){
                emailAlreadyExists = true;
            }  catch (Exception e) {
                Utils.debug(getClass(),"Unkown Error",e);
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
            } else if(usernameAlreadyExists) {
                Utils.dialog("Username already exists","choose another one please",getActivity());
                usernameEditText.requestFocus();
            } else if(emailAlreadyExists) {
                Utils.dialog("Email already exists","choose another one please",getActivity());
                emailEditText.requestFocus();
            }else{
                Utils.dialog("Try Again","Something went wrong... please try again",getActivity());
            }
        }
    }



}
