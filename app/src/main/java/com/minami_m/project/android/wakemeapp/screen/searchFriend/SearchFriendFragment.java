package com.minami_m.project.android.wakemeapp.screen.searchFriend;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.minami_m.project.android.wakemeapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFriendFragment extends Fragment {
    EditText editText;


    public SearchFriendFragment() {
        // Required empty public constructor
    }

    public static SearchFriendFragment newInstance() {
        return new SearchFriendFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_friend, container, false);
        editText = view.findViewById(R.id.search_by_email_text_field);
        SearchFriendActivity activity = (SearchFriendActivity) requireActivity();
        activity.setEditEmail(editText);
        return view;
    }

}
