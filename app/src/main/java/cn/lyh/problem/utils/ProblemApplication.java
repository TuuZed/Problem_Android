package cn.lyh.problem.utils;

import android.app.Application;
import android.content.SharedPreferences;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

public class ProblemApplication extends Application {
    SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences("address", MODE_PRIVATE);
        String address = sp.getString("address", "");
        String date = sp.getString("date", "");
        Date d = new Date();
        String sd = Tools.sdf.format(d);
        try {
            if (Tools.daysBetween(sd, date) > 0) {
                ConfigInfo.URL.setBASE(address);
                return;
            }
        } catch (ParseException e) {
        }
        FinalHttp fh = new FinalHttp();
        fh.get(ConfigInfo.URL.GETSERVERADDRESS, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                try {
                    SharedPreferences.Editor editor = sp.edit();
                    JSONObject object = new JSONObject(s);
                    String address = object.getString("address");
                    String date = object.getString("date");
                    editor.putString("date", date);
                    editor.putString("address", address);
                    editor.commit();
                    ConfigInfo.URL.setBASE(address);

                } catch (JSONException e) {
                }
            }
        });


    }
}
