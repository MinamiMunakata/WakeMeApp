package com.minami_m.project.android.wakemeapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment implements View.OnClickListener, inputValidationHandler {

    private FirebaseAuth mAuth;
    private Button signInBtn;
    private EditText editTextEmail;
    private EditText editTextPw;
    private TextView signUpLink;
    private TextView errorMsg;

    private  CallbackManager mCallbackManager;

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

        // ------------

        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = view.findViewById(R.id.fb_login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });

        // ------------

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    ((ActivityChangeListener)getActivity()).launchActivity(MainActivity.class);
                    Toast.makeText(getContext(), user.getEmail(), Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onAuthStateChanged: " + user.getUid() + " : " + user.getDisplayName() + " : " + user.getEmail());
                    return;
                } else {
                    // TODO: sign out
                }
            }
        });

        return view;
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // TODO: check if the user has an account
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
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
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        updateUI(true);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(getContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        errorMsg.setText(task.getException().getMessage());
                        return;
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
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
}
