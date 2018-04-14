package cn.lyh.problem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.lyh.problem.R;
import cn.lyh.problem.adapter.METAdapter;
import cn.lyh.problem.model.Problem;
import cn.lyh.problem.utils.ActivityManager;
import cn.lyh.problem.utils.ConfigInfo;
import cn.lyh.problem.utils.Tools;

/**
 * 收藏
 */
public class EnshrineActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private METAdapter mAdapter;


    private boolean isLast = false;
    private int page = 1;

    private SharedPreferences sp = null;

    private int lastVisibleItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enshrine);
        initViews();
        initToobar();
        initAdapter();
        initData();
    }


    private void initViews() {
        ActivityManager.getManager().addActivity(this);
        mToolbar = (Toolbar) findViewById(R.id.toobar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }


    private void initToobar() {
        mToolbar.setTitle(getResources().getString(R.string.title_activity_enshrine));
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
        mAdapter = new METAdapter(this, false, true,false);
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
                    if (Tools.isNetworkAvailable(EnshrineActivity.this)) {
                        getData(page++);
                    } else {
                        Toast.makeText(EnshrineActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }

    //初始化数据
    private void initData() {
        sp = getSharedPreferences("enshrine", MODE_PRIVATE);
        if (Tools.isNetworkAvailable(this)) {
            getData(page++);
        } else {
            Toast.makeText(EnshrineActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
            analyze(sp.getString("enshrine", null));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_more, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_search) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        if (Tools.isNetworkAvailable(this)) {
            page = 1;
            mAdapter.removeAll();
            getData(page++);
        } else {
            Toast.makeText(EnshrineActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
            mAdapter.removeAll();
            analyze(sp.getString("enshrine", null));
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }


    //取得数据
    private void getData(int page) {
        if (!Tools.isNetworkAvailable(this)) {
            Toast.makeText(EnshrineActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        FinalHttp fh = new FinalHttp();
        AjaxParams params = new AjaxParams();
        params.put("uid", ConfigInfo.user.getuId() + "");
        params.put("page", (page++) + "");
        fh.post(ConfigInfo.URL.UIDENSHRINE, params, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("enshrine", s);
                editor.commit();
                analyze(s);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                //Toast.makeText(EnshrineActivity.this, strMsg, Toast.LENGTH_SHORT).show();
                Toast.makeText(EnshrineActivity.this,getResources().getString(R.string.connection_timeout) , Toast.LENGTH_SHORT).show();
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
                JSONArray array = object.getJSONArray("enshrines");
                for (int i = 0; i < array.length(); i++) {
                    Problem p = new Problem();
                    JSONObject obj = array.getJSONObject(i);
                    p.setReply(obj.getString("reply"));
                    p.setrId(obj.getInt("rid"));
                    p.setTopic(obj.getString("topic"));
                    p.settId(obj.getInt("tid"));
                    p.setName(obj.getString("name"));
                    p.setuId(obj.getInt("uid"));
                    p.setProblem(obj.getString("problem"));
                    p.setpId(obj.getInt("pid"));
                    p.setPraise(obj.getInt("praise"));
                    p.setSex(obj.getString("sex"));
                    mAdapter.addItem(p);
                }
            } else {
                //Toast.makeText(MainActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            System.out.print(e.toString());
        }

    }
}
