package com.minami_m.project.android.wakemeapp.Screen.SignIn;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.UploadTask;
import com.minami_m.project.android.wakemeapp.Common.Listener.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.Common.Helper.FirebaseRealtimeDatabaseHelper;
import com.minami_m.project.android.wakemeapp.Common.Helper.FirebaseStorageHelper;
import com.minami_m.project.android.wakemeapp.Common.Handler.FontStyleHandler;
import com.minami_m.project.android.wakemeapp.Common.Handler.DateAndTimeFormatHandler;
import com.minami_m.project.android.wakemeapp.Common.Handler.InputHandler;
import com.minami_m.project.android.wakemeapp.Screen.Main.MainActivity;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.Model.User;
import com.minami_m.project.android.wakemeapp.Common.Handler.InputValidationHandler;

import java.io.IOException;

import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment
        implements InputValidationHandler, View.OnClickListener {
    private FirebaseAuth mAuth;
    private EditText nameField;
    private EditText emailField;
    private EditText pwField;
    private ImageView icon;
    private TextView errorMsg;
    private static int PICK_IMAGE_REQUEST = 12345;
    private Uri filePath;
    private String downloadedIconUrl;
    //    private ProgressBar progressBar;
    private ImageView loadingImage;
    private RelativeLayout loadingBG;
    private TextView signUp;



    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance(@Nullable String email, @Nullable String pw) {
        SignUpFragment fragment = new SignUpFragment();
        if (email != null) {
            Bundle data = new Bundle();
            data.putString("email", email);
            data.putString("pw", pw);
            fragment.setArguments(data);
        }
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mAuth = FirebaseAuth.getInstance();
        signUp = view.findViewById(R.id.sign_up);
        FontStyleHandler.setFont(getContext(), signUp, true, true);
        nameField = view.findViewById(R.id.edit_name);
        emailField = view.findViewById(R.id.edit_email);
        setEmailFromSignInForm();
        errorMsg = view.findViewById(R.id.sign_up_error);
        pwField = view.findViewById(R.id.edit_pw);
        Button signUpButton = view.findViewById(R.id.signup_btn);
        FontStyleHandler.setFont(getContext(), signUpButton, false, true);
        FontStyleHandler.setFont(getContext(), signUpButton, false, true);
        signUpButton.setOnClickListener(this);
        icon = view.findViewById(R.id.user_icon);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImg();
            }
        });
        loadingBG = view.findViewById(R.id.loading_bg_sign_up);
        loadingImage = view.findViewById(R.id.sign_up_loading_img);
        Glide.with(this).load(R.raw.loading).into(loadingImage);
//        progressBar = view.findViewById(R.id.progressbar);
        loadingBG.setVisibility(View.INVISIBLE);
        return view;
    }

    private void setEmailFromSignInForm() {
        Bundle data = getArguments();
        if (data != null && data.containsKey("email") && data.containsKey("pw")) {
            String email = data.getString("email");
            String password = data.getString("pw");
            emailField.setText(email);
            FirebaseUser user = mAuth.getCurrentUser();
            // Get auth credentials from the user for re-authentication. The example below shows
            // email and password credentials but there are multiple possible providers,
            // such as GoogleAuthProvider or FacebookAuthProvider.
            AuthCredential credential = EmailAuthProvider
                    .getCredential(email, password);

            // Prompt the user to re-provide their sign-in credentials
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d(TAG, "User re-authenticated.");
                        }
                    });
            if (user != null) {
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "User account deleted.");
                        } else {
                            Log.i(TAG, "onComplete: " + task.getException().getMessage());
                        }
                    }
                });
            }
        }
    }

    public void chooseImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST);
    }

    public void writeNewUSerWithImg(final FirebaseUser currentUser) {
        // upload image to Firebase Storage
        FirebaseStorageHelper.ICON_REF(currentUser).putFile(filePath)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingBG.setVisibility(View.INVISIBLE);
                        toast("Failed... " + e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        loadingBG.setVisibility(View.INVISIBLE);
                        toast("Successfully Uploaded");
                        FirebaseStorageHelper.ICON_REF(currentUser).getDownloadUrl()
                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                downloadedIconUrl = task.getResult().toString();
                                User newUser = new User(currentUser.getUid(),
                                        currentUser.getDisplayName(),
                                        currentUser.getEmail(),
                                        downloadedIconUrl);
                                newUser.setStatus(DateAndTimeFormatHandler.generateStatus(newUser.getLastLogin()));
                                FirebaseRealtimeDatabaseHelper.writeNewUser(newUser);
                                ((ActivityChangeListener) getActivity()).launchActivity(MainActivity.class);
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                // TODO: delete?
//                double progress = (100.0 * taskSnapshot
//                        .getBytesTransferred()/taskSnapshot
//                        .getTotalByteCount());
//                progressBar.setProgress((int)progress);
//                Log.i(TAG, "onProgress: " + progress);
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
        InputHandler.hideSoftKeyBoard(getActivity());
        if (isValidInput()) {
            loadingBG.setVisibility(View.VISIBLE);
            final String name = nameField.getText().toString(),
                    email = emailField.getText().toString(),
                    password = pwField.getText().toString();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final FirebaseUser currentUser = mAuth.getCurrentUser();
                                UserProfileChangeRequest request =
                                        new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();
                                currentUser.updateProfile(request)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (filePath != null) {
                                            writeNewUSerWithImg(currentUser);
                                        } else {
                                            User newUser = new User(currentUser.getUid(),
                                                    currentUser.getDisplayName(),
                                                    currentUser.getEmail());
                                            FirebaseRealtimeDatabaseHelper.writeNewUser(newUser);
                                            loadingBG.setVisibility(View.INVISIBLE);
                                            ((ActivityChangeListener) getActivity()).launchActivity(MainActivity.class);
                                        }
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                loadingBG.setVisibility(View.INVISIBLE);
                                                Log.i(TAG, "onFailure: 12345 " + e.getMessage());
                                                toast("Failed to update the user profile.");
                                            }
                                        });
                            } else {
                                // If sign in fails, display a message to the user.
                                loadingBG.setVisibility(View.INVISIBLE);
                                Log.w(TAG, "createUserWithEmail:failure 12345", task.getException());
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

