package com.treasurebox.titwdj.treasurebox.Model.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.treasurebox.titwdj.treasurebox.Activity.BaseActivity;
import com.treasurebox.titwdj.treasurebox.Activity.FriendActivity;
import com.treasurebox.titwdj.treasurebox.Utils.AppManager;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Custom.MyApplication;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;

import java.io.File;
import java.util.List;

/**
 * Created by 11393 on 2017/9/11.
 * 用户好友信息
 */
@Table(database = TBdb.class)
public class FriendInfo extends BaseModel {

    /**
     * proId : 206942171882
     * head : tit.png
     * phone : 1234567888
     * friendUsername : 啦啦啦
     * memoList : [{"memoId":-1,"uid":0,"fid":0,"memoName":"账号","friendContent":"6942171882"},{"memoId":12,"uid":20,"fid":31,"memoName":"","friendContent":""}]
     */
    @Column
    @PrimaryKey
    String proId;
    @Column
    String head;
    @Column
    String phone;
    @Column
    String friendUsername;

    List<MemoList> memoList;

    public String getProId() {
        return proId;
    }

    public void setProId(String proId) {
        this.proId = proId;
    }

    public String getHead() {
        return head;
    }

    public String getHead2() {
        File file = new File(Util.getImageDownloadPath() + head);
        File file1 = new File(head);
        if (file.exists()) {
            LogUtil.d("UserModel", friendUsername + " 头像已在本地：" + head);
            return Util.getImageDownloadPath() + head;
        } else if (file1.exists()) {
            LogUtil.d("UserModel", friendUsername + " 头像已在本地：" + head);
            return head;
        } else {
            LogUtil.d("UserModel", friendUsername + " 头像不在本地：" + head);
            Util.downloadImage(new BaseActivity(), HttpPathUtil.getImagePre() +  head, head, Util.headPath);
            return HttpPathUtil.getImagePre() +  head;
        }
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFriendUsername() {
        return friendUsername;
    }

    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    public List<MemoList> getMemoList() {
        return SQLite.select().from(MemoList.class)
                .where(MemoList_Table.uid.eq(MyApplication.user.getUid()))
                .and(MemoList_Table.fid.eq(Integer.parseInt(FriendActivity.fid)))
                .queryList();
    }

    public void setMemoList(List<MemoList> memoList) {
        this.memoList = memoList;
    }

    @Override
    public boolean save() {
        if (memoList != null || !memoList.isEmpty()){
            for (MemoList memo:this.memoList) {
                memo.save();
            }
        }
        return super.save();
    }
}
