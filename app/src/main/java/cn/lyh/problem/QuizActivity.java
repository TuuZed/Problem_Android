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
import android.widget.Toast;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import cn.lyh.problem.utils.ActivityManager;
import cn.lyh.problem.utils.ConfigInfo;
import cn.lyh.problem.utils.Tools;

/**
 * 提问
 */
public class QuizActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText mEtProblem, mEtTopic, mEtExplain;

    private int pid;
    private String problem;
    private String topic;
    private String explain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        initViews();
        initToobar();
        setEtProblemTextChangedListener();

        pid = getIntent().getIntExtra("pid",0);
        problem = getIntent().getStringExtra("problem");
        topic = getIntent().getStringExtra("topic");
        explain = getIntent().getStringExtra("explain");

        if (pid != 0){
            mEtProblem.setEnabled(false);
            mEtProblem.setText(problem);
            mEtTopic.setText(topic);
            mEtExplain.setText(explain);
        }
    }

    private void initViews() {
        ActivityManager.getManager().addActivity(this);
        mToolbar = (Toolbar) findViewById(R.id.toobar);
        mEtProblem = (EditText) findViewById(R.id.et_problem);
        mEtTopic = (EditText) findViewById(R.id.et_topic);
        mEtExplain = (EditText) findViewById(R.id.et_explain);
    }


    private void initToobar() {
        mToolbar.setTitle(getResources().getString(R.string.quiz));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setEtProblemTextChangedListener() {
        mEtProblem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if ((editable + "").equals("")) {
                    mEtExplain.setVisibility(View.GONE);
                    mEtTopic.setVisibility(View.GONE);
                } else {
                    mEtExplain.setVisibility(View.VISIBLE);
                    mEtTopic.setVisibility(View.VISIBLE);
                }
                String text = editable.toString();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_quiz, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_release) {
            String problem = mEtProblem.getText() + "";
            String topic = mEtTopic.getText() + "";
            String explain = mEtExplain.getText() + "";


            if (problem.equals("")) {
                Toast.makeText(this, getResources().getString(R.string.problem_not_null), Toast.LENGTH_SHORT).show();
            } else if (topic.equals("")) {
                Toast.makeText(this, getResources().getString(R.string.topic_not_null), Toast.LENGTH_SHORT).show();
            } else {
                //上传提问
                if (!Tools.isNetworkAvailable(this)) {
                    Toast.makeText(QuizActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
                    return true;
                }
                AjaxParams params = new AjaxParams();
                String url =null;
                if (pid == 0){
                    params.put("uid", ConfigInfo.user.getuId() + "");
                    params.put("problem", Tools.encode(mEtProblem.getText() + ""));
                    params.put("topic", Tools.encode(mEtTopic.getText() + ""));
                    params.put("explain", Tools.encode(mEtExplain.getText() + ""));
                    url = ConfigInfo.URL.QUIZ;
                }else{
                    params.put("pid", pid + "");
                    params.put("topic", Tools.encode(mEtTopic.getText() + ""));
                    params.put("explain", Tools.encode(mEtExplain.getText() + ""));
                    url = ConfigInfo.URL.UPDATAQUIZ;
                }
                FH(url,params);


            }
            return true;
        }

        return super.

                onOptionsItemSelected(item);
    }

    private void FH(String url,AjaxParams params){
        FinalHttp fh = new FinalHttp();
        fh.post(url, params, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                try {
                    JSONObject object = new JSONObject(s);
                    if (object.getInt("code") == 100) {
                        Toast.makeText(QuizActivity.this, getResources().getString(R.string.release_success), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(QuizActivity.this, getResources().getString(R.string.release_not_success), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    //Toast.makeText(QuizActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(QuizActivity.this,getResources().getString(R.string.connection_timeout) , Toast.LENGTH_SHORT).show();
            }
        });

    }

}
