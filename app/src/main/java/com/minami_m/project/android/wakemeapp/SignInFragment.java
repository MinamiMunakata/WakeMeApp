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
public class SignInFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private Button sign_in_btn;
    private EditText editText_email;
    private EditText editText_pw;

    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        sign_in_btn = view.findViewById(R.id.sign_in_btn);
        editText_email = view.findViewById(R.id.edit_text_email_for_sign_in);
        editText_pw = view.findViewById(R.id.edit_text_password_for_sign_in);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        sign_in_btn.setOnClickListener(this);
//        updateUI(currentUser);
    }

    private boolean checkValidation() {
        if (TextUtils.isEmpty(editText_email.getText()) ||
                TextUtils.isEmpty(editText_pw.getText())) return false;
        return true;
    }


    @Override
    public void onClick(View view) {
        if (checkValidation()) {
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser != null);
        } else {
            Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateUI(Boolean hasAccount) {
        if (hasAccount) {
            launchMainActivity();
        } else {
            displaySignUpForm();
            String email = editText_email.getText().toString(),
                    password = editText_pw.getText().toString();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user != null);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
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
}
