package com.minami_m.project.android.wakemeapp.Screen.SignIn;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.minami_m.project.android.wakemeapp.Common.Listener.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.Common.Listener.FacebookLoginListener;
import com.minami_m.project.android.wakemeapp.Common.Helper.FirebaseRealtimeDatabaseHelper;
import com.minami_m.project.android.wakemeapp.Common.Listener.FragmentChangeListener;
import com.minami_m.project.android.wakemeapp.Screen.Main.MainActivity;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.Model.User;
import com.minami_m.project.android.wakemeapp.Common.Handler.TimeHandler;

import java.util.Arrays;

import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;

public class SignInActivity extends AppCompatActivity implements FragmentChangeListener,
        ActivityChangeListener, FacebookLoginListener {

    CallbackManager callbackManager;
    private static final String EMAIL = "email";
    AccessTokenTracker mAccessTokenTracker;
    ProfileTracker profileTracker;
    FirebaseAuth mAuth;
    Profile facebookProfile;
    static ProgressBar progressBar;

    public static void setProgressBar(ProgressBar bar) {
        progressBar = bar;
        bar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // facebook
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);
        // ========
        setContentView(com.minami_m.project.android.wakemeapp.R.layout.activity_sign_in);
        // --- create a new fragment to be placed in the activity layout ---
        if (findViewById(R.id.signin_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            SignInFragment firstFragment = new SignInFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.signin_container, firstFragment).commit();
        }
        // ------------------------------------------------------------------
        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,AccessToken currentAccessToken) {
                Log.i(TAG, "onCurrentAccessTokenChanged: 123456");
            }
        };

        callbackManager = CallbackManager.Factory.create();
        // TODO: DELETE THE LINE BELOW LATER
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "onSuccess: 123456");
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel: 123456");
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(FacebookException error) {
                Log.i(TAG, "onError: 123456");
                Log.i(TAG, "onError: " + error.getMessage());
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void replaceFragment(Fragment newFragment) {
        // TODO: pass data if required
//        Bundle args = new Bundle();
//        args.putInt(ArticleFragment.ARG_POSITION, position);
//        newFragment.setArguments(args);

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
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        mAuth = firebaseAuth;
        if (AccessToken.getCurrentAccessToken() != null){
            LoginManager.getInstance().logOut();
            progressBar.setVisibility(View.INVISIBLE);

        } else {
            LoginManager.getInstance().logOut();
            LoginManager.getInstance().logInWithReadPermissions(this,Arrays.asList(EMAIL));
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
                            // Check if the usr sign in for the first time.
                            boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                            if (isNewUser) {
                                facebookProfile = Profile.getCurrentProfile();
                                String avatar = facebookProfile.getProfilePictureUri(300, 300).toString();
                                User newUser = new User(user.getUid(),
                                        facebookProfile.getName(),
                                        user.getEmail(),
                                        avatar);
                                newUser.setStatus(TimeHandler.generateStatus(newUser.getLastLogin()));
                                FirebaseRealtimeDatabaseHelper.writeNewUser(newUser);
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                            launchActivity(MainActivity.class);

                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

}
