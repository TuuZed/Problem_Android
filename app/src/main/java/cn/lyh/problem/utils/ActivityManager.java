package cn.lyh.problem.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LYH on 2015/9/25.
 */
public class ActivityManager {
    private List<Activity> mList = new ArrayList<Activity>();

    private ActivityManager(){}

    public static ActivityManager getManager(){
        return new ActivityManager();
    }
    /**
     * 添加Activity到集合中
     * @param activity
     */
    public void addActivity(Activity activity){
        mList.add(activity);
    }

    /**
     * 关闭所有Activity
     */
    public void closeActivity(){
        for (Activity a:mList) {
            if (a != null)
                a.finish();
        }
        mList = null;
    }
}
