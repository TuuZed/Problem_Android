package cn.lyh.problem.activity;

import android.content.Intent;
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
import cn.lyh.problem.adapter.ProblemAdapter;
import cn.lyh.problem.model.Problem;
import cn.lyh.problem.utils.ActivityManager;
import cn.lyh.problem.utils.ConfigInfo;
import cn.lyh.problem.utils.Tools;

public class ProblemActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private Toolbar mToolbar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProblemAdapter mAdapter = null;
    private RecyclerView mRecyclerView;

    private int pid = 0;
    private String problem = null;

    private boolean isLast = false;
    private int page = 1;


    @Override
    protected void onRestart() {
        super.onRestart();
        page = 1;
        mAdapter.removeAll();
        getData(page++);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);
        initViews();
        initData();
        initToolbar();
        initAdapter();
        getData(page++);


    }

    private void initViews() {
        ActivityManager.getManager().addActivity(this);
        mToolbar = (Toolbar) findViewById(R.id.toobar);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }

    private void initData() {
        pid = getIntent().getIntExtra("pid", 0);
        problem = getIntent().getStringExtra("problem");
    }

    private void initToolbar() {
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

    private int lastVisibleItem = 0;

    private void initAdapter() {
        mAdapter = new ProblemAdapter(this,pid);
        final LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(mAdapter);


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
                    getData(page++);
                }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_problem, menu);
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
        if (!Tools.isNetworkAvailable(this)){
            Toast.makeText(ProblemActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        mAdapter.removeAll();
        page = 1;
        getData(page);
    }


    private void getData(int page) {
        if (!Tools.isNetworkAvailable(this)){
            Toast.makeText(ProblemActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        FinalHttp fh = new FinalHttp();
        AjaxParams params = new AjaxParams();
        params.put("page", (page++) + "");
        params.put("pid", pid + "");
        fh.post(ConfigInfo.URL.PIDQUIZALL, params, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                try {
                    JSONObject object = new JSONObject(s);
                    if (object.getInt("code") == 100) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        isLast = object.getBoolean("last");
                        mAdapter.setIsLast(isLast);
                        Problem head = new Problem();
                        head.setProblem(object.getString("problem"));
                        head.settId(object.getInt("tid"));
                        head.setTopic(object.getString("topic"));
                        head.setExplain(object.getString("explain"));
                        mAdapter.setHead(head);
                        JSONArray array = object.getJSONArray("replys");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            Problem p = new Problem();
                            p.setuId(obj.getInt("uid"));
                            p.setPraise(obj.getInt("praise"));
                            p.setSex(obj.getString("sex"));
                            p.setrId(obj.getInt("rid"));
                            p.setName(obj.getString("name"));
                            p.setReply(obj.getString("reply"));
                            mAdapter.addItem(p);

                        }
                    } else {
                        //Toast.makeText(ProblemActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    //Toast.makeText(ProblemActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(ProblemActivity.this,getResources().getString(R.string.connection_timeout) , Toast.LENGTH_SHORT).show();
            }
        });
    }

}
