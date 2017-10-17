package com.treasurebox.titwdj.treasurebox.Model.dbflow;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Created by 11393 on 2017/9/17.
 */
@Table(database = TBdb.class)
public class Message extends BaseModel{
    /**
     * wid : 4
     * wcintent : 明那年dyqhhrww
     * wtime : 2017-10-01
     * wto : 啦啦
     * wfrom : 20
     * wphone : 18103410307
     * status : 1
     */
    @PrimaryKey
    private int wid;
    @Column
    private String wcintent;
    @Column
    private String wtime;
    @Column
    private String wto;
    @Column
    private int wfrom;
    @Column
    private String wphone;
    @Column
    private int status;//0--未提醒，1--触发提醒，2--提醒结束

    public int getWid() {
        return wid;
    }

    public void setWid(int wid) {
        this.wid = wid;
    }

    public String getWcintent() {
        return wcintent;
    }

    public void setWcintent(String wcintent) {
        this.wcintent = wcintent;
    }

    public String getWtime() {
        return wtime;
    }

    public void setWtime(String wtime) {
        this.wtime = wtime;
    }

    public String getWto() {
        return wto;
    }

    public void setWto(String wto) {
        this.wto = wto;
    }

    public int getWfrom() {
        return wfrom;
    }

    public void setWfrom(int wfrom) {
        this.wfrom = wfrom;
    }

    public String getWphone() {
        return wphone;
    }

    public void setWphone(String wphone) {
        this.wphone = wphone;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
