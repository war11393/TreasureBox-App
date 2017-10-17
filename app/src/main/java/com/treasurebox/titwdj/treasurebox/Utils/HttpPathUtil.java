package com.treasurebox.titwdj.treasurebox.Utils;

public class HttpPathUtil {
//    private static final String pre = "http://192.168.1.196:8080/tb/";//杜权
//    private static final String pre = "http://192.168.1.231:8888/tb/";//浩然
    private static final String pre = "http://123.207.180.232:8080/tb/";//云

    //获取网络图片路径前缀
    public static String getImagePre() {
//        return "http://192.168.1.196:8080/image/";//杜权
//        return "http://192.168.1.231:8888/image/";//浩然
        return "http://123.207.180.232:8080/image/";//云
    }

    /**
     * 登陆
     * 提供的参数:number(登陆账号),password(登陆密码)，channelId（每次登陆的时候进行一次更新，防止变化）
     * @return
     */
    public static String login() {
        return pre + "user/login";
    }

    /**
     * 用户注册模块
     * 提供的参数：password（密码），repassword（重复密码），phone（手机号）channelId（用户绑定时生成的)
     * @return
     */
    public static String addUser() {
        return pre + "user/addUser";
    }

    /**
     * 修改角色信息
     * 提供的参数：uid，username（角色名），place，constellation（星座），blood（血型）
     * signature（个性签名），birthday（生日），hobby（爱好），job（工作），gender（性别），personalPassword（私人密码）
     * age（年龄），ufacing（头像）
     * @return
     */
    public static String updateRoleData() {
        return pre + "user/updateRoleData";
    }

    /**
     * 修改密码
     * 提供的参数：uid,password
     * @return
     */
    public static String updatePasswprd() {
        return pre + "user/updatePasswprd";
    }

    /**
     * 添加好友之前的模糊查询所有符合条件的用户
     * selectName
     * @return
     */
    public static String vagueSelectFriend() {
        return pre + "friend/vagueSelectFriend";
    }

    /**
     * 添加好友
     * 提供的参数：number（好友的账号），uid（用户的uid），friendUsername（给好友的备注），cid：1--亲人，2--爱人，3--长辈，4--同事/学，5--朋友
     * @return
     */
    public static String addFriend() {
        return pre + "friend/addFriend";
    }

    /**
     * 删除好友
     * 提供的参数：fid
     * @return
     */
    public static String deleteFriend() {
        return pre + "friend/deleteFriend";
    }

    /**
     * 查询全部好友
     * 提供的参数：uid
     * @return
     */
    public static String selectAllFriends() {
        return pre + "friend/selectAllFriends";
    }

    /**
     * 查看好友详细信息
     * 提供的参数：uid（用户的uid），friendNumber（好友的账号），fid（好友的fid）
     * @return
     */
    public static String selectFriendData() {
        return pre + "friend/selectFriendData";
    }

    /**
     * 修改好友备注
     * 提供的参数：fid（好友的fid），friendUsername（好友的备注）
     * @return
     */
    public static String updateFriendName() {
        return pre + "friend/updateFriendName";
    }

    /**
     * 删除便签
     * 提供的参数：memoId
     * @return
     */
    public static String deleteMemo() {
        return pre + "friend/deleteMemo";
    }

    /**
     * 添加好友便签
     * 提供的参数：uid，fid，memoName（便签标题），friendContent（便签内容）
     * @return
     */
    public static String addMemo() {
        return pre + "friend/addMemo";
    }

    /**
     * 发布纸条
     * 所需参数：uid（发布纸条人的uid），mood（纸条设置的心情颜色id），noteAdout（纸条关于的人的userNumber），noteContent（纸条的内容）
     * friendNumberList（设置权限勾选的亲友的friendNumber），obvious（设置权限的方式0：设置可见用户，其他用户不可见；1：设置不可见用户，其他用户可见）
     * ufacing(图片)
     */
    public static String addNote() {
        return pre + "note/addNote";
    }

    /**
     * 分页显示用户的所有字条
     * 所需参数：noteId（此为分页的最后一条字条的id，第一次为0），uid（要显示字条用户的uid），
     * @return
     */
    public static String showMyAllNote() {
        return pre + "note/showMyAllNote";
    }

    /**
     * 显示所有亲友的自己有权限看的字条
     * 所需参数：uid（当前用户uid），myuserNunber（当前用户userNumber），noteId（此为分页的最后一条字条的id，第一次为0）
     * @return
     */
    public static String showAllfriNote() {
        return pre + "note/showAllfriNote";
    }

    /**
     * 发表评论
     * 所需参数：noteId（评回的纸条的id），commentId（评论人id），ifObv（是否匿名0是,1否），econtent（评论内容）
     * @return
     */
    public static String pushcomment() {
        return pre + "evaluate/pushcomment";
    }

    /**
     * 发表回复
     * 所需参数：noteId（评回的纸条id），commentId（被回复的人的id），replyId（回复的人的id），ifObv（是否匿名0是,1否），econtent（回复内容）
     * eflag（被回复的评回的标志位 1：评价纸条 2：回复评价 3：回复回复）,replyEid(回复的纸条的id)
     * @return
     */
    public static String pushReply() {
        return pre + "evaluate/pushReply";
    }

    /**
     * 添加提醒记录
     * 提供的参数:wcintent(提醒内容)，wtime（提醒时间），wto（如果是好友列表中的为好友账号，如果不是则为空），wfrom（用户id），wphone（当wto为空的时候需要把被提醒人的手机号记录下来）
     * @return "提醒设置成功"
     */
    public static String setWarn() {
        return pre + "warn/addWarn";
    }

    /**
     * 前台查询，提醒触发的记录
     * 提供的参数：uid
     * @return warnList
     */
    public static String selWarnByPre() {
        return pre + "warn/selWarnByPre";
    }

    /**
     * 查询能力值
     * 提供的参数：userNumber
     * @return
     */
    public static String selectValue(){
        return pre+"analyse/selectValue";
    }

    /**
     * 查询详细能力值，附加建议
     * 所需参数 userNumber：用户账号
     * @return
     */
    public static String selectAllmoodValue(){
        return pre+"analyse/selectAllmoodValue";
    }
    /**
     * 所需参数：number：用户账号，查询建议
     * @return
     */
    public static String getMoodAdvice(){
        return pre+"analyse/getMoodAdvice";
    }

    /**
     * 查询相关提醒
     * 提供的参数：number
     * @return
     */
    public static String getSuggest(){
        return pre+"analyse/getAdvice";
    }

    /**
     * 添加反馈
     * 所需参数：uid(用户的id)，content(反馈的内容)
     * @return
     */
    public static String addFeedBack(){
        return pre+"feedBack/addFeedBack";
    }

    /**
     * 添加漂流瓶
     * 所需的参数：uid（用户的uid），title（漂流瓶标题），driftContent（漂流记录的内容），identifier（样式编号：数字）
     * @return
     */
    public static String addDrift_note() {
        return pre + "driftBottle/addDrift_note";
    }

    /**
     * 随机抽取一条漂流瓶和其所有评论且不是此用户的
     * 所需参数：uid
     * @return
     */
    public static String randomSelectDrift_note() {
        return pre + "driftBottle/randomSelectDrift_note";
    }

    /**
     * 扔回大海
     * 所需的参数：driftId（漂流瓶的id）
     * @return
     */
    public static String atSea() {
        return pre + "driftBottle/atSea";
    }

    /**
     * 厌恶此漂流瓶
     * 所需参数：driftId
     * 注意：一旦点击了厌恶，此漂流瓶将永久不被拾起
     * @return
     */
    public static String hate() {
        return pre + "driftBottle/hate";
    }

    /**
     * 添加漂流瓶评论
     * 所需的参数：driftId（漂流瓶的id），drifCommentId（评论人的uid），drifIfObv（是否匿名 数字），drifContent（评论内容）
     * @return
     */
    public static String addDrift_evaluate_discuss() {
        return pre + "driftBottle/addDrift_evaluate_discuss";
    }

    /**
     * 用户查询个人的所有漂流瓶和评论过的漂流瓶
     * 所需参数：uid
     * @return
     */
    public static String selectDriftByUid(){
        return pre+"driftBottle/selectDriftByUid";
    }

    /**
     * 查询漂流瓶的评论
     * 所需参数：driftId
     * @return
     */
    public static String selectDriftEvaluate(){
        return pre+"driftBottle/selectOneDriftEvaluate";
    }
    //---------------------------------------------------------------------------以上为已经对接的接口------------------------------------------------------------------------------//
    //==============================好友便签模块======================================

    /**
     * 修改好友便签--暂时未设置修改便签得功能
     * 提供的参数：memoId（json串），memoName（json串），friendContent（json串）
     *
     * @return
     */
    public static String updateMemo() {
        return pre + "friend/updateMemo";
    }

    //====================纸条模块============================

    /**
     * 点赞
     * 所需参数：noteId（点赞纸条id）
     *
     * @return
     */
    public static String getGoodNum() {
        return pre + "Note/getGoodNum";
    }

    /**
     * 扔鸡蛋
     * 所需参数：noteId（扔鸡蛋纸条id）
     *
     * @return
     */
    public static String getEgg() {
        return pre + "Note/getEgg";
    }

    /**
     * 显示某个亲友的自己有权限看的字条
     * 所需参数：myuserNunber（当前用户的userNumber），friUid（要看的亲友的uid），noteId（此为分页的最后一条字条的id，第一次为0）
     *
     * @return
     */
    public static String showOneFriNote() {
        return pre + "Note/showOneFriNote";
    }

    //=================================好友模块=====================================

    /**
     * 查看所有好友分类
     * 无参数
     *
     * @return
     */
    public static String selectAllCategory() {
        return pre + "category/selectAllCategory";
    }

    /**
     * 查询某一好友模糊查询
     * 提供的参数：uid，selectName（查询的条件）
     *
     * @return
     */
    public static String selectFriend() {
        return pre + "friend/selectFriend";
    }

    /**
     * 查询删除的好友,用来恢复删除得好友，也可以选择重加---这里未使用
     * 提供的参数：uid
     *
     * @return
     */
    public static String selectDeleteFriend() {
        return pre + "friend/selectDeleteFriend";
    }
}