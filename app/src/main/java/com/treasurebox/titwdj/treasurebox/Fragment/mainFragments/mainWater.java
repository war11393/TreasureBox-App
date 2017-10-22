package com.treasurebox.titwdj.treasurebox.Fragment.mainFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.treasurebox.titwdj.treasurebox.Adapter.DriftNoteEnvaluate;
import com.treasurebox.titwdj.treasurebox.Adapter.DriftNotes;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Model.nother.DriftNote;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

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
 * Created by 11393 on 2017/8/14.
 */
public class mainWater extends Fragment implements View.OnClickListener{
    private static final String TAG = "mainWater";

    EditText mainWaterEdit, reviewContent;
    TextView mainWaterText;
    RecyclerView mainWaterReview, historyReview;
    CheckBox mainWaterNotLook;
    ImageView mainWaterWriteReview;
    LinearLayout getContainer, reviewContainer, buttonContainer, imageContainer;
    Button mainWaterGet, mainWaterHistory, mainWaterSend, sendReview;

    DriftNoteEnvaluate adapter;
    DriftNotes adapter_notes;

    public static DriftNote dn = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_water, container, false);
        initView(view);
        return view;
    }

    @Override//处理点击
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_water_history://查看历史
                lookHistory();
                break;
            case R.id.main_water_send_review://发表评论
                if (!TextUtils.isEmpty(reviewContent.getText())) {
                    reviewDn(reviewContent.getText().toString().trim());
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "请输入有效信息", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }break;
            case R.id.main_water_get://捡瓶子
                if(mainWaterGet.getText().toString().trim().equals("捡瓶子")) {
                    getWater();
                } else {
                    checkHate();
                    waterBack();
                    getWater();
                }break;
            case R.id.main_water_send://发瓶子
                if (mainWaterSend.getText().toString().trim().equals("扔瓶子")) {
                    setSendWater();
                } else if (mainWaterSend.getText().toString().trim().equals("扔出")){
                    sendWater();
                }break;
        }
    }

    //http请求-查看历史瓶子
    private void lookHistory() {
        RequestBody body = new FormBody.Builder().add("uid", MyApplication.user.getUid() + "").build();
        HttpUtil.sendPostOkHttpRequest(HttpPathUtil.selectDriftByUid(), body, true, new Callback() {
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
                serversLoadTimes = 0;HttpUtil.closeDialog();
                String resp = response.body().string();
                LogUtil.d(TAG, resp);
                if (Util.JsonUtils.isGoodJson(resp)) {
                    List<DriftNote> notes = JSON.parseArray(resp, DriftNote.class);
                    setHistory(notes);
                } else {
                    showError();
                }
            }
        });
    }
    //http请求-评论这个瓶子--待完善
    private void reviewDn(String trim) {
        RequestBody body = new FormBody.Builder()
                .add("driftId", dn.getDriftId() + "").add("drifCommentId", MyApplication.user.getUid() + "")
                .add("drifIfObv", 0 + "").add("drifContent", trim).add("userName", MyApplication.user.getUsername()).build();
        HttpUtil.sendPostOkHttpRequest(HttpPathUtil.addDrift_evaluate_discuss(), body, true, new Callback() {
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
                serversLoadTimes = 0;HttpUtil.closeDialog();
                String resp = response.body().string();
                LogUtil.d(TAG, resp);
                if (Util.JsonUtils.isGoodJson(resp)) {
                    DriftNote.DriftEvaluateListBean driftEvaluateListBean = new Gson().fromJson(resp, DriftNote.DriftEvaluateListBean.class);
                    List<DriftNote.DriftEvaluateListBean> list = dn.getDrift_evaluateList();
                    list.add(driftEvaluateListBean);
                    dn.setDrift_evaluateList(list);
                    adapter = new DriftNoteEnvaluate(dn.getDrift_evaluateList());
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mainWaterReview.setAdapter(adapter);
                            }
                        });
                    }
                    colseReview();
                } else {
                    showError();
                }
            }
        });
    }
    //http请求-执行扔回大海操作
    private void waterBack() {
        if (dn != null) {
            RequestBody body = new FormBody.Builder().add("driftId", dn.getDriftId() + "").build();
            HttpUtil.sendPostOkHttpRequest(HttpPathUtil.atSea(), body, true, new Callback() {
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
                    serversLoadTimes = 0;HttpUtil.closeDialog();
                    String resp = response.body().string();
                    LogUtil.d(TAG, resp);
                    if (resp.equals("已经成功返回大海！")) {
                        LogUtil.d(TAG, "成功扔回大海");
                    }
                }
            });
        }
    }
    //http请求-当用户厌恶此瓶时，不再捡起
    private void checkHate() {
        if (mainWaterNotLook.isChecked()) {
            RequestBody body = new FormBody.Builder().add("driftId", dn.getDriftId() + "").build();
            HttpUtil.sendPostOkHttpRequest(HttpPathUtil.hate(), body, true, new Callback() {
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
                    serversLoadTimes = 0;HttpUtil.closeDialog();
                    String resp = response.body().string();
                    LogUtil.d(TAG, resp);
                    if (resp.equals("此漂流瓶已经沉入海底！")) {
                        LogUtil.d(TAG, "厌恶完成");
                    }
                }
            });
        }
    }
    //http请求-捡一个漂流瓶，成功后修改按钮为换一个
    private void getWater() {
        RequestBody getBody = new FormBody.Builder().add("uid", MyApplication.user.getUid() + "").build();
        HttpUtil.sendPostOkHttpRequest(HttpPathUtil.randomSelectDrift_note(), getBody, true, new Callback() {
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
                serversLoadTimes = 0;HttpUtil.closeDialog();
                final String resp = response.body().string();
                LogUtil.d(TAG, resp);
                if (Util.JsonUtils.isGoodJson(resp)) {
                    dn = new Gson().fromJson(resp, DriftNote.class);
                    setGetWater(dn);
                } else {
                    showError();
                }
            }
        });
    }
    //http请求-发送漂流瓶
    private void sendWater() {
        RequestBody body = new FormBody.Builder()
                .add("uid", MyApplication.user.getUid() + "")
                .add("title", "前台没提供这个梗，以后再说")
                .add("driftContent", mainWaterEdit.getText().toString().trim())
                .add("identifier", 1 + "").build();
        HttpUtil.sendPostOkHttpRequest(HttpPathUtil.addDrift_note(), body, true, new Callback() {
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
                serversLoadTimes = 0;HttpUtil.closeDialog();
                String resp = response.body().string();
                LogUtil.d(TAG, resp);
                if (resp.equals("发送成功！")) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final SweetAlertDialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("SUCCESS").setContentText("发送成功^0^").setConfirmText("关闭");
                                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        dialog.dismiss();
                                    }
                                });dialog.show();
                                mainWaterEdit.setText("");
                            }
                        });
                    }
                } else {
                    showError();
                }
            }
        });
    }

    //关闭评论区
    private void colseReview() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    reviewContent.setText("");
                    reviewContainer.setVisibility(View.GONE);
                    buttonContainer.setVisibility(View.VISIBLE);
                }
            });
        }
    }
    //捡瓶子视图--待完善
    private void setGetWater(final DriftNote dn) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageContainer.setVisibility(View.GONE);//关闭空视图
                    historyReview.setVisibility(View.GONE);//关闭历史视图
                    historyReview.setAdapter(null);
                    mainWaterEdit.setVisibility(View.GONE);//关闭扔瓶子视图
                    mainWaterGet.setText("换一个");
                    mainWaterEdit.setText("");
                    mainWaterText.setVisibility(View.VISIBLE);
                    mainWaterText.setText("FROM:" + dn.getSendId() + "\n    " + dn.getDriftContent());
                    mainWaterReview.setVisibility(View.VISIBLE);
                    getContainer.setVisibility(View.VISIBLE);
                    mainWaterNotLook.setChecked(false);
                    adapter = new DriftNoteEnvaluate(dn.getDrift_evaluateList());
                    mainWaterReview.setAdapter(adapter);
                }
            });
        }
    }
    //点击历史图子项显示
    public void setHistoryNote (final DriftNote dn) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageContainer.setVisibility(View.GONE);//关闭空视图
                    historyReview.setVisibility(View.GONE);//关闭历史视图
                    historyReview.setAdapter(null);
                    mainWaterEdit.setVisibility(View.GONE);//关闭扔瓶子视图
                    mainWaterGet.setText("捡瓶子");
                    mainWaterEdit.setText("");
                    mainWaterText.setVisibility(View.VISIBLE);
                    mainWaterText.setText("FROM:" + dn.getSendId() + "\n    " + dn.getDriftContent());
                    mainWaterReview.setVisibility(View.VISIBLE);
                    getContainer.setVisibility(View.GONE);
                    mainWaterNotLook.setChecked(false);
                    adapter = new DriftNoteEnvaluate(dn.getDrift_evaluateList());
                    mainWaterReview.setAdapter(adapter);
                }
            });
        }
    }
    //发瓶子视图
    private void setSendWater() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageContainer.setVisibility(View.GONE);
                    historyReview.setVisibility(View.GONE);//关闭历史视图
                    historyReview.setAdapter(null);
                    mainWaterEdit.setVisibility(View.VISIBLE);
                    mainWaterEdit.setText("");mainWaterSend.setText("扔瓶子");mainWaterGet.setText("捡瓶子");
                    mainWaterText.setVisibility(View.GONE);
                    mainWaterReview.setVisibility(View.GONE);
                    getContainer.setVisibility(View.GONE);
                }
            });
        }
    }
    //历史视图
    private void setHistory(final List<DriftNote> DriftNotes) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageContainer.setVisibility(View.GONE);
                    mainWaterEdit.setVisibility(View.GONE);
                    historyReview.setVisibility(View.VISIBLE);
                    mainWaterEdit.setText("");mainWaterSend.setText("扔瓶子");mainWaterGet.setText("捡瓶子");
                    mainWaterText.setVisibility(View.GONE);
                    mainWaterReview.setVisibility(View.GONE);
                    getContainer.setVisibility(View.GONE);

                    if (getActivity() != null) {
                        adapter_notes = new DriftNotes(DriftNotes, mainWater.this, getActivity());
                        historyReview.setAdapter(adapter_notes);
                    }
                }
            });
        }
    }
    //空视图
    private void setEmpty() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    imageContainer.setVisibility(View.VISIBLE);
                    mainWaterEdit.setVisibility(View.GONE);
                    historyReview.setVisibility(View.GONE);//关闭历史视图
                    historyReview.setAdapter(null);
                    mainWaterEdit.setText("");mainWaterSend.setText("扔瓶子");mainWaterGet.setText("捡瓶子");
                    mainWaterText.setVisibility(View.GONE);
                    mainWaterReview.setVisibility(View.GONE);
                    getContainer.setVisibility(View.GONE);
                }
            });
        }
    }
    //初始化视图
    private void initView(View view) {
        mainWaterSend = (Button) view.findViewById(R.id.main_water_send);
        mainWaterHistory = (Button) view.findViewById(R.id.main_water_history);
        mainWaterGet = (Button) view.findViewById(R.id.main_water_get);
        sendReview = (Button) view.findViewById(R.id.main_water_send_review);
        getContainer = (LinearLayout) view.findViewById(R.id.hate_container);
        reviewContainer = (LinearLayout) view.findViewById(R.id.main_water_review_container);
        buttonContainer = (LinearLayout) view.findViewById(R.id.button_container);
        imageContainer = (LinearLayout) view.findViewById(R.id.main_water_image);
        mainWaterWriteReview = (ImageView) view.findViewById(R.id.main_water_write_review);
        mainWaterNotLook = (CheckBox) view.findViewById(R.id.main_water_not_look);
        mainWaterReview = (RecyclerView) view.findViewById(R.id.main_water_review);
        historyReview = (RecyclerView) view.findViewById(R.id.main_water_history_recycler);
        mainWaterText = (TextView) view.findViewById(R.id.main_water_text);
        mainWaterEdit = (EditText) view.findViewById(R.id.main_water_edit);
        reviewContent = (EditText) view.findViewById(R.id.main_water_review_content);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        mainWaterReview.setLayoutManager(layoutManager);
        historyReview.setLayoutManager(layoutManager2);
        mainWaterReview.setNestedScrollingEnabled(false);
        historyReview.setNestedScrollingEnabled(false);

        mainWaterEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().equals("")) {
                    mainWaterSend.setText("扔瓶子");
                } else {
                    mainWaterSend.setText("扔出");
                }
            }
        });
        mainWaterWriteReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (reviewContainer.getVisibility() == View.GONE) {
                                mainWaterWriteReview.setImageResource(R.drawable.waterclose);
                                reviewContainer.setVisibility(View.VISIBLE);
                                buttonContainer.setVisibility(View.GONE);
                            } else {
                                mainWaterWriteReview.setImageResource(R.drawable.note_review_pl);
                                reviewContainer.setVisibility(View.GONE);
                                buttonContainer.setVisibility(View.VISIBLE);
                                reviewContent.setText("");
                            }
                        }
                    });
                }
            }
        });
        mainWaterGet.setOnClickListener(this);
        mainWaterHistory.setOnClickListener(this);
        mainWaterSend.setOnClickListener(this);
        sendReview.setOnClickListener(this);
    }

    //显示出错弹窗
    private void showError() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final SweetAlertDialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("WARN").setContentText("系统繁忙，请稍后再试>_<").setConfirmText("知道了");
                    dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            dialog.dismiss();
                        }
                    });dialog.show();
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dn != null) {
            waterBack();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setEmpty();
    }
}
