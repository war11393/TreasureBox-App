package com.treasurebox.titwdj.treasurebox.Model.nother;

import com.treasurebox.titwdj.treasurebox.Activity.BaseActivity;
import com.treasurebox.titwdj.treasurebox.R;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;
import com.treasurebox.titwdj.treasurebox.Utils.LogUtil;
import com.treasurebox.titwdj.treasurebox.Utils.Util;

import java.io.File;

/**
 * Created by 11393 on 2017/10/18.
 */

public class HotContent {

    /**
     * topicId : 10
     * topicContent : ？。。。。。
     * time : 2017-10-18 22:16:21
     * user : {"uid":36,"number":"9418912858","username":"忘川呐","password":"wzdasnl6","phone":"18103410307","place":"山西省-太原市-尖草坪区","constellation":"处女座","blood":"O型","signature":"我真的穷的只剩帅","birthday":"2017-9-21","ufacing":"9418912858_-1_0.png","hobby":"打豆豆","job":"学生","gender":"先生","personalPassword":"","fingerprint":"","age":21}
     * goodNum : 0
     * opinionNumber : 0
     * title : 改变人生轨迹的一次经历
     */

    private int topicId;
    private String topicContent;
    private String time;
    private UserBean user;
    private int goodNum;
    private int opinionNumber;
    private String title;

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public String getTopicContent() {
        return topicContent;
    }

    public void setTopicContent(String topicContent) {
        this.topicContent = topicContent;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public int getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(int goodNum) {
        this.goodNum = goodNum;
    }

    public int getOpinionNumber() {
        return opinionNumber;
    }

    public void setOpinionNumber(int opinionNumber) {
        this.opinionNumber = opinionNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static class UserBean {
        /**
         * uid : 36
         * number : 9418912858
         * username : 忘川呐
         * password : wzdasnl6
         * phone : 18103410307
         * place : 山西省-太原市-尖草坪区
         * constellation : 处女座
         * blood : O型
         * signature : 我真的穷的只剩帅
         * birthday : 2017-9-21
         * ufacing : 9418912858_-1_0.png
         * hobby : 打豆豆
         * job : 学生
         * gender : 先生
         * personalPassword :
         * fingerprint :
         * age : 21
         */

        private int uid;
        private String number;
        private String username;
        private String phone;
        private String place;
        private String constellation;
        private String blood;
        private String signature;
        private String birthday;
        private String ufacing;
        private String hobby;
        private String job;
        private String gender;
        private String personalPassword;
        private String fingerprint;
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
            if (username == null) {
                return username;
            } else {
                return "某某用户";
            }
        }

        public void setUsername(String username) {
            this.username = username;
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
            if (ufacing != null) {
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
            } else
                return R.drawable.part_defaultimage + "";
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
}
