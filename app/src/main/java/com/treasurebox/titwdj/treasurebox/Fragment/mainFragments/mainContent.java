package com.treasurebox.titwdj.treasurebox.Fragment.mainFragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.treasurebox.titwdj.treasurebox.Activity.FragmentActivitys;
import com.treasurebox.titwdj.treasurebox.Activity.NoteActivity;
import com.treasurebox.titwdj.treasurebox.Custom.View.AbilityToFigure;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendList;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendList_Table;
import com.treasurebox.titwdj.treasurebox.Model.nother.Note;
import com.treasurebox.titwdj.treasurebox.Model.nother.Suggest;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Model.nother.HeWeather5;
import com.treasurebox.titwdj.treasurebox.Model.nother.BingImage;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
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

/**
 * Created by 11393 on 2017/8/3.
 * 主页碎片
 * 在这里进行一个总页与5个功能模块的视图构建与相应事件，具体功能的点击事件在其他碎片中实现
 */
public class mainContent extends Fragment {
    //常量定义
    private static final String TAG = "mainContent";

    View view;
    ImageView imageview;
    LinearLayout adviceContainer;
    AbilityToFigure abilityView, emotionView;
    TextView suggest1, suggest2, active;

    private HeWeather5 weather = new HeWeather5();
    protected static String area = "";
    private SharedPreferences sharedPreferences;

    //初始化视图
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_home, container, false);

        initView(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (area != ""){
            getWeather();
        }
    }

    //用有参数的静态工厂构造Fragment，参数指定所在地区
    public static mainContent newInstance(String p, String c, String a) {
        mainContent contentFragment = new mainContent();
        Bundle bundle = new Bundle();
        area = a;
        bundle.putString("p", p);
        bundle.putString("c", c);
        bundle.putString("a", a);
        contentFragment.setArguments(bundle);
        return contentFragment;
    }

    //从参数体获取数据初始化碎片
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null) {
            sharedPreferences = getActivity().getSharedPreferences("noteFlag", Context.MODE_PRIVATE);
        }
    }

    /**
     * 获取天气数据
     * 和风天气数据apikey：ef55ba2f11664441945488ec351c81cf
     * 请求地址--和风本网api：https://free-api.heweather.com/v5/weather?city=尖草坪区&key=ef55ba2f11664441945488ec351c81cf
     */
    private static final String key = "ef55ba2f11664441945488ec351c81cf";
    private void getWeather() {
        HttpUtil.sendOkHttpRequest("https://free-api.heweather.com/v5/weather?city=" + area + "&key=" + key, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { e.printStackTrace(); }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                Log.d(TAG, "和风天气数据: " + str);
                try {
                    if (Util.JsonUtils.isGoodJson(str)) {
                        weather = new Gson().fromJson(str, HeWeather5.class);
                    }
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {//此处关闭了天气数据的直接显示
                                //setWeatherData(weather, view);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //加载必应图片
    private void loadBingPic() {
        String getBingPicPath = "http://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1";
        final String bingPic = "http://cn.bing.com";
        final BingImage[] bingPath = new BingImage[1];
        HttpUtil.sendOkHttpRequest(getBingPicPath, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPicPath = response.body().string();
                bingPath[0] = new Gson().fromJson(bingPicPath, BingImage.class);
                Log.d(TAG, "获得必应图片路径: " + bingPath[0].imageList.get(0).imageurl + "--" + bingPath[0].imageList.get(0).enddate);
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {//使用Glide通过网络路径加载图片
                        Glide.with(MyApplication.getContext()).load(bingPic + bingPath[0].imageList.get(0).imageurl).into(imageview);
                    }
                });
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

    //获取纸条数据--好友的纸条
    private void getMyFriendNotes() {
        RequestBody body = new FormBody.Builder()
                .add("uid", MyApplication.user.getUid() + "")
                .add("myuserNunber", MyApplication.user.getNumber())
                .add("noteId", 0 + "").build();
        HttpUtil.sendPostOkHttpRequest(HttpPathUtil.showAllfriNote(), body, true, new Callback() {
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
                if (Util.JsonUtils.isGoodJson(resp)) {
                    int oldId = sharedPreferences.getInt("noteFlag", 1);
                    LogUtil.d(TAG, "oldId:" + oldId);
                    final List<Note> notes = JSON.parseArray(resp, Note.class), newNotes = new ArrayList<Note>();
                    for (Note note:notes) {
                        if (note.getNoteId() > oldId) {
                            newNotes.add(note);
                        }
                    }
                    if (newNotes.size() > 0) {
                        if (getActivity() != null)
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    List<String> numbers = new ArrayList<String>();
                                    for (int i = 0; i < newNotes.size(); i++) {
                                        Note note = newNotes.get(i);
                                        if (!numbers.contains(note.getNumber())) {
                                            final FriendList friend = SQLite.select().from(FriendList.class)
                                                    .where(FriendList_Table.friendNumber.eq(note.getNumber())).querySingle();
                                            if (friend != null) {
                                                numbers.add(friend.getFriendNumber());
                                                if (i != 0) {
                                                    active.setText(active.getText().toString().trim() + "\n" + (
                                                            friend.getFriendUsername().equals("")?friend.getFriendNickname():friend.getFriendUsername()) +
                                                            " 发表了新纸条");
                                                } else {
                                                    active.setText(friend.getFriendUsername().equals("")?friend.getFriendNickname():friend.getFriendUsername() +
                                                            " 发表了新纸条");
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                        active.setClickable(true);
                    } else {
                        if (getActivity() != null)
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    active.setText("暂无好友动态—_—");
                                }
                            });
                        active.setClickable(false);
                    }
                } else {
                    if (getActivity() != null)
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                active.setText("系统正忙...");
                            }
                        });
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getMyFriendNotes();
                    getSuggest();
                }
            });
        }
    }

    //给动态文本设置点击事件
    private void setActiveClick() {
        active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    Intent intent = new Intent(getActivity(), NoteActivity.class);
                    intent.putExtra("flag", NoteActivity.FRIEND_NOTE);
                    getActivity().startActivity(intent);
                }
            }
        });
    }

    //初始化视图
    private void initView(View view) {
        adviceContainer = (LinearLayout) view.findViewById(R.id.main_advice_container);
        imageview = (ImageView) view.findViewById(R.id.main_home_image);
        abilityView = (AbilityToFigure) view.findViewById(R.id.main_home_ability);
        emotionView = (AbilityToFigure) view.findViewById(R.id.main_home_emotion);
        suggest1 = (TextView) view.findViewById(R.id.main_home_suggest_content1);
        suggest2 = (TextView) view.findViewById(R.id.main_home_suggest_content2);
        active = (TextView) view.findViewById(R.id.main_home_active_content);
        setActiveClick();
        loadBingPic();

        if (MyApplication.user != null) {
            adviceContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getActivity() != null) {
                        Intent intent = new Intent(getActivity(), FragmentActivitys.class);
                        intent.putExtra(FragmentActivitys.extra_title, "个人数据分析");
                        intent.putExtra(FragmentActivitys.extra_flag, FragmentActivitys.adviceDetail);
                        getActivity().startActivity(intent);
                    }
                }
            });
            abilityView.setModel(MyApplication.userAbility.getAbility1Name(), MyApplication.userAbility.getAbility1Value());
            emotionView.setModel(MyApplication.userAbility.getAbility2Name(), MyApplication.userAbility.getAbility2Value());
        }
    }
}
