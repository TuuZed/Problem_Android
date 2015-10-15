package cn.lyh.problem.utils;

import cn.lyh.problem.model.User;

/**
 * Created by LYH on 2015/9/29.
 */
public class ConfigInfo {
   //数据库字符串
    public static class DB{
       public static final String DBNAME = "problem.db";

       public static final String TABUSER = "t_user";
       public static final String UID = "uId";
       public static final String UNAME = "uName";
       public static final String UDATE = "uDate";
       public static final String UEMAIL = "uEmail";
       public static final String UINTRO = "uIntro";
       public static final String USEX = "uSex";
       public static final String UPASSWD = "uPasswd";



       public static final String TABMANUSRCIPT = "t_manuscript";
       public static final String PID = "pid";
       public static final String PROBLEM = "problem";
       public static final String REPLY = "reply";
       public static final String RID = "rid";

    }

    public static User user = null;

    public static class URL{
        //基础URL

        private static final String BASE = "http://服务器地址/Problem";
        //注册
        public static final String REGISTER = BASE+"/register";
        //登录
        public static final String LOGIN = BASE+"/login";
        //更新用户个人信息
        public static final String UPDATAUSETINFO = BASE+"/updatauserinfo";
        //提问
        public static final String QUIZ = BASE+"/quiz";
        //更新提问
        public static final String UPDATAQUIZ = BASE+"/updataquiz";
        //回答
        public static final String REPLY = BASE+"/reply";
        //更新回答
        public static final String UPDATAREPLY = BASE+"/updatareply";
        //点赞
        public static final String PRAISE = BASE+"/praise";
        //收藏
        public static final String ENSHRINE = BASE+"/enshrine";
        //移除收藏
        public static final String UNENSHRINE = BASE+"/unenshrine";
        //获取指定用户提问、回答、赞的数量
        public static final String GETCOUNT = BASE+"/getcount";
        //获取指定用户的所有回答
        public static final String UIDQUIZ = BASE+"/uidquiz";
        //获取指定用户的所有回答
        public static final String UIDREPLY = BASE+"/uidreply";
        //获取指定用户的所有收藏
        public static final String UIDENSHRINE = BASE+"/uidenshrine";
        //获取指定话题至少包含一个回答的提问
        public static final String TIDQUIZAT = BASE+"/tidquizat";
        //获取所有至少包含一个回答的提问
        public static final String ALLQUIZAT = BASE+"/allquizat";
        //获取所有提问
        public static final String ALLQUIZ = BASE+"/allquiz";
        //获取指定提问的所有信息(包括提问信息和全部回答)
        public static final String PIDQUIZALL = BASE+"/pidquizall";
        //模糊搜索提问
        public static final String LIKEQUIZ = BASE+"/likequiz";
        //模糊搜索话题
        public static final String LIKETOPIC = BASE+"/liketopic";




    }

}
