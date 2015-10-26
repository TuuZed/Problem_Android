package cn.lyh.problem.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import cn.lyh.problem.R;
import cn.lyh.problem.utils.ActivityManager;
import cn.lyh.problem.utils.ConfigInfo;
import cn.lyh.problem.utils.ImagesUtils;
import cn.lyh.problem.utils.Tools;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private ImageView mIvAvatar;
    private TextView mTvName, mTvIntro;
    private TextView mTvReply, mTvQuiz, mTvPraise;

    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initViews();
        initToobar();
        initData();
        getData();
    }

    private void initViews() {
        ActivityManager.getManager().addActivity(this);
        mToolbar = (Toolbar) findViewById(R.id.toobar);
        mIvAvatar = (ImageView) findViewById(R.id.iv_avatar);
        mTvName = (TextView) findViewById(R.id.tv_name);
        mTvIntro = (TextView) findViewById(R.id.tv_intro);
        mTvReply = (TextView) findViewById(R.id.tv_reply);
        mTvQuiz = (TextView) findViewById(R.id.tv_quiz);
        mTvPraise = (TextView) findViewById(R.id.tv_praise);
        findViewById(R.id.ll_reply).setOnClickListener(this);
        findViewById(R.id.ll_quiz).setOnClickListener(this);
    }

    private void initToobar() {
        mToolbar.setTitle(getResources().getString(R.string.title_activity_user_info));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initData() {
        int sex = 1;
        if (ConfigInfo.user.getuSex().equals("女")) {
            sex = 2;
        }
        mTvName.setText(ConfigInfo.user.getuName());
        if (!ConfigInfo.user.getuIntro().equals("")) {
            mTvIntro.setText(ConfigInfo.user.getuName());
        }
        mIvAvatar.setImageBitmap(ImagesUtils.drawHeadIcon(this, ConfigInfo.user.getuName(), true, sex));
        mTvIntro.setText(ConfigInfo.user.getuIntro());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            //编辑
            startActivity(new Intent(this, EditUserInfoActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.ll_reply:
                intent.setClass(this, RepliedActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_quiz:
                intent.setClass(this, QuizedActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void getData() {
        if (!Tools.isNetworkAvailable(this)) {
            Toast.makeText(UserInfoActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
            return;
        }
        FinalHttp fh = new FinalHttp();
        AjaxParams params = new AjaxParams();
        params.put("uid", ConfigInfo.user.getuId() + "");
        fh.post(ConfigInfo.URL.GETCOUNT, params, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                try {
                    JSONObject object = new JSONObject(s);
                    if (object.getInt("code") == 100) {
                        mTvReply.setText(object.getInt("reply") + "");
                        mTvQuiz.setText(object.getInt("problem") + "");
                        mTvPraise.setText(object.getInt("praise") + "");
                    }
                } catch (JSONException e) {
                  //  Toast.makeText(UserInfoActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(UserInfoActivity.this,getResources().getString(R.string.connection_timeout) , Toast.LENGTH_SHORT).show();
            }
        });
    }


}
