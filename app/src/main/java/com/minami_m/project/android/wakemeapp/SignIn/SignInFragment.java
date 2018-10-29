package com.minami_m.project.android.wakemeapp.SignIn;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.ProfileTracker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.minami_m.project.android.wakemeapp.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.FacebookLoginListener;
import com.minami_m.project.android.wakemeapp.FirebaseRealtimeDatabaseHelper;
import com.minami_m.project.android.wakemeapp.FragmentChangeListener;
import com.minami_m.project.android.wakemeapp.InputHandler;
import com.minami_m.project.android.wakemeapp.Main.MainActivity;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.inputValidationHandler;

import java.util.Date;

import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment implements
        View.OnClickListener,
        inputValidationHandler {

    private FirebaseAuth mAuth;
    private Button signInBtn;
    private EditText editTextEmail;
    private EditText editTextPw;
    private TextView signUpLink;
    private TextView errorMsg;
    private ProfileTracker profileTracker;
    private ProgressBar progressBar;

    private  CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTokenTracker;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        // Initialize Firebase Auth
        signInBtn = view.findViewById(R.id.sign_in_btn);
        editTextEmail = view.findViewById(R.id.edit_text_email_for_sign_in);
        editTextPw = view.findViewById(R.id.edit_text_password_for_sign_in);
        signUpLink = view.findViewById(R.id.sign_up_link);
        errorMsg = view.findViewById(R.id.sign_in_error);
        progressBar = view.findViewById(R.id.sign_in_progressbar);
        progressBar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (user.getDisplayName() != null) toast("Welcome " + user.getDisplayName());
//                    ((ActivityChangeListener)getActivity()).launchActivity(MainActivity.class);
                    return;
                } else {
                    // TODO: sign out
                }
            }
        });

        Button loginButton = view.findViewById(R.id.fb_login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignInActivity.setProgressBar(progressBar);
                FacebookLoginListener facebookLoginListener = (FacebookLoginListener)getActivity();
                facebookLoginListener.loginWithFacebook(mAuth);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            FirebaseRealtimeDatabaseHelper.updateStatusWithLoginTime(
                    mAuth.getCurrentUser().getUid(),
                    new Date().getTime());
            ((ActivityChangeListener)getActivity()).launchActivity(MainActivity.class);
        }
        signInBtn.setOnClickListener(this);
        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI(false);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (isValidInput()) {
            String email = editTextEmail.getText().toString(),
                    password = editTextPw.getText().toString();
            // TODO: show a progress bar
            progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.INVISIBLE);
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        updateUI(true);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        toast("Authentication failed.");
                        errorMsg.setText(task.getException().getMessage());
                        return;
                    }
                }
            });
        } else {
            toast("Invalid Input");
        }

    }

    private void updateUI(Boolean hasAccount) {
        if (hasAccount) {
            ((ActivityChangeListener)getActivity()).launchActivity(MainActivity.class);
        } else {
            displaySignUpForm();
        }
    }

    private void displaySignUpForm() {
        SignUpFragment signUpFragment = new SignUpFragment();
        FragmentChangeListener fragmentChangeListener = (FragmentChangeListener)getActivity();
        fragmentChangeListener.replaceFragment(signUpFragment);
    }

    @Override
    public boolean isValidInput() {
        if (!InputHandler.isValidFormEmail(editTextEmail)) return false;
        if (!InputHandler.isValidFormPW(editTextPw)) return false;
        return true;
    }

    private void toast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

}
