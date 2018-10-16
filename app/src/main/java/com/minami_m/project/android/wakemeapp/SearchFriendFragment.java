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
public class SearchFriendFragment extends Fragment {
    EditText editText;


    public SearchFriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_friend, container, false);
        editText = view.findViewById(R.id.edit_email);
        return view;
    }

}
