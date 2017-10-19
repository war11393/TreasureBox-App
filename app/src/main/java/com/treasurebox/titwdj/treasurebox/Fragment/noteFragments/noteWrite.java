package com.treasurebox.titwdj.treasurebox.Fragment.noteFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.treasurebox.titwdj.treasurebox.Activity.FragmentActivitys;
import com.treasurebox.titwdj.treasurebox.Model.nother.Note;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.AppManager;
import com.treasurebox.titwdj.treasurebox.Utils.BitmapUtils;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Custom.View.PopWindow_ChooseImage;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.FriendList;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.client;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.dialog;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.maxLoadTimes;
import static com.treasurebox.titwdj.treasurebox.Utils.HttpUtil.serversLoadTimes;


/**
 * Created by 11393 on 2017/8/14.
 * 写纸条碎片
 */
public class noteWrite extends Fragment implements View.OnClickListener{
    private static final String TAG = "noteWrite";
    private static final int NOTE_PERMISSION_REQUEST = 200;
    private static final int NOTE_PERMISSION_RESULT = 201;

    EditText noteWriteText;
    GridLayout noteWritePhotos;
    CheckBox noteWriteLocate;
    Button noteWriteConfirm;
    TextView noteWritePermissionText, noteWriteWithText;
    LinearLayout noteWritePermission, noteWriteWith;
    ImageView noteWriteHeart1, noteWriteHeart2, noteWriteHeart3, noteWriteHeart4, noteWriteHeart5;
    ImageView[] hearts = {noteWriteHeart1, noteWriteHeart2, noteWriteHeart3, noteWriteHeart4, noteWriteHeart5};
    int[] resIds = {R.drawable.note_heartsty_1, R.drawable.note_heartsty_2, R.drawable.note_heartsty_3, R.drawable.note_heartsty_4, R.drawable.note_heartsty_5};

    private int width;
    private PopWindow_ChooseImage mpopupWindow = null;
    private Activity activity;
    public static List<FriendList> userFriendLists = new ArrayList<>();

    private int heartsty = 3;
    public static ArrayList<String> pics = new ArrayList<>();
    public static List<String> notePermissionList = new ArrayList<>();
    public static int notePermissionStatus = 0;
    public static List<String> noteWithList = new ArrayList<>();
    public static List<String> noteWithNameList = new ArrayList<>();
    private String noteWith = "无";
    private String noteContent = "";
    private String noteLocate = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_note_write, container, false);
        initViewAndSetClick(view);
        userFriendLists = MyApplication.checkFriendList();
        return view;
    }

    @Override//处理点击
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.note_write_confirm:
                setData();//装填部分数据,作为参数
                //添加纸条
                MultipartBody.Builder builder = new MultipartBody.Builder();
                for (String path:pics) {//添加图片文件
                    File file = new File(BitmapUtils.compressImageUpload(path));
                    builder.addFormDataPart("ufacing", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));
                }
                builder.addFormDataPart("uid", MyApplication.user.getUid() + "");
                builder.addFormDataPart("mood", heartsty + "");
                builder.addFormDataPart("noteAdout", new Gson().toJson(noteWithList));
                builder.addFormDataPart("noteContent", noteContent);
                builder.addFormDataPart("locate", noteLocate);
                matchPermission(builder);
                RequestBody body = builder.build();
                HttpUtil.sendPostOkHttpRequest(HttpPathUtil.addNote(), body, true, new Callback() {
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
                        if (Util.JsonUtils.isGoodJson(resp)){
                            Note Note = new Gson().fromJson(resp, Note.class);
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final SweetAlertDialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("SUCCESS").setContentText("记录成功").setConfirmText("返回");
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
                        } else {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final SweetAlertDialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                                .setTitleText("WARN").setContentText("系统正忙，请稍后再试").setConfirmText("知道了");
                                        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                dialog.dismiss();
                                            }
                                        });
                                        dialog.show();
                                    }
                                });
                            }
                        }
                    }
                });
                break;
            case R.id.note_write_heart1:
                hearts[heartsty - 1].setImageResource(resIds[heartsty - 1]);
                heartsty = 1;
                hearts[0].setImageResource(R.drawable.note_heartsty_12);
                break;
            case R.id.note_write_heart2:
                hearts[heartsty - 1].setImageResource(resIds[heartsty - 1]);
                heartsty = 2;
                hearts[1].setImageResource(R.drawable.note_heartsty_22);
                break;
            case R.id.note_write_heart3:
                hearts[heartsty - 1].setImageResource(resIds[heartsty - 1]);
                heartsty = 3;
                hearts[2].setImageResource(R.drawable.note_heartsty_32);
                break;
            case R.id.note_write_heart4:
                hearts[heartsty - 1].setImageResource(resIds[heartsty - 1]);
                heartsty = 4;
                hearts[3].setImageResource(R.drawable.note_heartsty_42);
                break;
            case R.id.note_write_heart5:
                hearts[heartsty - 1].setImageResource(resIds[heartsty - 1]);
                heartsty = 5;
                hearts[4].setImageResource(R.drawable.note_heartsty_52);
                break;
            case R.id.note_write_permission:
                Intent intent = new Intent(getActivity(), FragmentActivitys.class);
                intent.putExtra(FragmentActivitys.extra_title, "谁能看到");
                intent.putExtra(FragmentActivitys.extra_flag, FragmentActivitys.notepermission);
                startActivityForResult(intent, NOTE_PERMISSION_REQUEST);
                break;
            case R.id.note_write_with:
                Intent intent1 = new Intent(getActivity(), FragmentActivitys.class);
                intent1.putExtra(FragmentActivitys.extra_title, "关于谁");
                intent1.putExtra(FragmentActivitys.extra_flag, FragmentActivitys.noteWith);
                startActivityForResult(intent1, NOTE_PERMISSION_REQUEST);
                break;
            default:
                break;
        }
    }

    //匹配权限数据
    private void matchPermission(MultipartBody.Builder builder) {//0--全部，1--自己，2--只让谁看，3--不让谁看
        switch (notePermissionStatus) {
            case 0://设置不可见用户列表--为空
                builder.addFormDataPart("obvious", 1 + "");
                builder.addFormDataPart("friendNumberList", new Gson().toJson(notePermissionList));
                break;
            case 1://设置可见用户列表--为空
                builder.addFormDataPart("obvious", 0 + "");
                builder.addFormDataPart("friendNumberList", new Gson().toJson(notePermissionList));
                break;
            case 2://设置不可见用户列表
                builder.addFormDataPart("obvious", 1 + "");
                builder.addFormDataPart("friendNumberList", new Gson().toJson(notePermissionList));
                break;
            case 3://设置可见用户列表
                builder.addFormDataPart("obvious", 0 + "");
                builder.addFormDataPart("friendNumberList", new Gson().toJson(notePermissionList));
                break;
        }
    }

    //装载部分参数
    private void setData() {
        if (noteWriteLocate.isChecked()){
            noteLocate = MyApplication.locate.get("省") + "-" + MyApplication.locate.get("市") + "-" + MyApplication.locate.get("区");
        } else {
            noteLocate = "";
        }
        noteContent = noteWriteText.getText().toString().trim();
    }

    //为弹出窗口监听
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            mpopupWindow.dismiss();
            switch (v.getId()) {
                case R.id.camera_take://打开相机拍照
                    ((FragmentActivitys) getActivity()).cameraPath = Util.checkCameraPermission(getActivity());
                    break;
                case R.id.camera_choose://从相册选择
                    Util.startPicChoose(getActivity());
                    break;
                default:
                    break;
            }
        }
    };

    @Override//每次显示都更新一次数据
    public void onResume() {//重新装填数据
        super.onResume();
        userFriendLists = MyApplication.checkFriendList();//重新初始化好友列表数据

        noteWritePhotos.removeAllViews();
        //设置图片
        for (int i = 0; i < pics.size(); i++) {
            addPicture(pics.get(i).toString());
        }
        addPhoto(R.drawable.note_write_addphoto);//设置添加图片按钮
        //设置关于谁的显示
        if (noteWithNameList.size() == 0){
            noteWriteWithText.setText("无");
        }
        for (int i = 0; i < noteWithNameList.size(); i++) {
            if (i == 0) {
                noteWriteWithText.setText(noteWithNameList.get(i));
            } else {
                noteWriteWithText.setText(noteWriteWithText.getText().toString() + "," + noteWithNameList.get(i));
            }
        }
        //设置谁能看见的显示
        switch (notePermissionStatus) {
            case 0:noteWritePermissionText.setText("所有好友");break;
            case 1:noteWritePermissionText.setText("仅自己");break;
            case 2:noteWritePermissionText.setText("部分好友");break;
            case 3:noteWritePermissionText.setText("除了...");break;
        }
    }

    @Override//销毁时重置pics列表
    public void onDestroy() {
        super.onDestroy();
        pics.clear();
    }

    //添加图片
    public void addPicture(final String path) {
        final ImageView image = new ImageView(getActivity());
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final SweetAlertDialog dialog = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("是否删除这张图片")
                                .setConfirmText("确认")
                                .setCancelText("取消");
                        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                pics.remove(path);
                                noteWritePhotos.removeView(image);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
                return true;
            }
        });

        GridLayout.LayoutParams ps = new GridLayout.LayoutParams(new LinearLayout.LayoutParams(width / 3 - Util.dip2px(getContext(), 13), width / 3 - Util.dip2px(getContext(), 13)));
        ps.setMargins(Util.dip2px(getContext(), 1), Util.dip2px(getContext(), 1), Util.dip2px(getContext(), 1), Util.dip2px(getContext(), 1));
        image.setLayoutParams(ps);

        Glide.with(getActivity()).load(path).into(image);

        noteWritePhotos.addView(image);
    }

    //添加图片的按钮
    private void addPhoto(int resId) {
        ImageView image = new ImageView(getActivity());
        image.setScaleType(ImageView.ScaleType.FIT_XY);

        GridLayout.LayoutParams ps = new GridLayout.LayoutParams(new LinearLayout.LayoutParams(width / 3 - Util.dip2px(getContext(), 13), width / 3 - Util.dip2px(getContext(), 13)));
        ps.setMargins(Util.dip2px(getContext(), 1), Util.dip2px(getContext(), 1), Util.dip2px(getContext(), 1), Util.dip2px(getContext(), 1));
        image.setLayoutParams(ps);

        Glide.with(getActivity()).load(resId).into(image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpopupWindow = new PopWindow_ChooseImage(getActivity(), itemsOnClick);
                mpopupWindow.showAtLocation(getActivity().findViewById(R.id.note_write_total), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });
        noteWritePhotos.addView(image);
    }

    //初始化视图并添加点击监听
    private void initViewAndSetClick(View view) {
        hearts[4] = (ImageView) view.findViewById(R.id.note_write_heart5);
        hearts[3] = (ImageView) view.findViewById(R.id.note_write_heart4);
        hearts[2] = (ImageView) view.findViewById(R.id.note_write_heart3);
        hearts[1] = (ImageView) view.findViewById(R.id.note_write_heart2);
        hearts[0] = (ImageView) view.findViewById(R.id.note_write_heart1);
        noteWriteWith = (LinearLayout) view.findViewById(R.id.note_write_with);
        noteWriteWithText = (TextView) view.findViewById(R.id.note_write_with_text);
        noteWritePermission = (LinearLayout) view.findViewById(R.id.note_write_permission);
        noteWritePermissionText = (TextView) view.findViewById(R.id.note_write_permission_text);
        noteWriteLocate = (CheckBox) view.findViewById(R.id.note_write_locate);
        noteWritePhotos = (GridLayout) view.findViewById(R.id.note_write_photos);
        noteWriteText = (EditText) view.findViewById(R.id.note_write_text);
        noteWriteConfirm = (Button) view.findViewById(R.id.note_write_confirm);

        noteWritePhotos.setColumnCount(3);
        width = getContext().getResources().getDisplayMetrics().widthPixels;

        if (MyApplication.locate != null){
            noteWriteLocate.setText("显示地点（" + MyApplication.locate.get("省") + "-" + MyApplication.locate.get("市") + "-" + MyApplication.locate.get("区") + "）");
        } else {
            noteWriteLocate.setText("显示地点（暂未获取到位置信息）");
        }

        hearts[4].setOnClickListener(this);
        hearts[3].setOnClickListener(this);
        hearts[2].setOnClickListener(this);
        hearts[1].setOnClickListener(this);
        hearts[0].setOnClickListener(this);
        noteWritePermission.setOnClickListener(this);
        noteWriteWith.setOnClickListener(this);
        noteWriteConfirm.setOnClickListener(this);
    }
}
