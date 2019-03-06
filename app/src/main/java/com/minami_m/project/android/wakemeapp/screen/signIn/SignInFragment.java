package com.minami_m.project.android.wakemeapp.screen.signIn;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.common.handler.FontStyleHandler;
import com.minami_m.project.android.wakemeapp.common.helper.FBRealTimeDBHelper;
import com.minami_m.project.android.wakemeapp.common.listener.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.common.listener.FacebookLoginListener;
import com.minami_m.project.android.wakemeapp.common.listener.FragmentChangeListener;
import com.minami_m.project.android.wakemeapp.screen.main.MainActivity;

import java.util.Date;
import java.util.Objects;

import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment implements
        View.OnClickListener {

    private FirebaseAuth mAuth;
    private Button signInBtn;
    private EditText editTextEmail, editTextPw;
    private TextView signUpLink, errorMsg;
    private ImageView loadingImage, helpIconToResetPW;
    private RelativeLayout loadingBG;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        signInBtn = view.findViewById(R.id.sign_in_btn);
        FontStyleHandler.setFont(getContext(), signInBtn, false, true);
        setupInputFields(view);
        setupLoadingImage(view);
        // Initialize an error message field.
        errorMsg = view.findViewById(R.id.sign_in_error);
        errorMsg.setVisibility(View.GONE);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Facebook login
        setupFBLoginButton(view);
        // TODO: reset
        helpIconToResetPW = view.findViewById(R.id.help_to_reset_password);
        helpIconToResetPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInActivity activity = (SignInActivity) requireActivity();
                activity.showDialog();
            }
        });

        return view;
    }

    private void setupInputFields(View view) {
        editTextEmail = view.findViewById(R.id.edit_text_email_for_sign_in);
        FontStyleHandler.setFont(getContext(), editTextEmail, false, false);
        editTextPw = view.findViewById(R.id.edit_text_password_for_sign_in);
        FontStyleHandler.setFont(getContext(), editTextPw, false, false);
        signUpLink = view.findViewById(R.id.sign_up_link);
        FontStyleHandler.setFont(getContext(), signUpLink, false, true);
    }

    private void setupLoadingImage(View view) {
        loadingBG = view.findViewById(R.id.loading_bg);
        loadingImage = view.findViewById(R.id.sign_in_loading_img);
        Glide.with(this).load(R.raw.loading).into(loadingImage);
        loadingBG.setVisibility(View.INVISIBLE);
    }

    private void setupFBLoginButton(View view) {
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
        hideSoftKeyBoard();
        if (!TextUtils.isEmpty(editTextEmail.getText()) && !TextUtils.isEmpty(editTextPw.getText())) {
            final String email = editTextEmail.getText().toString().trim(),
                    password = editTextPw.getText().toString().trim();
            loadingBG.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    loadingBG.setVisibility(View.INVISIBLE);
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        final FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            updateUI(true);
                        }

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "onComplete: ", task.getException());
                        toast("Authentication failed.");
                        errorMsg.setVisibility(View.VISIBLE);
                        errorMsg.setText(Objects.requireNonNull(task.getException()).getMessage());
                    }
                }
            });
        } else {
            toast("Enter both email and password.");
        }

    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (Objects.requireNonNull(imm).isAcceptingText()) {
            imm.hideSoftInputFromWindow(Objects.requireNonNull(requireActivity().getCurrentFocus()).getWindowToken(), 0);
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

    private void toast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
