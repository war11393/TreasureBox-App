package com.treasurebox.titwdj.treasurebox.Fragment.noteFragments;

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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.AppManager;
import com.treasurebox.titwdj.treasurebox.Adapter.note_permission_with;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;

/**
 * Created by 11393 on 2017/8/30.
 * 纸条权限碎片
 */
public class notePermission extends Fragment implements View.OnClickListener {
    private static final String TAG = "notePermission";

    ImageView notePermissionAllBox, notePermissionOnlyBox, notePermissionAllowBox, notePermissionWhoNotBox;
    LinearLayout notePermissionAll, notePermissionOnly, notePermissionAllow, notePermissionWhoNot;
    ImageView notePermissionAllowImg, notePermissionWhoNotImg;
    RecyclerView notePermissionAllowList, notePermissionWhoNotList;
    Button notePermissionBtn;

    LinearLayout[] permissions = {notePermissionAll, notePermissionOnly, notePermissionAllow, notePermissionWhoNot};
    ImageView[] boxes = {notePermissionAllBox, notePermissionOnlyBox, notePermissionAllowBox, notePermissionWhoNotBox};
    int perStatus = 0;//0--全闭，1--部分好友开，2--不给谁看开
    int status = 0;//0--未选中，1--部分好友，2--不给谁看
    note_permission_with adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_permission, container, false);
        initView(view);
        initData();

        noteWrite.notePermissionList.clear();

        return view;
    }

    //设置复杂权限项
    private void setAllowList(int status) {
        switch (status) {
            case 0:
                Glide.with(this).load(R.drawable.part_close).into(notePermissionAllowImg);
                Glide.with(this).load(R.drawable.part_close).into(notePermissionWhoNotImg);
                notePermissionWhoNotList.setVisibility(View.GONE);
                notePermissionAllowList.setVisibility(View.GONE);
                break;
            case 1:
                if (perStatus == 1) {
                    perStatus = 0;
                    Glide.with(this).load(R.drawable.part_close).into(notePermissionAllowImg);
                    notePermissionAllowList.setVisibility(View.GONE);
                } else {
                    Glide.with(this).load(R.drawable.part_open).into(notePermissionAllowImg);
                    Glide.with(this).load(R.drawable.part_close).into(notePermissionWhoNotImg);
                    notePermissionWhoNotList.setVisibility(View.GONE);
                    notePermissionAllowList.setVisibility(View.VISIBLE);
                    perStatus = 1;
                }
                break;
            case 2:
                if (perStatus == 2) {
                    Glide.with(this).load(R.drawable.part_close).into(notePermissionWhoNotImg);
                    notePermissionWhoNotList.setVisibility(View.GONE);
                    perStatus = 0;
                } else {
                    Glide.with(this).load(R.drawable.part_open).into(notePermissionWhoNotImg);
                    Glide.with(this).load(R.drawable.part_close).into(notePermissionAllowImg);
                    notePermissionWhoNotList.setVisibility(View.VISIBLE);
                    notePermissionAllowList.setVisibility(View.GONE);
                    perStatus = 2;
                }
                break;
        }
    }

    private void initData() {
        noteWrite.userFriendLists = MyApplication.checkFriendList();
        adapter = new note_permission_with(noteWrite.userFriendLists, 0);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()), layoutManager1 = new LinearLayoutManager(getContext());
        notePermissionAllowList.setLayoutManager(layoutManager);
        notePermissionAllowList.setAdapter(adapter);
        notePermissionWhoNotList.setLayoutManager(layoutManager1);
        notePermissionWhoNotList.setAdapter(adapter);
    }

    int notePermissionStatus = 0;//---------------------------------------------------------------------------------------------------------这个需要在未来初始化
    @Override//点击监听
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.note_permission_all:
                notePermissionStatus = 0;
                status = 0;setboximg(0);break;
            case R.id.note_permission_only:
                notePermissionStatus = 1;
                status = 0;setboximg(1);break;
            case R.id.note_permission_allow:
                notePermissionStatus = 2;
                status = 1;setboximg(2);setAllowList(status);
                break;
            case R.id.note_permission_who_not:
                notePermissionStatus = 3;
                status = 2;setboximg(3);setAllowList(status);
                break;
            case R.id.note_permission_btn:
                noteWrite.notePermissionStatus = notePermissionStatus;
                if (notePermissionStatus == 0 || notePermissionStatus == 1){
                    noteWrite.notePermissionList.clear();
                }
                AppManager.getInstance().finishActivity((AppCompatActivity) getActivity());
                break;
            default:
                break;
        }
    }

    //设置大权限项选中样式
    private void setboximg(int j) {
        for (int i = 0; i < 4; i++) {
            if (i == j && i == 3){
                Glide.with(this).load(R.drawable.part_ok).into(boxes[i]);
            } else if (i == j){
                Glide.with(this).load(R.drawable.part_ok_ok).into(boxes[i]);
            } else {
                Glide.with(this).load(R.drawable.part_trans).into(boxes[i]);
            }
        }
    }

    //初始化视图
    private void initView(View view) {
        notePermissionWhoNotList = (RecyclerView) view.findViewById(R.id.note_permission_who_not_list);
        permissions[3] = (LinearLayout) view.findViewById(R.id.note_permission_who_not);
        notePermissionWhoNotImg = (ImageView) view.findViewById(R.id.note_permission_who_not_img);
        boxes[3] = (ImageView) view.findViewById(R.id.note_permission_who_not_box);
        notePermissionAllowList = (RecyclerView) view.findViewById(R.id.note_permission_allow_list);
        permissions[2] = (LinearLayout) view.findViewById(R.id.note_permission_allow);
        notePermissionAllowImg = (ImageView) view.findViewById(R.id.note_permission_allow_img);
        boxes[2] = (ImageView) view.findViewById(R.id.note_permission_allow_box);
        permissions[1] = (LinearLayout) view.findViewById(R.id.note_permission_only);
        boxes[1] = (ImageView) view.findViewById(R.id.note_permission_only_box);
        permissions[0] = (LinearLayout) view.findViewById(R.id.note_permission_all);
        boxes[0] = (ImageView) view.findViewById(R.id.note_permission_all_box);
        notePermissionBtn = (Button) view.findViewById(R.id.note_permission_btn);

        permissions[0].setOnClickListener(this);
        permissions[1].setOnClickListener(this);
        permissions[2].setOnClickListener(this);
        permissions[3].setOnClickListener(this);
        notePermissionBtn.setOnClickListener(this);
    }
}
