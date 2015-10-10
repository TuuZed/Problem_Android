package cn.lyh.problem.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import cn.lyh.problem.model.Problem;
import cn.lyh.problem.utils.ConfigInfo;

public class ManuscriptDb extends SQLiteOpenHelper {
    private Context cxt;

    public ManuscriptDb(Context cxt) {
        super(cxt, ConfigInfo.DB.TABMANUSRCIPT, null, 1);
        this.cxt = cxt;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + ConfigInfo.DB.TABMANUSRCIPT + "(" +
                " _id integer primary key autoincrement,"
                + ConfigInfo.DB.PID + " integer,"
                + ConfigInfo.DB.PROBLEM + " text,"
                + ConfigInfo.DB.RID + " integer,"
                + ConfigInfo.DB.REPLY + " text)";
        db.execSQL(sql);
    }


    public void insert(Problem p) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConfigInfo.DB.PID, p.getpId());
        values.put(ConfigInfo.DB.PROBLEM, p.getProblem());
        values.put(ConfigInfo.DB.RID, p.getrId());
        values.put(ConfigInfo.DB.REPLY, p.getReply());
        db.insert(ConfigInfo.DB.TABMANUSRCIPT, null, values);
        db.close();
    }

    public void delect(int pid, int rid) {
        SQLiteDatabase db = getWritableDatabase();
        if (rid == 0) {
            db.delete(ConfigInfo.DB.TABMANUSRCIPT, ConfigInfo.DB.PID + "=?", new String[]{pid + ""});
        } else if (pid == 0) {
            db.delete(ConfigInfo.DB.TABMANUSRCIPT, ConfigInfo.DB.RID + "=?", new String[]{rid + ""});
        }

        db.close();
    }

    public void update(String reply, int pid, int rid) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConfigInfo.DB.REPLY, reply);
        if (rid == 0) {
            db.update(ConfigInfo.DB.TABMANUSRCIPT, values, ConfigInfo.DB.PID + "=?", new String[]{pid + ""});
        }else if(pid == 0){
            db.update(ConfigInfo.DB.TABMANUSRCIPT, values, ConfigInfo.DB.RID + "=?", new String[]{rid + ""});
        }
        db.close();
    }

    public List<Problem> query() {
        List<Problem> lists = new ArrayList<Problem>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(ConfigInfo.DB.TABMANUSRCIPT, null, null, null, null, null, null);
        while (c.moveToNext()) {
            Problem p = new Problem();
            p.setpId(c.getInt(c.getColumnIndex(ConfigInfo.DB.PID)));
            p.setProblem(c.getString(c.getColumnIndex(ConfigInfo.DB.PROBLEM)));
            p.setrId(c.getInt(c.getColumnIndex(ConfigInfo.DB.RID)));
            p.setReply(c.getString(c.getColumnIndex(ConfigInfo.DB.REPLY)));
            lists.add(p);
        }
        return lists;
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
