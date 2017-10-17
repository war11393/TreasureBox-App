package com.treasurebox.titwdj.treasurebox.Fragment.userFragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.treasurebox.titwdj.treasurebox.Model.robot.RobotImpl;
import com.treasurebox.titwdj.treasurebox.Model.robot.RobotMsg;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Adapter.user_robot_msg;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11393 on 2017/8/14.
 */

public class userRobot extends Fragment {
    private static final String TAG = "userRobot";

    private List<RobotMsg> msgList = new ArrayList<>();
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecycleView;
    private user_robot_msg adapter;

    private Handler handler = new Handler();
    Runnable UI = new Runnable() {
        @Override
        public void run() {
            adapter = new user_robot_msg(msgList);
            msgRecycleView.setAdapter(adapter);
            msgRecycleView.scrollToPosition(msgList.size() - 1);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_robot, container, false);

        initVIew(view);//初始化视图

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        msgRecycleView.setLayoutManager(layoutManager);
        adapter = new user_robot_msg(msgList);
        msgRecycleView.setAdapter(adapter);

        setSendOnclick(send);//配置发送按钮点击

        return view;
    }

    //发送按钮监听
    private void setSendOnclick(Button send) {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = inputText.getText().toString();
                final RobotImpl robot = new RobotImpl();
                if (!"".equals(content)){
                    RobotMsg msg = new RobotMsg(content, RobotMsg.TYPE_SENT);
                    msgList.add(msg);

                    handler.post(UI);
                    inputText.setText("");//清空输入框内容

                    robot.talk(content);//向图灵机器人发消息
                    final RobotMsg msg2 = new RobotMsg("", RobotMsg.TYPE_RECEIVED);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            msg2.setContent(robot.getResp());
                            msgList.add(msg2);
                            handler.post(UI);
                        }
                    }).start();
                }
            }
        });
    }

    private void initVIew(View view) {
        inputText = (EditText) view.findViewById(R.id.user_robot_input_text);
        send = (Button) view.findViewById(R.id.user_robot_send);
        msgRecycleView = (RecyclerView) view.findViewById(R.id.msg_recycle_view);
    }
}
