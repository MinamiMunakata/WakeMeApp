package com.minami_m.project.android.wakemeapp.Screen.SearchFriend;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.minami_m.project.android.wakemeapp.Common.Listener.ActivityChangeListener;
import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.Screen.Main.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SuccessfullyAddedDialog extends DialogFragment implements ActivityChangeListener {
    public static SuccessfullyAddedDialog newInstance() {
        return new SuccessfullyAddedDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_successfully_added, null))
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                        dialog.dismiss();
                        launchActivity(MainActivity.class);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = (int) (size.x * 0.8);
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog);
        getDialog().getWindow().setLayout(width, height);
        Button okButton = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        okButton.setTextColor(getContext().getColor(R.color.colorMyAccent));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) okButton.getLayoutParams();
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        okButton.setLayoutParams(layoutParams);
        getDialog().setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                launchActivity(MainActivity.class);
            }
        });

    }

    @Override
    public void launchActivity(Class nextActivity) {
        Intent intent = new Intent(getContext(), nextActivity);
        startActivity(intent);
    }
}
