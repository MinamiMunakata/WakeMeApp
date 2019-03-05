package com.minami_m.project.android.wakemeapp.screen.signIn;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minami_m.project.android.wakemeapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResetPasswordDialogFragment extends Fragment {


    public ResetPasswordDialogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password_dialog, container, false);
    }

}
