package cn.lyh.problem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import cn.lyh.problem.utils.ActivityManager;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initViews();
    }
    private void initViews() {

        ActivityManager.getManager().addActivity(this);
        if (Integer.parseInt(android.os.Build.VERSION.SDK + "") >= 19) {
            // level 19+
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

}
