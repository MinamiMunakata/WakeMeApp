package com.minami_m.project.android.wakemeapp;


import android.os.Bundle;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

import static com.firebase.ui.auth.ui.email.RegisterEmailFragment.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements inputValidationHandler, View.OnClickListener {
    private FirebaseAuth mAuth;
    EditText nameField;
    EditText emailField;
    EditText pwField;
    Button signUpButton;
    ImageView icon;
    TextView errorMsg;



    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mAuth = FirebaseAuth.getInstance();
        nameField = view.findViewById(R.id.edit_name);
        emailField = view.findViewById(R.id.edit_email);
        errorMsg = view.findViewById(R.id.sign_up_error);
        icon = view.findViewById(R.id.user_icon);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Upload image
            }
        });
        pwField = view.findViewById(R.id.edit_pw);
        signUpButton = view.findViewById(R.id.signup_btn);
        signUpButton.setOnClickListener(this);
        return view;
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
            String email = emailField.getText().toString(),
                    password = pwField.getText().toString();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                // TODO: Register to real time database with name
                                ((ActivityChangeListener) getActivity()).launchActivity(MainActivity.class);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                errorMsg.setText(task.getException().getMessage());
                            }
                        }
                    });

        } else {
            Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
        }
    }
}