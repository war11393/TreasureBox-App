package com.treasurebox.titwdj.treasurebox.Model.robot;

/**
 * Created by 予以心 on 2017/7/24.
 * 消息的实体类
 */
public class RobotMsg {
    public static final int TYPE_RECEIVED = 0;//收到的消息
    public static final int TYPE_SENT = 1;//发出的消息
    private String content;//消息内容
    private int type;//消息类型

    public RobotMsg(String content, int type) {
        this.content = content;
        this.type = type;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }
}
