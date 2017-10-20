package com.treasurebox.titwdj.treasurebox.Utils;

import android.widget.Toast;

import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.R;

import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by 11393 on 2017/7/29.
 * 自定义网络访问工具类
 * 添加依赖  compile 'com.squareup.okhttp3:okhttp:3.4.1' 使用okhttp访问网络
 */

public class HttpUtil {
    private static final String TAG = "HttpUtil";
    public static int serversLoadTimes = 0, maxLoadTimes = 3;
    public static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS).build();

    public static SweetAlertDialog dialog;
    public static SweetAlertDialog errorDialog;

    //发起一个Okhttp网络请求，在callback中处理回调
    public static void sendOkHttpRequest(String address, Callback callback) {
        LogUtil.d(TAG, "发起Get网络请求：" + address);
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    //发起POST请求
    public static void sendPostOkHttpRequest(String address, RequestBody body, Callback callback) {
        LogUtil.d(TAG, "发起Post网络请求：" + address);
        Request request = new Request.Builder().url(address).method("POST", body).build();
        client.newCall(request).enqueue(callback);
    }

    //可显示进度条儿
    public static void sendOkHttpRequest(String address, boolean b, Callback callback) {
        LogUtil.d(TAG, "发起Get网络请求：" + address);
        showDialog(b);
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    //可显示进度条儿
    public static void sendPostOkHttpRequest(String address, RequestBody body, boolean b, Callback callback) {
        LogUtil.d(TAG, "发起Post网络请求：" + address);
        showDialog(b);
        Request request = new Request.Builder().url(address).method("POST", body).build();
        client.newCall(request).enqueue(callback);
    }

    //显示进度条儿
    private static void showDialog(boolean b) {
        LogUtil.d(TAG, "显示进度条儿");
        if (b) {
            if (dialog == null) {
                dialog = new SweetAlertDialog(AppManager.getInstance().getTopActivity(), SweetAlertDialog.PROGRESS_TYPE);
                dialog.setTitleText("Loading...");
                dialog.getProgressHelper().setBarColor(R.attr.primaryC);
                dialog.show();
            } else if (dialog.isShowing()) {
                return;
            } else {
                dialog = new SweetAlertDialog(AppManager.getInstance().getTopActivity(), SweetAlertDialog.PROGRESS_TYPE);
                dialog.setTitleText("Loading...");
                dialog.getProgressHelper().setBarColor(R.attr.primaryC);
                dialog.show();
            }
        }
    }
    public static void closeDialog() {
        LogUtil.d(TAG, "关闭进度条儿");
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
    //显示连接失败弹窗
    public static void showError() {
        if (AppManager.getInstance().getTopActivity() != null)
            AppManager.getInstance().getTopActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    errorDialog = new SweetAlertDialog(AppManager.getInstance().getTopActivity(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("WARN").setContentText("系统正忙，请稍后再试>_<").setConfirmText("好的");
                    errorDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            errorDialog.dismiss();
                        }
                    });
                    errorDialog.show();
                }
            });
        closeDialog();
    }
    //显示连接失败弹窗
    public static void showErrorToast() {
        closeDialog();
        if (AppManager.getInstance().getTopActivity() != null)
            AppManager.getInstance().getTopActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AppManager.getInstance().getTopActivity(), "获取相关数据失败", Toast.LENGTH_SHORT).show();
                }
            });
    }
}
