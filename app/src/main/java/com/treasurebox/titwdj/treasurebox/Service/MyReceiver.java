package com.treasurebox.titwdj.treasurebox.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;
import com.treasurebox.titwdj.treasurebox.Activity.BaseActivity;
import com.treasurebox.titwdj.treasurebox.Activity.MainActivity;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;

import java.util.List;

/**
 * 云推送广播接收
 */
public class MyReceiver extends PushMessageReceiver {
    private static final String TAG = "百度云推送";

    private static String ChannelId = "";

    @Override
    public void onBind(Context context, int errorcode, String appid, String userId, String channelId, String requestId) {
        LogUtil.d(TAG, "onBind: 绑定成功" + errorcode + "---" + appid + "---" + userId + "---" + channelId + "---" + requestId);
        ChannelId = channelId;
    }

    @Override
    public void onUnbind(Context context, int i, String s) {
        Log.d(TAG, "onUnbind: ");
    }

    @Override
    public void onSetTags(Context context, int i, List<String> list, List<String> list1, String s) {
        LogUtil.d(TAG, "onSetTags: ");
    }

    @Override
    public void onDelTags(Context context, int i, List<String> list, List<String> list1, String s) {
        LogUtil.d(TAG, "onDelTags: ");
    }

    @Override
    public void onListTags(Context context, int i, List<String> list, String s) {
        LogUtil.d(TAG, "onListTags: ");
    }

    @Override
    public void onMessage(Context context, String s, String s1) {
        LogUtil.d(TAG, "onMessage: 收到消息" + s + " --- " + s1 );
    }

    @Override
    public void onNotificationClicked(Context context, String s, String s1, String s2) {
        LogUtil.d(TAG, "onNotificationClicked: 点击通知");
    }

    @Override
    public void onNotificationArrived(Context context, String s, String s1, String s2) {
        LogUtil.d(TAG, "onNotificationArrived: 貌似是通知到达了:" + s + "--" + s1 + "--" + s2);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context
                .NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 400,
                new Intent(MyApplication.getApplication(), MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentTitle(s);
        mBuilder.setSmallIcon(R.drawable.logo_normal);
        mBuilder.setContentText(s1);
        mBuilder.setContentIntent(mainPendingIntent);
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(1, mBuilder.build());
    }

    public static String getChannelId(){
        return ChannelId;
    }
}
