package com.minami_m.project.android.wakemeapp;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseStorageHelper {
    public static final FirebaseStorage STORAGE = FirebaseStorage.getInstance();
    public static final StorageReference REF = STORAGE.getReference();

    public static final StorageReference ICON_REF(FirebaseUser currentUser) {
        StorageReference ICON_REF = REF
                .child("users")
                .child(currentUser.getUid())
                .child("icon/" + currentUser.getUid());
        return ICON_REF;
    }
}
