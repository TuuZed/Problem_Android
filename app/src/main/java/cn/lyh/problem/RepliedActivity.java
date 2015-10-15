package cn.lyh.problem;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.lyh.problem.adapter.METAdapter;
import cn.lyh.problem.model.Problem;
import cn.lyh.problem.utils.ActivityManager;
import cn.lyh.problem.utils.ConfigInfo;
import cn.lyh.problem.utils.Tools;


public class RepliedActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private METAdapter mAdapter;


    private boolean isLast = false;
    private int page = 1;

    private int lastVisibleItem = 0;

    private SharedPreferences sp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replied_layout);
        initViews();
        initToobar();
        initAdapter();
        initData();

    }

    private void initViews() {
        ActivityManager.getManager().addActivity(this);
        mToolbar = (Toolbar) findViewById(R.id.toobar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initToobar() {
        mToolbar.setTitle(getResources().getString(R.string.replied));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void initAdapter() {
        mAdapter = new METAdapter(this, true, false,true);
        final LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(llm);

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = llm.findLastVisibleItemPosition();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == mAdapter.getItemCount()) {
                    //上拉加载更多
                    if (Tools.isNetworkAvailable(RepliedActivity.this)) {
                        getData(page++);
                    } else {
                        Toast.makeText(RepliedActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }

    //初始化数据
    private void initData() {
        sp = getSharedPreferences("replied", MODE_PRIVATE);
        if (Tools.isNetworkAvailable(this)) {
            getData(page++);
        } else {
            Toast.makeText(RepliedActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
            analyze(sp.getString("replied", null));
        }
    }

    @Override
    public void onRefresh() {
        if (Tools.isNetworkAvailable(this)) {
            page = 1;
            mAdapter.removeAll();
            getData(page++);
        } else {
            Toast.makeText(RepliedActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
            mAdapter.removeAll();
            analyze(sp.getString("replied", null));
            mSwipeRefreshLayout.setRefreshing(false);
        }

    }

    private void getData(int page) {
        if (!Tools.isNetworkAvailable(this)) {
            Toast.makeText(RepliedActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        FinalHttp fh = new FinalHttp();
        AjaxParams params = new AjaxParams();
        params.put("page", (page++) + "");
        params.put("uid", ConfigInfo.user.getuId() + "");
        fh.post(ConfigInfo.URL.UIDREPLY, params, new AjaxCallBack<String>() {

            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("replied", s);
                editor.commit();
                analyze(s);

            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(RepliedActivity.this,getResources().getString(R.string.connection_timeout) , Toast.LENGTH_SHORT).show();
            }
        });
        mSwipeRefreshLayout.setRefreshing(false);
    }

    //解析JSON
    private void analyze(String s) {
        if (s == null) return;
        try {
            JSONObject object = new JSONObject(s.replace("null,", ""));
            if (object.getInt("code") == 100) {
                isLast = object.getBoolean("last");
                mAdapter.setIsLast(isLast);
                JSONArray array = object.getJSONArray("replys");
                for (int i = 0; i < array.length(); i++) {
                    Problem p = new Problem();
                    JSONObject obj = array.getJSONObject(i);
                    p.setReply(obj.getString("reply"));
                    p.setrId(obj.getInt("rid"));
                    p.setName(ConfigInfo.user.getuName());
                    p.setuId(ConfigInfo.user.getuId());
                    p.setProblem(obj.getString("problem"));
                    p.setpId(obj.getInt("pid"));
                    p.setPraise(obj.getInt("praise"));
                    p.setSex(ConfigInfo.user.getuSex());
                    mAdapter.addItem(p);
                }
            } else {
                //Toast.makeText(MainActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            //Toast.makeText(RepliedActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

}
