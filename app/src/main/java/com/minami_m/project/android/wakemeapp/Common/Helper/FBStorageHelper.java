package com.minami_m.project.android.wakemeapp.Common.Helper;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FBStorageHelper {
    private static final FirebaseStorage STORAGE = FirebaseStorage.getInstance();
    private static final StorageReference REF = STORAGE.getReference();

    public static StorageReference ICON_REF(FirebaseUser currentUser) {
        return REF
                .child("users")
                .child(currentUser.getUid())
                .child("icon/" + currentUser.getUid());
    }
}
