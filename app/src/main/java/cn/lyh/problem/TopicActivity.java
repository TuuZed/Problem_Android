package cn.lyh.problem;

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

import cn.lyh.problem.adapter.METAdapter;
import cn.lyh.problem.model.Problem;
import cn.lyh.problem.utils.ActivityManager;
import cn.lyh.problem.utils.ConfigInfo;
import cn.lyh.problem.utils.Tools;

public class TopicActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private METAdapter mAdapter = null;

    private int page = 1;
    private boolean isLast = false;
    private int tid = 0;
    private String topic = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        initViews();
        initData();
        initToobar();
        initAdapter();

        getData(page++);
    }


    private void initViews() {
        ActivityManager.getManager().addActivity(this);
        mToolbar = (Toolbar) findViewById(R.id.toobar);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void initData() {
        tid = getIntent().getIntExtra("tid", 0);
        topic = getIntent().getStringExtra("topic");
    }

    private void initToobar() {
        mToolbar.setTitle(topic);
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
        final LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(llm);
        mAdapter = new METAdapter(this, true, false, false);
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

        getMenuInflater().inflate(R.menu.menu_topic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        if (!Tools.isNetworkAvailable(this)) {
            Toast.makeText(TopicActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        mAdapter.removeAll();
        page = 1;
        getData(page++);
    }

    private void getData(int page) {
        if (!Tools.isNetworkAvailable(this)) {
            Toast.makeText(TopicActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
        FinalHttp fh = new FinalHttp();
        AjaxParams params = new AjaxParams();
        params.put("tid", tid + "");
        params.put("page", (page++) + "");
        fh.post(ConfigInfo.URL.TIDQUIZAT, params, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                try {
                    JSONObject object = new JSONObject(s);
                    if (object.getInt("code") == 100) {
                        isLast = object.getBoolean("last");
                        mAdapter.setIsLast(isLast);
                        JSONArray array = object.getJSONArray("problems");
                        for (int i = 0; i < array.length(); i++) {
                            Problem p = new Problem();
                            JSONObject obj = array.getJSONObject(i);
                            p.setReply(obj.getString("reply"));
                            p.setrId(obj.getInt("rid"));
                            p.setName(obj.getString("name"));
                            p.setuId(obj.getInt("uid"));
                            p.setProblem(obj.getString("problem"));
                            p.setpId(obj.getInt("pid"));
                            p.setPraise(obj.getInt("praise"));
                            p.setSex(obj.getString("sex"));
                            mAdapter.addItem(p);
                        }
                    } else {
                        //Toast.makeText(TopicActivity.this, "没有更多了", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    //Toast.makeText(TopicActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(TopicActivity.this, getResources().getString(R.string.connection_timeout), Toast.LENGTH_SHORT).show();
            }
        });

        mSwipeRefreshLayout.setRefreshing(false);
    }
}
