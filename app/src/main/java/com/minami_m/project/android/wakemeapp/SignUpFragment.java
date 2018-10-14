package com.minami_m.project.android.wakemeapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {
    EditText name_field;
    EditText email_field;
    EditText pw_field;


    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        name_field = view.findViewById(R.id.edit_name);
        email_field = view.findViewById(R.id.edit_email);
        pw_field = view.findViewById(R.id.edit_pw);
        return view;
    }

}
