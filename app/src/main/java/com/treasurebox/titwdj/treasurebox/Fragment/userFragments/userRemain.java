package com.treasurebox.titwdj.treasurebox.Fragment.userFragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.treasurebox.titwdj.treasurebox.Activity.FragmentActivitys;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.AppManager;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Custom.View.DatePicker;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.client;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.dialog;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.maxLoadTimes;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.serversLoadTimes;


/**
 * Created by 11393 on 2017/9/17.
 */
public class userRemain extends Fragment implements View.OnClickListener {
    private static final String TAG = "userRemain";

    EditText userRemainText;
    public static TextView userRemainWithText, userRemainTimeText, userRemainPhoneText;
    LinearLayout userRemainTime, userRemainPhone, userRemainWith;
    Button userRemainConfirm;

    public static String phone = "无", time = "无", with = "无", number = "无";
    private static Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_remain, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        userRemainConfirm = (Button) view.findViewById(R.id.user_remain_confirm);
        userRemainPhone = (LinearLayout) view.findViewById(R.id.user_remain_phone);
        userRemainPhoneText = (TextView) view.findViewById(R.id.user_remain_phone_text);
        userRemainTime = (LinearLayout) view.findViewById(R.id.user_remain_time);
        userRemainTimeText = (TextView) view.findViewById(R.id.user_remain_time_text);
        userRemainWith = (LinearLayout) view.findViewById(R.id.user_remain_with);
        userRemainWithText = (TextView) view.findViewById(R.id.user_remain_with_text);
        userRemainText = (EditText) view.findViewById(R.id.user_remain_text);

        userRemainPhoneText.setText(phone);
        userRemainTimeText.setText(time);
        userRemainWithText.setText(with);

        dialog = setTimeChoose();

        userRemainPhone.setOnClickListener(this);
        userRemainWith.setOnClickListener(this);
        userRemainTime.setOnClickListener(this);
        userRemainConfirm.setOnClickListener(this);
    }

    //时间选择
    private Dialog setTimeChoose() {
        final Calendar calendar = Calendar.getInstance();
        final DatePicker.Builder builder = new DatePicker.Builder(getActivity(), -10);
        builder.setPositiveButton(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                Date date = null;
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd").parse(builder.getStr());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (date.getTime() > calendar.getTimeInMillis()) {
                    time = builder.getStr();
                    userRemainTimeText.setText(time);
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final SweetAlertDialog dialog1 = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                                        .setTitleText("小贴士").setContentText("所选日期不能比今天早哦！").setConfirmText("知道了");
                                dialog1.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        dialog1.dismiss();
                                    }
                                });
                                dialog1.show();
                            }
                        });
                    }
                }
            }
        });
        builder.setNegativeButton(new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //创建对话框
        Dialog dialog = builder.create();
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager m = getActivity().getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.width = (int) (d.getWidth() * 0.75); // 宽度设置为屏幕的0.65
        dialogWindow.setAttributes(p);

        return dialog;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_remain_time://选择时间
                dialog.show();break;
            case R.id.user_remain_with://选择好友
                Intent intent = new Intent(getContext(), FragmentActivitys.class);
                intent.putExtra(FragmentActivitys.extra_title, "选择好友");
                intent.putExtra(FragmentActivitys.extra_flag, FragmentActivitys.userremainwith);
                startActivity(intent);
                break;
            case R.id.user_remain_confirm://提交
                if ("无".equals(with) || "无".equals(time) || "".equals(userRemainText.getText().toString().trim())) {
                    SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("ERROR")
                            .setContentText("还有数据没有填完0_0");
                    dialog.show();
                } else {
                    /**
                     * 添加提醒记录
                     * 提供的参数:wcintent(提醒内容)，wtime（提醒时间），wto（如果是好友列表中的为好友账号，如果不是则为空），wfrom（用户id），wphone（当wto为空的时候需要把被提醒人的手机号记录下来）
                     * @return "提醒设置成功"
                     */
                    RequestBody body = new FormBody.Builder()
                            .add("wcintent", userRemainText.getText().toString().trim())
                            .add("wtime", time)
                            .add("wto", number)
                            .add("wfrom", MyApplication.user.getUid() + "")
                            .add("wphone", phone).build();
                    HttpUtil.sendPostOkHttpRequest(HttpPathUtil.setWarn(), body, true, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            LogUtil.d(TAG, e.toString() + "   正重新尝试链接...");
                            if(e.getClass().equals(SocketTimeoutException.class) && serversLoadTimes < maxLoadTimes)//如果超时并未超过指定次数，则重新连接
                            {
                                serversLoadTimes++;
                                client.newCall(call.request()).enqueue(this);
                            } else {
                                serversLoadTimes = 0;
                                e.printStackTrace();
                                HttpUtil.showError();
                            }
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            serversLoadTimes = 0;dialog.dismiss();
                            String resp = response.body().string();
                            LogUtil.d(TAG, resp);
                            if ("提醒设置成功".equals(resp)) {
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            final SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                                    .setTitleText("提交成功！");
                                            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    dialog.dismiss();
                                                    AppManager.getInstance().finishActivity((AppCompatActivity) getActivity());
                                                }
                                            });
                                            dialog.show();
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
                break;
        }
    }
}
