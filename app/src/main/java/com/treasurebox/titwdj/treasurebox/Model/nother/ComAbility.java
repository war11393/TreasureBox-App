package com.treasurebox.titwdj.treasurebox.Model.nother;

/**
 * Created by 11393 on 2017/10/6.
 */

public class ComAbility {

    /**
     * moodValue : -5.81    情感值
     * stability : 0.31     情感稳定性
     * interaction : 0      被关注度
     * rational : 0         理性程度
     * secrate : 0          权限开放度
     *
     * happy : 0.16         开心
     * veryhappy : 0.47     高兴
     * sad : 0.05           难过
     * scard : 0.11         伤心
     * commen : 0.21        一般
     */
    private double moodValue;
    private double stability;
    private double interaction;
    private double rational;
    private double secrate;
    private double happy;
    private double veryhappy;
    private double sad;
    private double scard;
    private double commen;

    public ComAbility(double moodValue, double stability, double interaction, double rational, double secrate, double happy, double veryhappy, double sad, double scard, double commen) {
        this.moodValue = moodValue;
        this.stability = stability;
        this.interaction = interaction;
        this.rational = rational;
        this.secrate = secrate;
        this.happy = happy;
        this.veryhappy = veryhappy;
        this.sad = sad;
        this.scard = scard;
        this.commen = commen;
    }

    public String[] getAbility1Name(){
        return new String[]{"情感值", "情感稳定性", "被关注度", "理性值", "开放度"};
    }

    public String[] getAbility2Name(){
        return new String[]{"开心", "高兴", "难过", "伤心", "一般"};
    }

    public int[] getAbility1Value(){
        return new int[]{doubleToInt(moodValue), doubleToInt(stability), doubleToInt(interaction), doubleToInt(rational), doubleToInt(secrate)};
    }

    public int[] getAbility2Value(){
        return new int[]{doubleToInt(happy), doubleToInt(veryhappy), doubleToInt(sad), doubleToInt(scard), doubleToInt(commen)};
    }

    public static int doubleToInt(double value) {
        if (value < 0.5) {
            return 2 - round(value*100*2/100);
        } else {
            return 2 + round(value*100*2/100);
        }
    }
    public static int round(double value) {
        return (int) (value + 0.5);
    }

    public void setMoodValue(double moodValue) {
        this.moodValue = moodValue;
    }
    public void setStability(double stability) {
        this.stability = stability;
    }
    public void setInteraction(double interaction) {
        this.interaction = interaction;
    }
    public void setRational(double rational) {
        this.rational = rational;
    }
    public void setSecrate(double secrate) {
        this.secrate = secrate;
    }
    public void setHappy(double happy) {
        this.happy = happy;
    }
    public void setVeryhappy(double veryhappy) {
        this.veryhappy = veryhappy;
    }
    public void setSad(double sad) {
        this.sad = sad;
    }
    public void setScard(double scard) {
        this.scard = scard;
    }
    public void setCommen(double commen) {
        this.commen = commen;
    }
}
