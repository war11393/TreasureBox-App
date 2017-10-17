package com.treasurebox.titwdj.treasurebox.Model.nother;

import java.util.List;

/**
 * Created by 11393 on 2017/10/9.
 */

public class DriftNote {
    /**
     * driftId : 7
     * title : 前台没提供这个梗，以后再说
     * driftContent : 。。。。。。
     * sendId : 33
     * identifier : 1
     * driftTime : 2017-10-09 19:37:42
     * checkTheQuantity : 6
     * hate : 0
     * drift_evaluateList : [{"drif_evaluateId":1,"drifCommentId":36,"drifCommentTime":"2017-10-10 17:28:22","drifIfObv":0,"drifContent":"。。。","driftId":7,"userName":"忘川呐"}]
     * userNumber : 9418912858
     * userName : 忘川呐
     */

    private int driftId;
    private String title;
    private String driftContent;
    private int sendId;
    private int identifier;
    private String driftTime;
    private int checkTheQuantity;
    private int hate;
    private String userNumber;
    private String userName;
    private List<DriftEvaluateListBean> drift_evaluateList;

    public int getDriftId() {
        return driftId;
    }

    public void setDriftId(int driftId) {
        this.driftId = driftId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDriftContent() {
        return driftContent;
    }

    public void setDriftContent(String driftContent) {
        this.driftContent = driftContent;
    }

    public int getSendId() {
        return sendId;
    }

    public void setSendId(int sendId) {
        this.sendId = sendId;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getDriftTime() {
        return driftTime;
    }

    public void setDriftTime(String driftTime) {
        this.driftTime = driftTime;
    }

    public int getCheckTheQuantity() {
        return checkTheQuantity;
    }

    public void setCheckTheQuantity(int checkTheQuantity) {
        this.checkTheQuantity = checkTheQuantity;
    }

    public int getHate() {
        return hate;
    }

    public void setHate(int hate) {
        this.hate = hate;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public void setUserNumber(String userNumber) {
        this.userNumber = userNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<DriftEvaluateListBean> getDrift_evaluateList() {
        return drift_evaluateList;
    }

    public void setDrift_evaluateList(List<DriftEvaluateListBean> drift_evaluateList) {
        this.drift_evaluateList = drift_evaluateList;
    }

    public static class DriftEvaluateListBean {
        /**
         * drif_evaluateId : 14
         * drifCommentId : 36
         * drifCommentTime : 2017-10-12 19:22:10
         * drifIfObv : 0
         * drifContent : 评论N
         * driftId : 6
         * userName : 忘川呐
         */

        private int drif_evaluateId;
        private int drifCommentId;
        private String drifCommentTime;
        private int drifIfObv;
        private String drifContent;
        private int driftId;
        private String userName;

        public int getDrif_evaluateId() {
            return drif_evaluateId;
        }

        public void setDrif_evaluateId(int drif_evaluateId) {
            this.drif_evaluateId = drif_evaluateId;
        }

        public int getDrifCommentId() {
            return drifCommentId;
        }

        public void setDrifCommentId(int drifCommentId) {
            this.drifCommentId = drifCommentId;
        }

        public String getDrifCommentTime() {
            return drifCommentTime;
        }

        public void setDrifCommentTime(String drifCommentTime) {
            this.drifCommentTime = drifCommentTime;
        }

        public int getDrifIfObv() {
            return drifIfObv;
        }

        public void setDrifIfObv(int drifIfObv) {
            this.drifIfObv = drifIfObv;
        }

        public String getDrifContent() {
            return drifContent;
        }

        public void setDrifContent(String drifContent) {
            this.drifContent = drifContent;
        }

        public int getDriftId() {
            return driftId;
        }

        public void setDriftId(int driftId) {
            this.driftId = driftId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
