package cn.lyh.problem.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.lyh.problem.R;
import cn.lyh.problem.adapter.DrawerAdapter;
import cn.lyh.problem.adapter.METAdapter;
import cn.lyh.problem.model.Problem;
import cn.lyh.problem.utils.ActivityManager;
import cn.lyh.problem.utils.ConfigInfo;
import cn.lyh.problem.utils.Tools;

public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private METAdapter mAdapter = null;

    private int page = 1;
    private boolean isLast = false;

    private int lastVisibleItem = 0;

    private SharedPreferences sp = null;


    private Handler mHandler;

    @Override
    protected void onRestart() {
        super.onRestart();
        initDrawer();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.arg1 == 0x123) {
                    findViewById(R.id.rl_progress).setVisibility(View.GONE);
                    findViewById(R.id.rl_content).setVisibility(View.VISIBLE);
                }
            }
        };

        new Thread() {
            @Override
            public void run() {
                super.run();
                boolean is = true;
                while (is) {
                    if (ConfigInfo.URL.isGetServerAddress) {
                        is = false;
                        Message message = new Message();
                        message.arg1 = 0x123;
                        mHandler.sendMessage(message);
                    }
                }
            }
        }.start();


        initViews();
        initToolbar();
        initDrawer();
        initAdapter();
        initData();

    }

    //初始化View
    private void initViews() {
        ActivityManager.getManager().addActivity(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mToolbar = (Toolbar) findViewById(R.id.toobar);
        mListView = (ListView) findViewById(R.id.lv_drawer);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }

    //初始化Toolbar
    private void initToolbar() {
        mToolbar.setTitle(getResources().getString(R.string.home));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, R.string.app_name, R.string.app_name);
        toggle.syncState();
        mDrawerLayout.setDrawerListener(toggle);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });
    }

    //初始化抽屉
    private void initDrawer() {
        DrawerAdapter adapter = new DrawerAdapter(this, mDrawerLayout);
        mListView.setAdapter(adapter);

    }

    //初始化适配器
    private void initAdapter() {
        mAdapter = new METAdapter(this, false, false, false);
        mRecyclerView.setAdapter(mAdapter);
        final LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
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
                    if (Tools.isNetworkAvailable(MainActivity.this)) {
                        getData(page++);
                    } else {
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

    }

    //初始化数据
    private void initData() {
        sp = getSharedPreferences("main", MODE_PRIVATE);
        if (Tools.isNetworkAvailable(this)) {
            getData(page++);
        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
            analyze(sp.getString("main", null));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_github) {
            Intent intent = new Intent();// 创建Intent对象
            intent.setAction(Intent.ACTION_VIEW);// 为Intent设置动作
            String data = "https://github.com/TuuZed/Problem_Android";
            intent.setData(Uri.parse(data));// 为Intent设置数据
            startActivity(intent);// 将Intent传递给Activity
            return true;
        }
        if (id == R.id.action_sign_out) {
            //登出,清除数据,跳转至欢迎界面
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.action_sign_out))
                    .setMessage(getResources().getString(R.string.sure_to_logout_and_exit_the_program))
                    .setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            android.app.ActivityManager manager =
                                    (android.app.ActivityManager) MainActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
                            manager.clearApplicationUserData();
                        }
                    }).setNegativeButton(getResources().getString(R.string.cancel), null).show();


        }
        if (id == R.id.action_search) {
            //搜索
            startActivity(new Intent(this, SearchActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private long exitTime = 0;

    //退出程序
    private void exit() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.and_then_press_an_exit_procedure),
                    Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            ActivityManager.getManager().closeActivity();
        }
    }

    @Override
    public void onRefresh() {
        if (Tools.isNetworkAvailable(this)) {
            page = 1;
            mAdapter.removeAll();
            getData(page++);
        } else {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
            mAdapter.removeAll();
            analyze(sp.getString("main", null));
            mSwipeRefreshLayout.setRefreshing(false);
        }

    }

    //取得数据
    private void getData(int page) {
        FinalHttp fh = new FinalHttp();
        AjaxParams params = new AjaxParams();
        params.put("page", (page++) + "");
        fh.post(ConfigInfo.URL.ALLQUIZAT, params, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("main", s);
                editor.commit();
                analyze(s);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(MainActivity.this, getResources().getString(R.string.connection_timeout), Toast.LENGTH_SHORT).show();
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
                JSONArray array = object.getJSONArray("problems");
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
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

}
