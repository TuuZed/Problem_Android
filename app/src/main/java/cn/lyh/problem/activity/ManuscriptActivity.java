package cn.lyh.problem.activity;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import cn.lyh.problem.R;
import cn.lyh.problem.adapter.ManuscriptAdapter;
import cn.lyh.problem.utils.ActivityManager;

public class ManuscriptActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ManuscriptAdapter mAdapter = null;

    @Override
    protected void onRestart() {
        super.onRestart();
        mAdapter.Refresh();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manuscript);
        initViews();
        initToobar();
        initAdapter();
    }
    private void initViews(){
        ActivityManager.getManager().addActivity(this);
        mToolbar = (Toolbar)findViewById(R.id.toobar);
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }
    private void initToobar(){
        mToolbar.setTitle(getResources().getString(R.string.manuscript));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void initAdapter(){
        mAdapter = new ManuscriptAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(llm);
    }

    @Override
    public void onRefresh() {
        mAdapter.Refresh();
        mSwipeRefreshLayout.setRefreshing(false);
    }


}
