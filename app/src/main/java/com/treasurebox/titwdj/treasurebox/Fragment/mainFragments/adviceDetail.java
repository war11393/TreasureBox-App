package com.treasurebox.titwdj.treasurebox.Fragment.mainFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.google.gson.Gson;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Model.nother.MoodAdvice;
import com.treasurebox.titwdj.treasurebox.Model.nother.MoodValue;
import com.treasurebox.titwdj.treasurebox.Model.nother.Suggest;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.client;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.dialog;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.maxLoadTimes;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.serversLoadTimes;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.showError;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.showErrorToast;

public class adviceDetail extends Fragment {
    private static final String TAG = "adviceDetail";

    private final String[] mLabelsThree = {"1", "2", "3", "4", "5", "6", "7"};
    private final float[][] mValuesThree = {{405f, 507f, 400f, 800f, 205f, 300f, 605f},
            {105f, 205f, 150f, 500f, 505f, 505f, 300f},
            {800f, 750f, 780f, 150f, 800f, 800f, 500f}};

    LineChartView lineChart;
    TextView suggest1, suggest2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advice_detail, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getValueData();
                    getSuggest();
                }
            });
        }
    }

    //HTTP请求-获取折线数据
    private void getValueData() {
        RequestBody body = new FormBody.Builder().add("userNumber", MyApplication.user.getNumber()).build();
        HttpUtil.sendPostOkHttpRequest(HttpPathUtil.selectAllmoodValue(), body, true, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.d(TAG, e.toString() + "   正重新尝试链接...");
                if (e.getClass().equals(SocketTimeoutException.class) && serversLoadTimes < maxLoadTimes)//如果超时并未超过指定次数，则重新连接
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
                serversLoadTimes = 0;
                dialog.dismiss();
                final String resp = response.body().string();
                LogUtil.d(TAG, resp);
                if(Util.JsonUtils.isGoodJson(resp)) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                List<MoodValue> moodValues = JSON.parseArray(resp, MoodValue.class);
                                Collections.reverse(moodValues);
                                setLineData(moodValues);
                            }
                        });
                    }
                } else {
                    showErrorToast();
                }
            }
        });
    }

    //获取系统建议
    private void getSuggest(){
        RequestBody body = new FormBody.Builder().add("number", MyApplication.user.getNumber()).build();
        HttpUtil.sendPostOkHttpRequest(HttpPathUtil.getSuggest(), body, true, new Callback() {
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
                final String resp = response.body().string();
                LogUtil.d(TAG, resp);
                if (Util.JsonUtils.isGoodJson(resp)) {
                    final Suggest suggest = new Gson().fromJson(resp, Suggest.class);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String[] strings = suggest.getDiet().toString().split("sss");
                                LogUtil.d(TAG, strings.length + "");
                                for (int i = 0; i < strings.length; i++) {
                                    if (i == 0) {
                                        suggest1.setText(strings[0].trim());
                                    } else {
                                        suggest1.setText(suggest1.getText().toString().trim() + "\n" + strings[i].trim());
                                    }
                                }
                                suggest2.setText(suggest.getPoint().toString().trim());
                            }
                        });
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                suggest1.setText("系统正忙...");
                                suggest2.setText("系统正忙...");
                            }
                        });
                    }
                }
            }
        });
    }

    //设置折线图视图
    private void setLineData(final List<MoodValue> moodValues) {
        final LineSet data;
        if (moodValues == null) {
            data = new LineSet();
            data.addPoint("0", 0);
            data.addPoint("1", 0);
            data.addPoint("2", 0);
            data.addPoint("3", 0);
            data.addPoint("4", 0);
            data.addPoint("5", 0);
            data.addPoint("6", 0);
            data.addPoint("7", 0);
            data.addPoint("8", 0);
            data.addPoint("9", 0);

            data.setColor(Color.parseColor("#FF58C674"))//设置直线颜色
                    .setDotsStrokeThickness(Tools.fromDpToPx(2))
                    .setDotsStrokeColor(Color.parseColor("#FF58C674"))//设置 圈圈颜色
                    .setDotsColor(Color.parseColor("#eef1f6"));
            lineChart.addData(data);

            lineChart.setBorderSpacing(1)
                    .setAxisColor(0xff000000)
                    .setAxisBorderValues(0, 24, 12);

            lineChart.show();
        } else {
            data = new LineSet();
            for (int i = 0; i < 10; i++) {
                if (i < moodValues.size()) {
                    data.addPoint(moodValues.get(i).getTime(), moodValues.get(i).getValue() + 12);
                } else {
                    data.addPoint("", 0);
                }
            }
            data.setColor(Color.parseColor("#FF58C674"))//设置直线颜色
                    .beginAt(0).endAt(moodValues.size())
                    .setDotsStrokeThickness(Tools.fromDpToPx(2))
                    .setDotsStrokeColor(Color.parseColor("#FF58C674"))//设置 圈圈颜色
                    .setDotsColor(Color.parseColor("#eef1f6"));
            lineChart.addData(data);

            lineChart.setBorderSpacing(1)
                    .setAxisColor(0xff000000)
                    .setAxisBorderValues(0, 24, 12);

            lineChart.show();
        }
    }

    private void initView(View view) {
        lineChart = (LineChartView) view.findViewById(R.id.advice_line_chart);

        suggest1 = (TextView) view.findViewById(R.id.main_home_suggest_content1);
        suggest2 = (TextView) view.findViewById(R.id.main_home_suggest_content2);
    }
}
