package com.treasurebox.titwdj.treasurebox.Model.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.treasurebox.titwdj.treasurebox.Activity.BaseActivity;
import com.treasurebox.titwdj.treasurebox.Utils.AppManager;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;

import java.io.File;

@Table(database = TBdb.class)
public class FriendList extends BaseModel {

    /**
     * fid : 4
     * friendNumber : 6813990908
     * friendUsername : 撒比杜权
     * cid : 1
     * friendTime : 2017-09-11 19:52:28
     * friendNickname : 你
     * facing : tit.png
     * uid : 20
     * recoverFriend : 0
     */
    @PrimaryKey
    private int fid;
    @Column
    private String friendNumber;
    @Column
    private String friendUsername;
    @Column
    private int cid;
    @Column
    private String friendTime;
    @Column
    private String friendNickname;
    @Column
    private String facing;
    @Column
    private int uid;
    @Column
    private int recoverFriend;

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public String getFriendNumber() {
        return friendNumber;
    }

    public void setFriendNumber(String friendNumber) {
        this.friendNumber = friendNumber;
    }

    public String getFriendUsername() {
        return friendUsername;
    }

    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getFriendTime() {
        return friendTime;
    }

    public void setFriendTime(String friendTime) {
        this.friendTime = friendTime;
    }

    public String getFriendNickname() {
        return friendNickname;
    }

    public void setFriendNickname(String friendNickname) {
        this.friendNickname = friendNickname;
    }

    public String getFacing() {
        return facing;
    }

    public String getFacing2() {
        File file = new File(Util.getImageDownloadPath() + facing);
        File file1 = new File(facing);
        if (file.exists()) {
            LogUtil.d("UserModel", friendUsername + " 头像已在本地：" + facing);
            return Util.getImageDownloadPath() + facing;
        } else if (file1.exists()) {
            LogUtil.d("UserModel", friendUsername + " 头像已在本地：" + facing);
            return facing;
        } else {
            LogUtil.d("UserModel", friendUsername + " 头像不在本地：" + facing);
            Util.downloadImage(new BaseActivity(), HttpPathUtil.getImagePre() +  facing, facing, Util.headPath);
            return HttpPathUtil.getImagePre() +  facing;
        }
    }

    public void setFacing(String facing) {
        this.facing = facing;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getRecoverFriend() {
        return recoverFriend;
    }

    public void setRecoverFriend(int recoverFriend) {
        this.recoverFriend = recoverFriend;
    }
}
