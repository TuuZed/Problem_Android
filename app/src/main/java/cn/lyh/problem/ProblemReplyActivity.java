package cn.lyh.problem;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import cn.lyh.problem.utils.ActivityManager;
import cn.lyh.problem.utils.ConfigInfo;
import cn.lyh.problem.utils.Tools;

public class ProblemReplyActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTvReply, mTvProblem;

    private int rid = 0;
    private String reply = "";
    private String problem = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_reply);
        initViews();
        initData();
        initToobar();
        getData();
    }


    private void initViews() {
        ActivityManager.getManager().addActivity(this);
        mToolbar = (Toolbar) findViewById(R.id.toobar);
        mTvReply = (TextView) findViewById(R.id.tv_reply);
        mTvProblem = (TextView) findViewById(R.id.tv_problem);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initData() {
        rid = getIntent().getIntExtra("rid", 0);
        reply = getIntent().getStringExtra("reply");
        problem = getIntent().getStringExtra("problem");
    }

    private void initToobar() {
        mToolbar.setTitle(problem);
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
        getMenuInflater().inflate(R.menu.menu_problem_reply, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_praise) {
            //赞
            AjaxParams params = new AjaxParams();
            params.put("rid", rid + "");
            params.put("uid", ConfigInfo.user.getuId() + "");
            FH(ConfigInfo.URL.PRAISE, params, true);
            return true;
        } else if (id == R.id.action_enshrine) {
            //收藏
            AjaxParams params = new AjaxParams();
            params.put("rid", rid + "");
            params.put("uid", ConfigInfo.user.getuId() + "");
            FH(ConfigInfo.URL.ENSHRINE, params, false);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        getData();
    }

    private void getData() {
        mTvReply.setText(reply);
        mTvProblem.setText(problem);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void FH(String url, AjaxParams params, final boolean isPraise) {
        if (!Tools.isNetworkAvailable(this)) {
            Toast.makeText(ProblemReplyActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        FinalHttp fh = new FinalHttp();
        fh.post(url, params, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                try {
                    JSONObject object = new JSONObject(s);
                    if (object.getInt("code") == 100) {
                        if (isPraise) {
                            Toast.makeText(ProblemReplyActivity.this, getResources().getString(R.string.praise_success), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProblemReplyActivity.this, getResources().getString(R.string.enshrine_success), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        if (isPraise) {
                            Toast.makeText(ProblemReplyActivity.this, getResources().getString(R.string.praised), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProblemReplyActivity.this, getResources().getString(R.string.enshrined), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    //Toast.makeText(ProblemReplyActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(ProblemReplyActivity.this,getResources().getString(R.string.connection_timeout) , Toast.LENGTH_SHORT).show();
            }
        });


    }
}
