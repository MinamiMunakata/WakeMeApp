package com.minami_m.project.android.wakemeapp.screen.signIn;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.UploadTask;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.common.handler.FontStyleHandler;
import com.minami_m.project.android.wakemeapp.common.helper.FBRealTimeDBHelper;
import com.minami_m.project.android.wakemeapp.common.helper.FBStorageHelper;
import com.minami_m.project.android.wakemeapp.common.listener.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.model.User;
import com.minami_m.project.android.wakemeapp.screen.main.MainActivity;

import java.io.IOException;
import java.util.Objects;

import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener {
    private static final int PICK_IMAGE_REQUEST = 12345;
    private FirebaseAuth mAuth;
    private EditText nameField;
    private EditText emailField;
    private EditText pwField;
    private ImageView icon;
    private TextView errorMsg;
    private Uri filePath;
    private String downloadedIconUrl;
    private RelativeLayout loadingBG;
    private FirebaseAnalytics mFirebaseAnalytics;


    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mAuth = FirebaseAuth.getInstance();
        initialize(view);
        setupSignUpButton(view);
        setupLoadingImg(view);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImg();
            }
        });
        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(requireContext());
        Bundle params = new Bundle();
        params.putString("image_name", "WakeMeApp");
        params.putString("full_text", "Log events.");
        mFirebaseAnalytics.logEvent("share_image", params);
        return view;
    }

    private void initialize(View view) {
        TextView signUp = view.findViewById(R.id.sign_up_title);
        FontStyleHandler.setFont(getContext(), signUp, true, true);
        nameField = view.findViewById(R.id.edit_name);
        emailField = view.findViewById(R.id.edit_email);
        errorMsg = view.findViewById(R.id.sign_up_error);
        pwField = view.findViewById(R.id.edit_pw);
        icon = view.findViewById(R.id.user_icon);
    }

    private void setupSignUpButton(View view) {
        Button signUpButton = view.findViewById(R.id.sign_up_btn);
        FontStyleHandler.setFont(getContext(), signUpButton, false, true);
        FontStyleHandler.setFont(getContext(), signUpButton, false, true);
        signUpButton.setOnClickListener(this);
    }

    private void setupLoadingImg(View view) {
        loadingBG = view.findViewById(R.id.loading_bg_sign_up);
        ImageView loadingImage = view.findViewById(R.id.sign_up_loading_img);
        Glide.with(this).load(R.raw.loading).into(loadingImage);
        loadingBG.setVisibility(View.INVISIBLE);
    }

    private void chooseImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                PICK_IMAGE_REQUEST);
    }

    private void writeNewUSerWithImg(final FirebaseUser currentUser) {
        // upload image to Firebase Storage
        FBStorageHelper.ICON_REF(currentUser).putFile(filePath)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toast("Failed to upload the image.");
                        saveToFBRealTimeDBAndMoveToMainActivity(currentUser);
                        Log.w(TAG, "onFailure: ", e);
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        loadingBG.setVisibility(View.INVISIBLE);
                        FBStorageHelper.ICON_REF(currentUser).getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        downloadedIconUrl = uri.toString();
                                        User newUser = new User(
                                                currentUser.getUid(),
                                                currentUser.getDisplayName(),
                                                currentUser.getEmail(),
                                                downloadedIconUrl);
                                        FBRealTimeDBHelper.writeNewUser(newUser);
                                        ((ActivityChangeListener) requireActivity()).launchActivity(MainActivity.class);
                                    }
                                });
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
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), filePath);
                icon.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        hideSoftKeyBoard();
        if (!TextUtils.isEmpty(nameField.getText())
                && !TextUtils.isEmpty(emailField.getText()) && !TextUtils.isEmpty(pwField.getText())) {
            loadingBG.setVisibility(View.VISIBLE);
            final String name = nameField.getText().toString().trim(),
                    email = emailField.getText().toString().trim(),
                    password = pwField.getText().toString().trim();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final FirebaseUser currentUser = mAuth.getCurrentUser();
                                if (currentUser != null) {
                                    updateFBAuthWithNameAndSaveUserDataToFB(currentUser, name);
                                }
                                logEvents("Succeed.");
                            } else {
                                // If sign in fails, display a message to the user.
                                loadingBG.setVisibility(View.INVISIBLE);
                                Log.w(TAG, "createUserWithEmail: ", task.getException());
                                toast("Authentication failed.");
                                String error = task.getException() != null ? task.getException().getMessage() : "";
                                errorMsg.setText(error);
                                logEvents("Failed");
                            }
                        }
                    });

        } else {
            toast("Fill in a form.");
        }
    }

    private void hideSoftKeyBoard() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (Objects.requireNonNull(imm).isAcceptingText()) {
            imm.hideSoftInputFromWindow(Objects.requireNonNull(requireActivity().getCurrentFocus()).getWindowToken(), 0);
        }
    }

    private void logEvents(String result) {
        Bundle bundle = new Bundle();
        bundle.putString(
                FirebaseAnalytics.Param.METHOD,
                "onClick: createUserWithEmailAndPassword\nResult: " + result);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP, bundle);
    }

    private void updateFBAuthWithNameAndSaveUserDataToFB(final FirebaseUser currentUser, String name) {
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        currentUser.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (filePath != null) { // Save with an icon.
                    writeNewUSerWithImg(currentUser);
                } else { // Save without an icon.
                    saveToFBRealTimeDBAndMoveToMainActivity(currentUser);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBG.setVisibility(View.INVISIBLE);
                Log.i(TAG, "onFailure: " + e.getMessage());
                toast("Failed to update the user profile.");
            }
        });
    }

    private void saveToFBRealTimeDBAndMoveToMainActivity(FirebaseUser currentUser) {
        User newUser = new User(currentUser.getUid(), currentUser.getDisplayName(), currentUser.getEmail());
        FBRealTimeDBHelper.writeNewUser(newUser);
        loadingBG.setVisibility(View.INVISIBLE);
        ((ActivityChangeListener) requireActivity()).launchActivity(MainActivity.class);
    }

    private void toast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}

