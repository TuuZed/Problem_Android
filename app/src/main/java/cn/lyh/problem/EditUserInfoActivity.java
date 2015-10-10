package cn.lyh.problem;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import cn.lyh.problem.db.UserDb;
import cn.lyh.problem.model.User;
import cn.lyh.problem.utils.ActivityManager;
import cn.lyh.problem.utils.ConfigInfo;
import cn.lyh.problem.utils.ImagesUtils;
import cn.lyh.problem.utils.Tools;

public class EditUserInfoActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mToolbar;
    private ImageView mIvAvatar;
    private EditText mEtName, mEtIntro;
    private TextView mTvMan, mTvWoman;
    private String mSex = ConfigInfo.user.getuSex();
    private String mName = ConfigInfo.user.getuName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);
        initViews();
        initToobar();
        initData();


        mEtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mName = editable.toString();
                mIvAvatar.setImageBitmap(ImagesUtils.
                        drawHeadIcon(EditUserInfoActivity.this, mName, true, 1));
            }
        });

    }

    private void initViews() {
        ActivityManager.getManager().addActivity(this);
        mToolbar = (Toolbar) findViewById(R.id.toobar);
        mEtIntro = (EditText) findViewById(R.id.et_intro);
        mEtName = (EditText) findViewById(R.id.et_name);
        mIvAvatar = (ImageView) findViewById(R.id.iv_avatar);
        mTvMan = (TextView) findViewById(R.id.tv_man);
        mTvMan.setOnClickListener(this);
        mTvWoman = (TextView) findViewById(R.id.tv_woman);
        mTvWoman.setOnClickListener(this);
    }

    private void initToobar() {
        mToolbar.setTitle(getResources().getString(R.string.title_activity_edit_user_info));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_user_info, menu);
        return true;
    }

    private void initData() {
        int sex = 1;
        if (mSex.equals("女")) {
            sex = 2;
            mTvWoman.setBackgroundColor(getResources().getColor(R.color.main_bule));
            mTvMan.setBackgroundColor(getResources().getColor(android.R.color.white));

        } else {
            mTvMan.setBackgroundColor(getResources().getColor(R.color.main_bule));
            mTvWoman.setBackgroundColor(getResources().getColor(android.R.color.white));
        }

        mEtName.setText(ConfigInfo.user.getuName());
        if (!ConfigInfo.user.getuIntro().equals("")) {
            mEtIntro.setText(ConfigInfo.user.getuIntro());
        }
        mIvAvatar.setImageBitmap(ImagesUtils.drawHeadIcon(this, mName, true, sex));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            //完成
            AjaxParams params = new AjaxParams();
            params.put("uid", ConfigInfo.user.getuId() + "");
            params.put("name", Tools.encode(mEtName.getText() + ""));
            params.put("sex", Tools.encode(mSex));
            params.put("intro", Tools.encode(mEtIntro.getText() + ""));
            FH(ConfigInfo.URL.UPDATAUSETINFO, params);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_man) {
            mSex = "男";
            mTvMan.setBackgroundColor(getResources().getColor(R.color.main_bule));
            mTvWoman.setBackgroundColor(getResources().getColor(android.R.color.white));
            mIvAvatar.setImageBitmap(ImagesUtils.drawHeadIcon(this, mName, true, 1));

        } else if (view.getId() == R.id.tv_woman) {
            mSex = "女";
            mTvWoman.setBackgroundColor(getResources().getColor(R.color.main_bule));
            mTvMan.setBackgroundColor(getResources().getColor(android.R.color.white));
            mIvAvatar.setImageBitmap(ImagesUtils.drawHeadIcon(this, mName, true, 2));
        }
    }

    private void FH(String url, AjaxParams params) {
        if (!Tools.isNetworkAvailable(this)){
            Toast.makeText(EditUserInfoActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
            return ;
        }

        FinalHttp fh = new FinalHttp();
        fh.post(url, params, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                try {
                    JSONObject object = new JSONObject(s);
                    if (object.getInt("code") == 100) {
                        JSONObject obj = object.getJSONObject("userinfo");
                        int uid = obj.getInt("uid");
                        String passwd = obj.getString("passwd");
                        String sex = obj.getString("sex");
                        String email = obj.getString("email");
                        String name = obj.getString("name");
                        String data = obj.getString("date");
                        String intro = obj.getString("intro");
                        User user = new User(uid, name, data, email, intro, sex, passwd);
                        UserDb userDb = new UserDb(EditUserInfoActivity.this);
                        userDb.updata(user);
                        ConfigInfo.user = user;
                        Toast.makeText(EditUserInfoActivity.this, getResources().getString(R.string.edit_success), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditUserInfoActivity.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    System.out.print(e.toString());
                    //Toast.makeText(EditUserInfoActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(EditUserInfoActivity.this,getResources().getString(R.string.connection_timeout) , Toast.LENGTH_SHORT).show();
            }
        });
    }
}
