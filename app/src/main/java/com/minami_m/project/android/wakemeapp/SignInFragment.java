package com.minami_m.project.android.wakemeapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment implements View.OnClickListener, inputValidationHandler {

    private FirebaseAuth mAuth;
    private Button sign_in_btn;
    private EditText editText_email;
    private EditText editText_pw;
    private TextView sign_up_link;
    private TextView error_msg;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        // Initialize Firebase Auth
        sign_in_btn = view.findViewById(R.id.sign_in_btn);
        editText_email = view.findViewById(R.id.edit_text_email_for_sign_in);
        editText_pw = view.findViewById(R.id.edit_text_password_for_sign_in);
        sign_up_link = view.findViewById(R.id.sign_up_link);
        error_msg = view.findViewById(R.id.sign_in_error);

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    launchMainActivity();
                    Toast.makeText(getContext(), user.getEmail(), Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "onAuthStateChanged: " + user.getUid() + " : " + user.getDisplayName() + " : " + user.getEmail());
                    return;
                } else {
                    // TODO: sign out
                }
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        sign_in_btn.setOnClickListener(this);
        sign_up_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUI(false);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (isValidInput()) {
            String email = editText_email.getText().toString(),
                    password = editText_pw.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        updateUI(true);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(getContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        error_msg.setText(task.getException().getMessage());
                        return;
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateUI(Boolean hasAccount) {
        if (hasAccount) {
            launchMainActivity();
        } else {
            displaySignUpForm();
        }
    }

    private void displaySignUpForm() {
        SignUpFragment signUpFragment = new SignUpFragment();
        FragmentChangeListener fragmentChangeListener = (FragmentChangeListener)getActivity();
        fragmentChangeListener.replaceFragment(signUpFragment);
    }

    private void launchMainActivity() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean isValidInput() {
        if (!InputHandler.isValidFormEmail(editText_email)) return false;
        if (!InputHandler.isValidFormPW(editText_pw)) return false;
        return true;
    }
}
