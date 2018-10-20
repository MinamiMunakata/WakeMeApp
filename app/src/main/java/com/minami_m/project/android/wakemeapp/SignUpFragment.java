package com.minami_m.project.android.wakemeapp;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.IOException;

import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment
        implements inputValidationHandler, View.OnClickListener {
    private FirebaseAuth mAuth;
    private EditText nameField;
    private EditText emailField;
    private EditText pwField;
    private Button signUpButton;
    private ImageView icon;
    private TextView errorMsg;
    private static int PICK_IMAGE_REQUEST = 12345;
    private Uri filePath;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Uri downloadedIconUri;



    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        nameField = view.findViewById(R.id.edit_name);
        emailField = view.findViewById(R.id.edit_email);
        errorMsg = view.findViewById(R.id.sign_up_error);
        pwField = view.findViewById(R.id.edit_pw);
        signUpButton = view.findViewById(R.id.signup_btn);
        signUpButton.setOnClickListener(this);
        icon = view.findViewById(R.id.user_icon);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImg();
            }
        });
        return view;
    }

    public void chooseImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST);
    }

    public void writeNewUSerWithImg(final FirebaseUser currentUser) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        StorageReference ref = storageReference;
        final StorageReference icon = ref
                .child("users")
                .child(currentUser.getUid())
                .child("icon/" + currentUser.getUid());
        // upload image to Firebase Storage
        icon.putFile(filePath)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        toast("Failed... " + e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        toast("Successfully Uploaded");
                        downloadedIconUri = taskSnapshot.getUploadSessionUri();
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                .setDisplayName(currentUser.getDisplayName())
                                .setPhotoUri(downloadedIconUri)
                                .build();
                        currentUser.updateProfile(request)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    User newUser = new User(currentUser.getUid(),
                                            currentUser.getDisplayName(),
                                            currentUser.getEmail(),
                                            downloadedIconUri.toString());
                                    FirebaseRealtimeDatabase.writeNewUser(newUser);
                                } else {
                                    Log.w(TAG, "onComplete: ", task.getException());
                                    toast("Failed to update the user profile.");
                                }
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot
                        .getBytesTransferred()/taskSnapshot
                        .getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int)progress + "%");
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST &&
                resultCode == Activity.RESULT_OK &&
                data != null &&
                data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                icon.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isValidInput() {
        if (!InputHandler.isValidFormName(nameField)) return false;
        if (!InputHandler.isValidFormEmail(emailField)) return false;
        if (!InputHandler.isValidFormPW(pwField)) return false;
        return true;
    }

    @Override
    public void onClick(View view) {
        if (isValidInput()) {
            final String name = nameField.getText().toString(),
                    email = emailField.getText().toString(),
                    password = pwField.getText().toString();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                // Update user info.
                                if (filePath != null) {
                                    writeNewUSerWithImg(currentUser);
                                } else {
                                    User newUser = new User(currentUser.getUid(),
                                            currentUser.getDisplayName(),
                                            currentUser.getEmail());
                                    FirebaseRealtimeDatabase.writeNewUser(newUser);
                                }
                                ((ActivityChangeListener) getActivity()).launchActivity(MainActivity.class);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                toast("Authentication failed.");
                                errorMsg.setText(task.getException().getMessage());
                            }
                        }
                    });

        } else {
            toast("Invalid Input");
        }
    }

    private void toast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}

