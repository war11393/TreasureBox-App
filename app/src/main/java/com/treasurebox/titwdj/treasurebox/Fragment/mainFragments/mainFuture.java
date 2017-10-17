package com.treasurebox.titwdj.treasurebox.Fragment.mainFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.treasurebox.titwdj.treasurebox.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 11393 on 2017/8/14.
 */

public class mainFuture extends Fragment {
    private static final String TAG = "mainFuture";
    
    @Bind(R.id.empty_fragment)
    TextView emptyText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userrobot2, container, false);
        ButterKnife.bind(this, view);

        emptyText.setText(emptyText.getText().toString() + TAG);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
