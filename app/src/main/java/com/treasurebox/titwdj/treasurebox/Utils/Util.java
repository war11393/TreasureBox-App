package com.treasurebox.titwdj.treasurebox.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 11393 on 2017/8/5.
 * 为自己创建的工具类
 */
public class Util {
    private static final String TAG = "Util";
    //主页标签常量
    public static final String HOME_TAG = "home";
    public static final String NOTE_TAG = "Note";
    public static final String MINE_TAG = "user";

    //相机常量
    public final static int ALBUM_REQUEST_CODE = 100;
    public final static int CAMERA_REQUEST_CODE = 101;

    //格式化时间数据
    public static String DateFormat(Date date){
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    //尺寸-dp转px
    public static int dip2px(Context ctx,float dpValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    //尺寸-px转dp
    public static int px2dip(Context ctx, float pxValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    //一个小定时器
    public static void sleep(final int duration){
        new Thread(new Runnable() {
            @Override
            public void run() {
                sleep(duration);
            }
        }).start();
    }

    //添加碎片,管理器--碎片实例--是否加入回退栈--容器id
    public static void addFragment(FragmentManager fragmentManager, Fragment fragment, boolean addToBackStack, int containerId) {
        String backStackName = fragment.getClass().getName();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStackName, 0);
        if (!fragmentPopped) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(containerId, fragment, backStackName)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (addToBackStack)
                transaction.addToBackStack(backStackName);
            transaction.commit();
        }
    }

    //通过反射获取状态栏高度，默认25dp
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = dip2px(context, 25);
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusBarHeight;
    }

    //如果键盘打开着就关掉
    public static void closeKeyBoard(Activity activity) {
        InputMethodManager imm =  (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null) {
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    //解析字符串是否为json字串
    public static class JsonUtils {
        public static boolean isBadJson(String json) {
            return !isGoodJson(json);
        }
        public static boolean isGoodJson(String json) {
            if (StringUtils.isBlank(json)) {
                return false;
            }
            try {
                new JsonParser().parse(json);
                return true;
            } catch (JsonParseException e) {
                LogUtil.d(TAG, "接收到了非json字串：" + json);
                return false;
            }
        }
    }

    //启动系统相机
    public static String startCamera(Activity activity) {
        // 指定相机拍摄照片保存地址
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent();
            // 指定开启系统相机的Action
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            File outDir = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!outDir.exists()) {
                outDir.mkdirs();
            }
            File outFile = new File(outDir, System.currentTimeMillis() + ".jpg");
            // 把文件地址转换成Uri格式
            Uri uri = Uri.fromFile(outFile);
            // 设置系统相机拍摄照片完成后图片文件的存放地址
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            // 此值在最低质量最小文件尺寸时是0，在最高质量最大文件尺寸时是１
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            activity.startActivityForResult(intent, CAMERA_REQUEST_CODE);
            LogUtil.d(TAG, "此次相片绝对路径：" + outFile.getAbsolutePath());
            return outFile.getAbsolutePath();//返回其绝对路径
        } else {
            Toast.makeText(activity, "请确认已经插入SD卡",
                    Toast.LENGTH_LONG).show();
            return null;
        }
    }

    //打开图片选择
    public static void startPicChoose(Activity activity){
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(intent, ALBUM_REQUEST_CODE);
    }

    //获取绝对路径
    public static String getAbsolutePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        LogUtil.d(TAG, "此URI绝对路径：" + data);
        return data;
    }

    //监测并请求相机权限,已获取则启动相机
    public static String checkCameraPermission(Activity activity) {
        List<String> permissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissions.isEmpty()) {
            String strings[] = new String[permissions.size()];
            for (int i = 0; i < permissions.size(); i++) {
                strings[i] = permissions.get(i);
            }
            ActivityCompat.requestPermissions(activity, strings, CAMERA_REQUEST_CODE);
            return null;
        } else {
            return startCamera(activity);
        }
    }

    //使用Glide保存图片到手机
    public static final String picturePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
    public static final String headPath = "TBHead";
    public static void downloadImage(final Activity activity, final String url, final String fName, final String path) {
        new AsyncTask<Void, Integer, File>() {
            @Override
            protected File doInBackground(Void... params) {
                File file = null;
                try {
                    FutureTarget<File> future = Glide
                            .with(MyApplication.getApplication())
                            .load(url)
                            .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
                    file = future.get();

                    // 首先保存图片
                    File pictureFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsoluteFile();
                    File appDir = new File(pictureFolder, path);
                    if (!appDir.exists()) {//如果文件夹不存在就创建一个
                        appDir.mkdirs();
                    }
                    String fileName = fName;
                    File destFile = new File(appDir, fileName);
                    FileUtils.copyFile(file, destFile);

                    // 最后通知图库更新
                    activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.fromFile(new File(destFile.getPath()))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return file;
            }
            @Override
            protected void onPostExecute(File file) {
                LogUtil.d(TAG, "存在了：" + picturePath + "/" + path + "/");
            }
            @Override
            protected void onProgressUpdate(Integer... values) {super.onProgressUpdate(values);}
        }.execute();
    }
    public static String getImageDownloadPath(){
        return picturePath + "/" + headPath + "/";
    }
}
