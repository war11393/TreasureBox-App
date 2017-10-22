package com.treasurebox.titwdj.treasurebox.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Fragment.mainFragments.mainWater;
import com.treasurebox.titwdj.treasurebox.Model.nother.DriftNote;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.AppManager;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;

import java.io.IOException;
import java.net.SocketTimeoutException;
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

public class DriftNotes extends RecyclerView.Adapter<DriftNotes.ViewHolder> {
    private static final String TAG = "DriftNotes";

    private Context mContext;
    private List<DriftNote> DriftNotes;
    private mainWater mainWater;
    private Activity mActivity;

    public DriftNotes(List<DriftNote> DriftNotes, mainWater mainWater, Activity activity) {
        this.DriftNotes = DriftNotes;
        this.mainWater = mainWater;
        this.mActivity = activity;
    }

    @Override
    public void onBindViewHolder(DriftNotes.ViewHolder holder, int position) {
        final DriftNote drift = DriftNotes.get(position);
        holder.topText.setText(MyApplication.user.getUsername().equals(drift.getUserName())?"我：":drift.getUserName() + "：\n    " + drift.getDriftContent());
        holder.bottomText.setText(drift.getDriftTime());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestBody body = new FormBody.Builder().add("driftId", drift.getDriftId() + "").build();
                HttpUtil.sendPostOkHttpRequest(HttpPathUtil.selectDriftEvaluate(), body, true, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
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
                            List<DriftNote.DriftEvaluateListBean> evaluateList = JSON.parseArray(resp, DriftNote.DriftEvaluateListBean.class);
                            drift.setDrift_evaluateList(evaluateList);
                        } else {
                            mActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "加载评论失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        mainWater.setHistoryNote(drift);
                        mainWater.dn = drift;
                    }
                });
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView topText, bottomText;
        LinearLayout layout;
        public ViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView;
            topText = itemView.findViewById(R.id.drift_left);
            bottomText = itemView.findViewById(R.id.drift_right);
        }
    }

    @Override
    public DriftNotes.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_driftnote, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return DriftNotes.size();
    }
}
