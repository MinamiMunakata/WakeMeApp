package com.minami_m.project.android.wakemeapp.screen.signIn;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.common.helper.FBRealTimeDBHelper;
import com.minami_m.project.android.wakemeapp.common.listener.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.common.listener.FacebookLoginListener;
import com.minami_m.project.android.wakemeapp.common.listener.FragmentChangeListener;
import com.minami_m.project.android.wakemeapp.common.listener.SignInListener;
import com.minami_m.project.android.wakemeapp.model.User;
import com.minami_m.project.android.wakemeapp.screen.main.MainActivity;

import java.util.Arrays;
import java.util.Objects;

import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;

public class SignInActivity extends AppCompatActivity implements FragmentChangeListener,
        ActivityChangeListener, FacebookLoginListener, SignInListener {

    private static final String EMAIL = "email";
    ImageView loadingImage;
    RelativeLayout loadingBG;
    CallbackManager callbackManager;
    FirebaseAuth mAuth;
    Profile facebookProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.minami_m.project.android.wakemeapp.R.layout.activity_sign_in);
        // create a new fragment to be placed in the activity layout
        createNewFragment(savedInstanceState);

        callbackManager = CallbackManager.Factory.create();
        // There is an error with out logout
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel: facebook login is canceled.");
                loadingBG.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "onError: ", error);
                loadingBG.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void createNewFragment(Bundle savedInstanceState) {
        if (findViewById(R.id.signin_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            SignInFragment firstFragment = new SignInFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.signin_container, firstFragment).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void replaceFragment(Fragment newFragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back
        transaction.replace(R.id.signin_container, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void launchActivity(Class nextActivity) {
        Intent intent = new Intent(this, nextActivity);
        startActivity(intent);
    }

    @Override
    public void loginWithFacebook(FirebaseAuth firebaseAuth) {
        if (loadingBG != null) {
            loadingBG.setVisibility(View.VISIBLE);
        }
        mAuth = firebaseAuth;
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
            loadingBG.setVisibility(View.INVISIBLE);

        } else {
            LoginManager.getInstance().logOut();
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(EMAIL));
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Check if the usr sign in for the first time.
                                boolean isNewUser = Objects.requireNonNull(task.getResult()).getAdditionalUserInfo().isNewUser();
                                if (isNewUser) {
                                    facebookProfile = Profile.getCurrentProfile();
                                    String avatar = facebookProfile.getProfilePictureUri(300, 300).toString();
                                    User newUser = new User(user.getUid(),
                                            facebookProfile.getName(),
                                            user.getEmail(),
                                            avatar);
                                    FBRealTimeDBHelper.writeNewUser(newUser);
                                }
                            }
                            loadingBG.setVisibility(View.INVISIBLE);
                            launchActivity(MainActivity.class);

                        } else {
                            loadingBG.setVisibility(View.INVISIBLE);
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void setLoadingImage(RelativeLayout loadingBackground, ImageView loadingImg, Context context) {
        this.loadingImage = loadingImg;
        Glide.with(context).load(R.raw.loading).into(loadingImg);
        this.loadingBG = loadingBackground;
        this.loadingBG.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showDialog() {
        FragmentTransaction manager = getSupportFragmentManager().beginTransaction();
        ResetPasswordDialogFragment dialog = new ResetPasswordDialogFragment();
        dialog.show(manager, "Dialog");
    }


//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_ENTER:
//                InputHandler.hideSoftKeyBoard(this);
//                return true;
//            default:
//                return super.onKeyUp(keyCode, event);
//        }
//    }
}
