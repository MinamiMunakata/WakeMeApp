package com.minami_m.project.android.wakemeapp.screen.searchFriend;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.minami_m.project.android.wakemeapp.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFriendResultFragment extends Fragment {
    private ImageView iconHolder;
    private TextView nameHolder;

    public SearchFriendResultFragment() {
        // Required empty public constructor
    }

    public static SearchFriendResultFragment newInstance() {
        return new SearchFriendResultFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_friend_result, container, false);
        iconHolder = view.findViewById(R.id.icon_holder);
        nameHolder = view.findViewById(R.id.name_holder);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        SearchFriendFragmentListener listener = (SearchFriendFragmentListener) getActivity();
        listener.showFriend(iconHolder, nameHolder);
    }

}
