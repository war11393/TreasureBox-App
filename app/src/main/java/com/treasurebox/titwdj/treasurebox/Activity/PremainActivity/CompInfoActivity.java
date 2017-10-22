package com.treasurebox.titwdj.treasurebox.Activity.PremainActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lljjcoder.citypickerview.widget.CityPicker;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.AppManager;
import com.treasurebox.titwdj.treasurebox.Utils.BitmapUtils;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.HttpUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.ProjectUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;
import com.treasurebox.titwdj.treasurebox.Activity.BaseActivity;
import com.treasurebox.titwdj.treasurebox.Custom.View.DatePicker;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Custom.View.PopWindow_ChooseImage;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.User;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Calendar;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
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

public class CompInfoActivity extends BaseActivity {
    private static final String TAG = "CompInfoActivity";

    CircleImageView compinfoHeadimage;
    RadioButton compinfoSexMan, compinfoSexWoman, compinfoSexNo;
    TextView compinfoCitypic, compinfoTimepic, compinfoAge, compinfoXingzuo;
    AppCompatSpinner compinfoBloodtype;
    EditText compinfoWorkinfo, compinfoHobby, compinfoSign, compinfoTbname, compinfoUsername;
    Button compinfoConfirmbtn;

    private String bloodChoose = "O型";
    private String sexChoose = "";
    public static String headImagePath = "";
    //相机常量
    private PopWindow_ChooseImage mpopupWindow = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compinfo);
        initView();//初始化视图
        setView();
    }

    private void setView() {
        //装载数据
        headImagePath = HttpPathUtil.getImagePre() + "tit.png";
        final User user = new User();
        user.setBlood("O型");
        user.setUfacing("tit.png");
        Glide.with(this).load(headImagePath).into(compinfoHeadimage);

        //头像点击监听,弹出弹窗
        compinfoHeadimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mpopupWindow = new PopWindow_ChooseImage(CompInfoActivity.this, itemsOnClick);
                mpopupWindow.showAtLocation(CompInfoActivity.this.findViewById(R.id.activity_compinfo_layout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });
        setBloodChoose(user);//血型选择
        setTimeChoose();//时间选择
        setCityChoose();//城市选择

        //确认按钮点击事件
        compinfoConfirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSexChoose();//性别选择
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                if (!headImagePath.equals(user.getUfacing2())) {
                    File imageFile = new File(BitmapUtils.compressImageUpload(headImagePath));
                    builder.addFormDataPart("ufacing", imageFile.getName(), RequestBody.create(MediaType.parse("image/*"), imageFile));
                } else {
                    File imageFile = new File(Util.picturePath + "/" + Util.headPath + "/" + user.getNoPreUfacing());
                    builder.addFormDataPart("ufacing", imageFile.getName(), RequestBody.create(MediaType.parse("image/*"), imageFile));
                }
                builder.addFormDataPart("uid", getIntent().getIntExtra("uid", -1) + "");
                builder.addFormDataPart("username", compinfoUsername.getText().toString().trim());
                builder.addFormDataPart("place", compinfoCitypic.getText().toString().trim());
                builder.addFormDataPart("constellation", compinfoXingzuo.getText().toString().trim().substring(3));
                builder.addFormDataPart("blood", bloodChoose);
                builder.addFormDataPart("signature", compinfoSign.getText().toString().trim());
                builder.addFormDataPart("birthday", compinfoTimepic.getText().toString().trim());
                builder.addFormDataPart("hobby", compinfoHobby.getText().toString().trim());
                builder.addFormDataPart("job", compinfoWorkinfo.getText().toString().trim());
                builder.addFormDataPart("gender", sexChoose);
                builder.addFormDataPart("personalPassword", "");//二级密码暂空
                builder.addFormDataPart("age", compinfoAge.getText().toString().trim().substring(3));
                RequestBody body = builder.build();

                HttpUtil.sendPostOkHttpRequest(HttpPathUtil.updateRoleData(), body, new Callback() {
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
                        serversLoadTimes = 0;
                        String resp = response.body().string();
                        LogUtil.d(TAG, resp);
                        if (Util.JsonUtils.isGoodJson(resp)) {
                            MyApplication.user = new Gson().fromJson(resp, User.class);
                            MyApplication.user.save();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final SweetAlertDialog dialog = new SweetAlertDialog(CompInfoActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("操作成功！").setConfirmText("去登陆");
                                    dialog.setCancelable(false);
                                    dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            dialog.dismiss();
                                            Intent intent = new Intent(CompInfoActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            AppManager.getInstance().finishActivity(CompInfoActivity.class);
                                        }
                                    });
                                    dialog.show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final SweetAlertDialog dialog = new SweetAlertDialog(CompInfoActivity.this, SweetAlertDialog.ERROR_TYPE)
                                            .setTitleText("WARN").setContentText("系统正忙，请稍后重试0_0");
                                    dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            dialog.dismiss();
                                            AppManager.getInstance().finishActivity(CompInfoActivity.this);
                                        }
                                    });
                                    dialog.show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    //性别选择
    private void setSexChoose() {
        if (compinfoSexMan.isChecked()) {
            sexChoose = "先生";
        } else if (compinfoSexWoman.isChecked()) {
            sexChoose = "女士";
        } else if (compinfoSexNo.isChecked()) {
            sexChoose = "保密";
        }
    }

    //性别默认显示匹配
    private void setSex(String s) {
        if ("先生".equals(s)) {
            compinfoSexMan.setChecked(true);
        } else if ("女士".equals(s)) {
            compinfoSexWoman.setChecked(true);
        } else if ("保密".equals(s)) {
            compinfoSexNo.setChecked(true);
        }
    }

    //城市选择
    private void setCityChoose() {
        compinfoCitypic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util.closeKeyBoard(CompInfoActivity.this);
                CityPicker cityPicker = new CityPicker.Builder(CompInfoActivity.this)
                        .textSize(18)
                        .title("地址选择")
                        .backgroundPop(0xa087ceeb)
                        .titleBackgroundColor("#87ceeb")
                        .titleTextColor("#ffffff")
                        .backgroundPop(0xa0000000)
                        .confirTextColor("#ffffff")
                        .cancelTextColor("#ffffff")
                        .province("北京市")
                        .city("北京市")
                        .district("昌平区")
                        .textColor(Color.parseColor("#808080"))
                        .provinceCyclic(true)
                        .cityCyclic(false)
                        .districtCyclic(false)
                        .visibleItemsCount(7)
                        .itemPadding(10)
                        .onlyShowProvinceAndCity(false)
                        .build();
                cityPicker.show();
                //监听方法，获取选择结果
                cityPicker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
                    @Override
                    public void onSelected(String... citySelected) {
                        String province = citySelected[0];//省
                        String city = citySelected[1];//市
                        String district = citySelected[2];//区县
                        compinfoCitypic.setText(province + "-" + city + "-" + district);
                        String code = citySelected[3];//邮编
                    }

                    @Override
                    public void onCancel() {
                        LogUtil.d(TAG, "onCancel: 取消城市选择");
                    }
                });
            }
        });
    }

    //时间选择
    private void setTimeChoose() {
        final Calendar calendar = Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH) + 1;
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        compinfoXingzuo.setText("星座：" + getConstellation(m, d));//预先设置星座
        compinfoTimepic.setText(y + "-" + m + "-" + d);
        compinfoTimepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatePicker.Builder builder = new DatePicker.Builder(CompInfoActivity.this, 0);
                builder.setPositiveButton(new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String birth = builder.getStr();
                        dialog.dismiss();
                        String[] date = birth.split("-");
                        int age = calendar.get(Calendar.YEAR) - Integer.parseInt(date[0]);
                        compinfoAge.setText("年龄：" + age);
                        compinfoXingzuo.setText("星座：" + getConstellation(Integer.parseInt(date[1]), Integer.parseInt(date[2])));
                        compinfoTimepic.setText(builder.getStr());
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
                WindowManager m = CompInfoActivity.this.getWindowManager();
                Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
                WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
                p.width = (int) (d.getWidth() * 0.75); // 宽度设置为屏幕的0.65
                dialogWindow.setAttributes(p);
                dialog.show();
            }
        });
    }

    //血型选择列表
    private void setBloodChoose(User user) {
        String[] spin_arry = getResources().getStringArray(R.array.blood_type);
        ArrayAdapter bloodadapter = new ArrayAdapter(CompInfoActivity.this, R.layout.item_spinner_blood, spin_arry);
        compinfoBloodtype.setAdapter(bloodadapter);
        compinfoBloodtype.setSelection(matchBlood(user.getBlood()), true);
        compinfoBloodtype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                bloodChoose = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private int matchBlood(String s) {
        if (s.equals("O型"))
            return 0;
        else if (s.equals("AB型"))
            return 1;
        else if (s.equals("A型"))
            return 2;
        else if (s.equals("B型"))
            return 3;
        else if (s.equals("其他"))
            return 4;
        return 0;
    }

    //选择拍照弹出窗口监听器
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            mpopupWindow.dismiss();
            switch (v.getId()) {
                case R.id.camera_take://打开相机拍照
                    headImagePath = Util.checkCameraPermission(CompInfoActivity.this);
                    break;
                case R.id.camera_choose://从相册选择
                    Util.startPicChoose(CompInfoActivity.this);
                    break;
                default:
                    break;
            }
        }
    };

    //处理权限请求
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Util.CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "权限禁止，无法操作", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    headImagePath = Util.startCamera(CompInfoActivity.this);
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    AppManager.getInstance().finishActivity(this);
                }
        }
    }

    //处理活动回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case Util.CAMERA_REQUEST_CODE:
                    Glide.with(CompInfoActivity.this).load(headImagePath).into(compinfoHeadimage);
                    break;
                case Util.ALBUM_REQUEST_CODE:
                    try {
                        Uri uri = data.getData();
                        final String absolutePath = Util.getAbsolutePath(CompInfoActivity.this, uri);
                        headImagePath = absolutePath;
                        Glide.with(CompInfoActivity.this).load(absolutePath).into(compinfoHeadimage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    //计算星座
    private final static int[] dayArr = new int[]{20, 19, 21, 20, 21, 22, 23, 23, 23, 24, 23, 22};
    private final static String[] constellationArr = new String[]{"摩羯座", "水瓶座", "双鱼座", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座"};

    public static String getConstellation(int month, int day) {
        return day < dayArr[month - 1] ? constellationArr[month - 1] : constellationArr[month];
    }

    //初始化视图
    private void initView() {
        ProjectUtil.setToolBar(this, "完善信息", R.drawable.part_back, backClick);
        compinfoHeadimage = (CircleImageView) findViewById(R.id.compinfo_headimage);
        compinfoUsername = (EditText) findViewById(R.id.compinfo_username);
        compinfoSexMan = (RadioButton) findViewById(R.id.compinfo_sex_man);
        compinfoSexWoman = (RadioButton) findViewById(R.id.compinfo_sex_woman);
        compinfoSexNo = (RadioButton) findViewById(R.id.compinfo_sex_no);
        compinfoCitypic = (TextView) findViewById(R.id.compinfo_citypic);
        compinfoTimepic = (TextView) findViewById(R.id.compinfo_timepic);
        compinfoAge = (TextView) findViewById(R.id.compinfo_age);
        compinfoXingzuo = (TextView) findViewById(R.id.compinfo_xingzuo);
        compinfoBloodtype = (AppCompatSpinner) findViewById(R.id.compinfo_bloodtype);
        compinfoWorkinfo = (EditText) findViewById(R.id.compinfo_workinfo);
        compinfoHobby = (EditText) findViewById(R.id.compinfo_hobby);
        compinfoSign = (EditText) findViewById(R.id.compinfo_sign);
        compinfoTbname = (EditText) findViewById(R.id.compinfo_tbname);
        compinfoConfirmbtn = (Button) findViewById(R.id.compinfo_confirmbtn);
    }

    //处理点击
    View.OnClickListener backClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.toolbar_image) {
                AppManager.getInstance().finishActivity(CompInfoActivity.this);
            }
        }
    };
}
