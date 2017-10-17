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

import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.AppManager;
import com.treasurebox.titwdj.treasurebox.Adapter.note_permission_with;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11393 on 2017/8/30.
 * 纸条关于谁碎片
 */
public class noteWith extends Fragment {
    private static final String TAG = "noteWith";

    RecyclerView noteWriteWithRecycler;
    Button noteWriteWithButton;
    note_permission_with adapter;
    LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

    public static List<String> noteWithList = new ArrayList<>();
    public static List<String> noteWithNameList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_with, container, false);
        initView(view);

        //清除上一次选择
        noteWrite.noteWithList.clear();
        noteWrite.noteWithNameList.clear();

        noteWriteWithButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//点击完成后传递数据
                noteWrite.noteWithList = noteWithList;
                noteWrite.noteWithNameList = noteWithNameList;
                AppManager.getInstance().finishActivity((AppCompatActivity) getActivity());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        noteWrite.userFriendLists = MyApplication.checkFriendList();
        adapter = new note_permission_with(noteWrite.userFriendLists, 1);
        noteWriteWithRecycler.setLayoutManager(layoutManager);
        noteWriteWithRecycler.setAdapter(adapter);
    }

    //初始化视图
    private void initView(View view) {
        noteWriteWithRecycler = (RecyclerView) view.findViewById(R.id.note_write_with_recycler);
        noteWriteWithButton = (Button) view.findViewById(R.id.note_write_with_button);
    }
}
