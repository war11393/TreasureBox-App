package com.treasurebox.titwdj.treasurebox.Fragment.userFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.treasurebox.titwdj.treasurebox.R;

/**
 * Created by 11393 on 2017/8/14.
 */
public class userAboutUs extends Fragment {
    private static final String TAG = "userAboutUs";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_aboutus, container, false);
        return view;
    }
}
