package cn.lyh.problem.model;

/**
 * Created by LYH on 2015/9/29.
 */
public class User {

    private int uId;
    private String uName;
    private String uDate;
    private String uEmail;
    private String uIntro;
    private String uSex;
    private String uPasswd;


    public User(int uId, String uName, String uDate, String uEmail, String uIntro, String uSex, String uPasswd) {
        this.uId = uId;
        this.uName = uName;
        this.uDate = uDate;
        this.uEmail = uEmail;
        this.uIntro = uIntro;
        this.uSex = uSex;
        this.uPasswd = uPasswd;
    }


    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuDate() {
        return uDate;
    }

    public void setuDate(String uDate) {
        this.uDate = uDate;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuIntro() {
        return uIntro;
    }

    public void setuIntro(String uIntro) {
        this.uIntro = uIntro;
    }

    public String getuSex() {
        return uSex;
    }

    public void setuSex(String uSex) {
        this.uSex = uSex;
    }

    public String getuPasswd() {
        return uPasswd;
    }

    public void setuPasswd(String uPasswd) {
        this.uPasswd = uPasswd;
    }
}
