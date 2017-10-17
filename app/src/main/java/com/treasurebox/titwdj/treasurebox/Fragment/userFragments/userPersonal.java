package com.treasurebox.titwdj.treasurebox.Fragment.userFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.ProjectUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;


/**
 * Created by 11393 on 2017/8/14.
 */

public class userPersonal extends Fragment implements View.OnClickListener{
    private static final String TAG = "userPersonal";

    LinearLayout userPersonal1, userPersonal2, userPersonal3, userPersonal4,
            userPersonal5, userPersonal6, userPersonal7, userPersonal8, userPersonal9;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_personal, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_personal1:ProjectUtil.changeTheme(getActivity(), 1, "经典蓝");break;
            case R.id.user_personal2:ProjectUtil.changeTheme(getActivity(), 2, "深空蓝");break;
            case R.id.user_personal3:ProjectUtil.changeTheme(getActivity(), 3, "玫瑰红");break;
            case R.id.user_personal4:ProjectUtil.changeTheme(getActivity(), 4, "奇异绿");break;
            case R.id.user_personal5:ProjectUtil.changeTheme(getActivity(), 5, "香蕉黄");break;
            case R.id.user_personal6:ProjectUtil.changeTheme(getActivity(), 6, "鲜橙黄");break;
            case R.id.user_personal7:ProjectUtil.changeTheme(getActivity(), 7, "深邃紫");break;
            case R.id.user_personal8:ProjectUtil.changeTheme(getActivity(), 8, "宝石绿");break;
            case R.id.user_personal9:ProjectUtil.changeTheme(getActivity(), 9, "典雅黑");break;
        }
    }

    private void initView(View view) {
        userPersonal9 = (LinearLayout) view.findViewById(R.id.user_personal9);
        userPersonal8 = (LinearLayout) view.findViewById(R.id.user_personal8);
        userPersonal7 = (LinearLayout) view.findViewById(R.id.user_personal7);
        userPersonal6 = (LinearLayout) view.findViewById(R.id.user_personal6);
        userPersonal5 = (LinearLayout) view.findViewById(R.id.user_personal5);
        userPersonal4 = (LinearLayout) view.findViewById(R.id.user_personal4);
        userPersonal3 = (LinearLayout) view.findViewById(R.id.user_personal3);
        userPersonal2 = (LinearLayout) view.findViewById(R.id.user_personal2);
        userPersonal1 = (LinearLayout) view.findViewById(R.id.user_personal1);

        userPersonal1.setOnClickListener(this);
        userPersonal2.setOnClickListener(this);
        userPersonal3.setOnClickListener(this);
        userPersonal4.setOnClickListener(this);
        userPersonal5.setOnClickListener(this);
        userPersonal6.setOnClickListener(this);
        userPersonal7.setOnClickListener(this);
        userPersonal8.setOnClickListener(this);
        userPersonal9.setOnClickListener(this);
    }
}
