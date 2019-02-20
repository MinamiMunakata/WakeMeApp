package com.minami_m.project.android.wakemeapp.screen.searchFriend;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.minami_m.project.android.wakemeapp.R;
import com.minami_m.project.android.wakemeapp.common.handler.InputHandler;
import com.minami_m.project.android.wakemeapp.common.handler.InputValidationHandler;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFriendFragment extends Fragment implements InputValidationHandler {
    private static final String TAG = "SearchFriendFragment";
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

    @Override
    public boolean isValidInput() {
        return InputHandler.isValidFormEmail(editText);
    }

}
