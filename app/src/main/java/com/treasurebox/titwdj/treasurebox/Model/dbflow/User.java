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
public class User extends BaseModel{
    /**
     * uid : 20
     * number : 3678164525
     * username : 你好啊
     * password : 123456789
     * phone : 18103410307
     * place : 西藏自治区-阿里地区-其他
     * constellation : 星座：射手座
     * blood : A型
     * signature : 你好啊
     * birthday : 2012-12-01
     * ufacing : C:\Users\MrDu\Desktop\tit.png
     * hobby : 打豆豆
     * job : 学生
     * gender : 女士
     * personalPassword :
     * fingerprint :
     * age : 5
     */
    @PrimaryKey
    private int uid;
    @Column
    private String number;
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private String phone;
    @Column
    private String place;
    @Column
    private String constellation;
    @Column
    private String blood;
    @Column
    private String signature;
    @Column
    private String birthday;
    @Column
    private String ufacing;
    @Column
    private String hobby;
    @Column
    private String job;
    @Column
    private String gender;
    @Column
    private String personalPassword;
    @Column
    private String fingerprint;
    @Column
    private int age;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getUfacing() {
        return ufacing;
    }

    public String getUfacing2() {
        File file = new File(Util.getImageDownloadPath() + ufacing);
        File file1 = new File(ufacing);
        if (file.exists()) {
            LogUtil.d("UserModel", "头像已在本地：" + ufacing);
            return Util.getImageDownloadPath() + ufacing;
        } else if (file1.exists()) {
            LogUtil.d("UserModel", "头像已在本地：" + ufacing);
            return ufacing;
        } else {
            LogUtil.d("UserModel", "头像不在本地：" + ufacing);
            Util.downloadImage(new BaseActivity(), HttpPathUtil.getImagePre() +  ufacing, ufacing, Util.headPath);
            return HttpPathUtil.getImagePre() +  ufacing;
        }
    }

    public String getNoPreUfacing() {
        return ufacing;
    }

    public void setUfacing(String ufacing) {
        this.ufacing = ufacing;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPersonalPassword() {
        return personalPassword;
    }

    public void setPersonalPassword(String personalPassword) {
        this.personalPassword = personalPassword;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
