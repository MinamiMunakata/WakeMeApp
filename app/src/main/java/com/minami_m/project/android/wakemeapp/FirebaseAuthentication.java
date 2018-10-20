package com.minami_m.project.android.wakemeapp;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class FirebaseAuthentication {
    public static void updateProfile(final FirebaseUser currentUser, String name) {
        Log.i("12345", "updateProfile: only name ->" + name);
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        currentUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("12345", "onComplete: User profile updated. name: " + currentUser.getDisplayName());
                } else {
                    Log.i("12345", "onComplete: failed...");
                }
            }
        });
    }

    public static void updateProfile(final FirebaseUser currentUser, Uri icon) {
        Log.i("12345", "updateProfile: only uri -> " + icon);
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(icon).build();
        currentUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("12345", "onComplete: User profile updated. name: " + currentUser.getPhotoUrl());
                } else {
                    Log.i("12345", "onComplete: failed...");
                }
            }
        });
    }

    public static void updateProfile(final FirebaseUser currentUser, String name, Uri icon) {
        Log.i("12345", "updateProfile: name ->" + name + "uri -> " + icon);
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(icon).build();
        currentUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("12345", "onComplete: User profile updated. name: " + currentUser.getDisplayName());
                } else {
                    Log.i("12345", "onComplete: failed...");
                }
            }
        });
    }

}
