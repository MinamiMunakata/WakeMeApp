package com.minami_m.project.android.wakemeapp.screen.signIn;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.ProfileTracker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.common.handler.FontStyleHandler;
import com.minami_m.project.android.wakemeapp.common.handler.InputHandler;
import com.minami_m.project.android.wakemeapp.common.handler.InputValidationHandler;
import com.minami_m.project.android.wakemeapp.common.helper.FBRealTimeDBHelper;
import com.minami_m.project.android.wakemeapp.common.listener.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.common.listener.FacebookLoginListener;
import com.minami_m.project.android.wakemeapp.common.listener.FragmentChangeListener;
import com.minami_m.project.android.wakemeapp.screen.main.MainActivity;

import java.util.Date;

import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment implements
        View.OnClickListener,
        InputValidationHandler {

    private FirebaseAuth mAuth;
    private Button signInBtn;
    private EditText editTextEmail, editTextPw;
    private TextView signUpLink, errorMsg;
    private ProfileTracker profileTracker;
    //    private String emailForSignUp = null;
//    private String passwordForSignUp = null;
    private ImageView loadingImage;
    private RelativeLayout loadingBG;

    private CallbackManager mCallbackManager;
    private AccessTokenTracker mAccessTokenTracker;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        // Initialize Firebase Auth
        signInBtn = view.findViewById(R.id.sign_in_btn);
        FontStyleHandler.setFont(getContext(), signInBtn, false, true);
        editTextEmail = view.findViewById(R.id.edit_text_email_for_sign_in);
        FontStyleHandler.setFont(getContext(), editTextEmail, false, false);
        editTextPw = view.findViewById(R.id.edit_text_password_for_sign_in);
        FontStyleHandler.setFont(getContext(), editTextPw, false, false);
        signUpLink = view.findViewById(R.id.sign_up_link);
        FontStyleHandler.setFont(getContext(), signUpLink, false, true);
        loadingBG = view.findViewById(R.id.loading_bg);
        loadingImage = view.findViewById(R.id.sign_in_loading_img);
        Glide.with(this).load(R.raw.loading).into(loadingImage);
        loadingBG.setVisibility(View.INVISIBLE);
        errorMsg = view.findViewById(R.id.sign_in_error);
        errorMsg.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        Button loginButton = view.findViewById(R.id.fb_login_button);
        FontStyleHandler.setFont(getContext(), loginButton, false, true);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SignInActivity) requireActivity()).setLoadingImage(loadingBG, loadingImage, getContext());
                FacebookLoginListener facebookLoginListener = (FacebookLoginListener) requireActivity();
                facebookLoginListener.loginWithFacebook(mAuth);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            FBRealTimeDBHelper.updateLoginTime(
                    mAuth.getCurrentUser().getUid(),
                    new Date().getTime());
            ((ActivityChangeListener) requireActivity()).launchActivity(MainActivity.class);
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
        InputHandler.hideSoftKeyBoard(requireActivity());
        if (isValidInput()) {
            final String email = editTextEmail.getText().toString(),
                    password = editTextPw.getText().toString();
            loadingBG.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    loadingBG.setVisibility(View.INVISIBLE);
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        final FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
//                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
//                                toast("Please create an account.");
//                                emailForSignUp = email;
//                                passwordForSignUp = password;
//                                updateUI(false);
//                            }
//                            FBRealTimeDBHelper.updateLoginTime(
//                                    currentUser.getUid(),
//                                    new Date().getTime());
                            updateUI(true);
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "onComplete: ", task.getException());
                        toast("Authentication failed.");
                        errorMsg.setVisibility(View.VISIBLE);
                        errorMsg.setText(task.getException().getMessage());
                    }
                }
            });
        } else {
            toast("Invalid Input");
        }

    }

    private void updateUI(Boolean hasAccount) {
        if (hasAccount) {
            ((ActivityChangeListener) requireActivity()).launchActivity(MainActivity.class);
        } else {
            displaySignUpForm();
        }
    }

    private void displaySignUpForm() {
        SignUpFragment signUpFragment = new SignUpFragment();
//        SignUpFragment signUpFragment = SignUpFragment.newInstance(emailForSignUp, passwordForSignUp);
        FragmentChangeListener fragmentChangeListener = (FragmentChangeListener) requireActivity();
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
