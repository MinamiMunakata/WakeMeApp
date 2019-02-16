package com.minami_m.project.android.wakemeapp.common.listener;

import com.google.firebase.auth.FirebaseAuth;

public interface FacebookLoginListener {
    void loginWithFacebook(FirebaseAuth firebaseAuth);
}
