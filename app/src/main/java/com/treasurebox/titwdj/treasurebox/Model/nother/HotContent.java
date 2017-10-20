package com.treasurebox.titwdj.treasurebox.Model.nother;

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
}
