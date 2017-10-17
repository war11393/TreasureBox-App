package com.treasurebox.titwdj.treasurebox.Model.nother;

import com.alibaba.fastjson.JSON;
import com.treasurebox.titwdj.treasurebox.Model.dbflow.TBdb;
import com.treasurebox.titwdj.treasurebox.Utils.HttpPathUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 11393 on 2017/9/14.
 * 纸条类
 */
public class Note {
    /**
     * noteId : 18
     * mood : 3
     * noteAdout : ["6942171882","6813990908"]
     * noteContent : 啦啦啦
     * imageList : ["3678164525_18.jpg","3678164525_18.jpg"]
     * time : 2017-09-14 18:22:05
     * goodNum : 0
     * egg : 0
     * user : {"uid":0,"number":"3678164525","age":0}
     * highOpinion : 0
     * lowOpinion : 0
     * opinionNumber : 0
     * evaluate : [{"eid":2,"noteId":18,"replyId":13,"commentId":20,"ifObv":1,"econtent":"fhdjkhfhdgkjashdgkjhdsjkghdsjkghjksdhgjkds","eflag":2}]
     */
    private int noteId;
    private int mood;
    private String noteContent;
    private String time;
    private int goodNum;
    private int egg;
    private int highOpinion;
    private int lowOpinion;
    private String locate;
    private int opinionNumber;
    private int uid;
    private String number;

    private String noteAdout;
    private List<String> imageList;

    private NoteUserBean user;
    private List<NoteEvaluate> evaluate;

    public static class NoteUserBean{
        /**
         * uid : 0
         * number : 3678164525
         * age : 0
         */

        private int uid;
        private String number;
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

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public static class NoteEvaluate {
        /**
         * eid : 3
         * noteId : 31
         * replyId : 0
         * commentId : 20
         * ifObv : 1
         * commentTime : 2017-09-16 11:22:16
         * econtent : è¿æ¯è¯è®º
         * eflag : 1
         * commentNum : 3678164525
         * replyEid : 0
         */

        private int eid;
        private int noteId;
        private int replyId;
        private int commentId;
        private int ifObv;
        private String commentTime;
        private String econtent;
        private int eflag;
        private String commentNum;
        private int replyEid;

        public int getEid() {
            return eid;
        }

        public void setEid(int eid) {
            this.eid = eid;
        }

        public int getNoteId() {
            return noteId;
        }

        public void setNoteId(int noteId) {
            this.noteId = noteId;
        }

        public int getReplyId() {
            return replyId;
        }

        public void setReplyId(int replyId) {
            this.replyId = replyId;
        }

        public int getCommentId() {
            return commentId;
        }

        public void setCommentId(int commentId) {
            this.commentId = commentId;
        }

        public int getIfObv() {
            return ifObv;
        }

        public void setIfObv(int ifObv) {
            this.ifObv = ifObv;
        }

        public String getCommentTime() {
            return commentTime;
        }

        public void setCommentTime(String commentTime) {
            this.commentTime = commentTime;
        }

        public String getEcontent() {
            return econtent;
        }

        public void setEcontent(String econtent) {
            this.econtent = econtent;
        }

        public int getEflag() {
            return eflag;
        }

        public void setEflag(int eflag) {
            this.eflag = eflag;
        }

        public String getCommentNum() {
            return commentNum;
        }

        public void setCommentNum(String commentNum) {
            this.commentNum = commentNum;
        }

        public int getReplyEid() {
            return replyEid;
        }

        public void setReplyEid(int replyEid) {
            this.replyEid = replyEid;
        }
    }

    public String getLocate() {
        return locate;
    }

    public void setLocate(String locate) {
        this.locate = locate;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public List<String> getNoteAdout() {
        return JSON.parseArray(noteAdout, String.class);
    }

    public void setNoteAdout(String noteAdout) {
        this.noteAdout = noteAdout;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(int goodNum) {
        this.goodNum = goodNum;
    }

    public int getEgg() {
        return egg;
    }

    public void setEgg(int egg) {
        this.egg = egg;
    }

    public NoteUserBean getUser() {
        return user;
    }

    public void setUser(NoteUserBean user) {
        this.user = user;
        this.uid = user.getUid();
        this.number = user.getNumber();
    }

    public int getHighOpinion() {
        return highOpinion;
    }

    public void setHighOpinion(int highOpinion) {
        this.highOpinion = highOpinion;
    }

    public int getLowOpinion() {
        return lowOpinion;
    }

    public void setLowOpinion(int lowOpinion) {
        this.lowOpinion = lowOpinion;
    }

    public int getOpinionNumber() {
        return opinionNumber;
    }

    public void setOpinionNumber(int opinionNumber) {
        this.opinionNumber = opinionNumber;
    }

    public List<String> getImageList() {
        List<String> list = new ArrayList<>();
        for (String str:imageList) {
            list.add(HttpPathUtil.getImagePre() + str);
        }
        return list;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public List<NoteEvaluate> getEvaluate() {
        return evaluate;
    }

    public void setEvaluate(List<NoteEvaluate> evaluate) {
        this.evaluate = evaluate;
    }

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
}
