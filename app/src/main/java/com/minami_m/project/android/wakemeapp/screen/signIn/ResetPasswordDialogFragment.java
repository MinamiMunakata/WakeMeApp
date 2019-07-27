package com.minami_m.project.android.wakemeapp.screen.signIn;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.common.handler.FontStyleHandler;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResetPasswordDialogFragment extends DialogFragment {
    private Context context;


    public ResetPasswordDialogFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_reset_password_dialog, null);
        final EditText editText = view.findViewById(R.id.email_to_reset_password);
        FontStyleHandler.setFont(requireActivity(), editText, false, false);
        Button button = view.findViewById(R.id.send_to_reset_password);
        FontStyleHandler.setFont(requireActivity(), button, false, false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editText.getText())) {
                    Toast.makeText(activity, "Enter email", Toast.LENGTH_LONG).show();
                } else {
                    String email = editText.getText().toString().trim();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(activity, "Email has sent.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                dismiss();
            }
        });
        builder.setView(view);
        // Get the layout inflater
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(getDialog().getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


}
