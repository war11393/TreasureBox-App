package com.treasurebox.titwdj.treasurebox.Model.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by 11393 on 2017/9/24.
 */
@Table(database = TBdb.class)
public class MemoList extends BaseModel {
    /**
     * memoId : -1
     * uid : 0
     * fid : 0
     * memoName : 账号
     * friendContent : 6942171882
     */
    @Column
    @PrimaryKey
    private int memoId;
    @Column
    @PrimaryKey
    private int uid;
    @Column
    @PrimaryKey
    private int fid;
    @Column
    @PrimaryKey
    private String memoName;
    @Column
    private String friendContent;

    public int getMemoId() {
        return memoId;
    }

    public void setMemoId(int memoId) {
        this.memoId = memoId;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getFid() {
        return fid;
    }

    public void setFid(int fid) {
        this.fid = fid;
    }

    public String getMemoName() {
        return memoName;
    }

    public void setMemoName(String memoName) {
        this.memoName = memoName;
    }

    public String getFriendContent() {
        return friendContent;
    }

    public void setFriendContent(String friendContent) {
        this.friendContent = friendContent;
    }
}
