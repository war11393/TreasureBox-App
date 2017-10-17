package com.treasurebox.titwdj.treasurebox.Fragment.userFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.AppManager;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Adapter.simple_text;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;


/**
 * Created by 11393 on 2017/9/17.
 */
public class userRemainWith extends Fragment {
    private static final String TAG = "userRemainWith";

    RecyclerView userRemainWithRecycler;
    EditText userRemainCustomName, userRemainCustomPhone;
    Button userRemainWithButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_remain_with, container, false);
        initView(view);
        setData();
        return view;
    }

    private void setData() {
        List<FriendInfo> userFriends = SQLite.select().from(FriendInfo.class).queryList();
        List<FriendInfo> friends = new ArrayList<>();
        for (FriendInfo userFriend:userFriends) {
            String proId = userFriend.getProId() + "";
            LogUtil.d("proId", proId);
            LogUtil.d("proId", String.valueOf(MyApplication.user.getUid()));
            String fNum = proId.substring(0, String.valueOf(MyApplication.user.getUid()).length());
            if (Integer.parseInt(fNum) == MyApplication.user.getUid()) {
                friends.add(userFriend);
            }
        }
        LogUtil.d("proId", friends.size() + "");
        simple_text adapter = new simple_text(friends, getActivity());
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        userRemainWithRecycler.setLayoutManager(manager);
        userRemainWithRecycler.setAdapter(adapter);
    }

    private void initView(View view) {
        userRemainWithButton = (Button) view.findViewById(R.id.user_remain_with_button);
        userRemainCustomPhone = (EditText) view.findViewById(R.id.user_remain_custom_phone);
        userRemainCustomName = (EditText) view.findViewById(R.id.user_remain_custom_name);
        userRemainWithRecycler = (RecyclerView) view.findViewById(R.id.user_remain_with_recycler);

        userRemainWithButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = userRemainCustomPhone.getText().toString().trim();
                if (!judgephone(string)){
                    SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("ERROR")
                            .setContentText("主人的数据好像输错了0_0");
                    dialog.show();
                } else if ("".equals(userRemainCustomName.getText().toString().trim())){
                    SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("ERROR")
                            .setContentText("主人要给这个人写一个备注哦0_0");
                    dialog.show();
                } else {
                    userRemain.number = userRemainCustomName.getText().toString().trim();
                    userRemain.phone = string;
                    userRemain.with = userRemain.number + "(" + userRemain.phone + ")";
                    userRemain.userRemainWithText.setText(userRemain.with);
                    AppManager.getInstance().finishActivity((AppCompatActivity) getActivity());
                }
            }
        });
    }

    //监测手机号合法性
    public boolean judgephone(String phone) {
        String str = "";
        str = phone;
        String pattern = "(13\\d|14[57]|15[^4,\\D]|17[678]|18\\d)\\d{8}|170[059]\\d{7}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(str);
        return m.matches();
    }
}
