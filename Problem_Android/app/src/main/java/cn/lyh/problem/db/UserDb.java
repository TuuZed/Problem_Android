package cn.lyh.problem.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.lyh.problem.model.User;
import cn.lyh.problem.utils.ConfigInfo;

/**
 * Created by LYH on 2015/9/29.
 */
public class UserDb extends SQLiteOpenHelper {

    private Context cxt;


    public UserDb(Context cxt) {
        super(cxt, ConfigInfo.DB.DBNAME, null, 1);
        this.cxt = cxt;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + ConfigInfo.DB.TABUSER + "(" +
                " _id integer primary key autoincrement,"
                + ConfigInfo.DB.UID + " integer,"
                + ConfigInfo.DB.UDATE + " text,"
                + ConfigInfo.DB.UEMAIL + " text,"
                + ConfigInfo.DB.UINTRO + " text,"
                + ConfigInfo.DB.UNAME + " text,"
                + ConfigInfo.DB.UPASSWD + " text,"
                + ConfigInfo.DB.USEX + " text)";
        db.execSQL(sql);
    }

    public void insert(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConfigInfo.DB.UID, user.getuId());
        values.put(ConfigInfo.DB.UDATE, user.getuDate());
        values.put(ConfigInfo.DB.UEMAIL, user.getuEmail());
        values.put(ConfigInfo.DB.UINTRO, user.getuIntro());
        values.put(ConfigInfo.DB.UNAME, user.getuName());
        values.put(ConfigInfo.DB.UPASSWD, user.getuPasswd());
        values.put(ConfigInfo.DB.USEX, user.getuSex());
        db.insert(ConfigInfo.DB.TABUSER, null, values);
        db.close();
    }

    public void updata(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConfigInfo.DB.UDATE, user.getuDate());
        values.put(ConfigInfo.DB.UEMAIL, user.getuEmail());
        values.put(ConfigInfo.DB.UINTRO, user.getuIntro());
        values.put(ConfigInfo.DB.UNAME, user.getuName());
        values.put(ConfigInfo.DB.USEX, user.getuSex());
        db.update(ConfigInfo.DB.TABUSER, values, "uid=?", new String[]{ConfigInfo.user.getuId() + ""});
    }


    public User query() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.query(ConfigInfo.DB.TABUSER, null, null, null, null, null, null);
        if (c.moveToNext()) {
            int uId = c.getInt(c.getColumnIndex(ConfigInfo.DB.UID));
            String uDate = c.getString(c.getColumnIndex(ConfigInfo.DB.UDATE));
            String uEmail = c.getString(c.getColumnIndex(ConfigInfo.DB.UEMAIL));
            String uIntro = c.getString(c.getColumnIndex(ConfigInfo.DB.UINTRO));
            String uName = c.getString(c.getColumnIndex(ConfigInfo.DB.UNAME));
            String uSex = c.getString(c.getColumnIndex(ConfigInfo.DB.USEX));
            String uPasswd = c.getString(c.getColumnIndex(ConfigInfo.DB.UPASSWD));
            return new User(uId, uName, uDate, uEmail, uIntro, uSex, uPasswd);
        }
        return null;

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
