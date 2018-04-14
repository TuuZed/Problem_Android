package cn.lyh.problem.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.lyh.problem.R;
import cn.lyh.problem.adapter.SearchAdapter;
import cn.lyh.problem.model.Problem;
import cn.lyh.problem.utils.ActivityManager;
import cn.lyh.problem.utils.ConfigInfo;
import cn.lyh.problem.utils.Tools;

public class SearchActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText mEtSearch;
    private SearchAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private boolean isLast = false;
    private int page = 1;
    private String problem = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initViews();
        initToobar();
        initAdapter();
        getData(problem,page);

        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                problem = editable.toString();
                getData(problem,page);
            }
        });


    }

    private void initViews() {
        ActivityManager.getManager().addActivity(this);
        mToolbar = (Toolbar) findViewById(R.id.toobar);
        mEtSearch = (EditText) findViewById(R.id.et_search);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
    }

    private void initToobar() {
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
    private void initAdapter(){
        mAdapter = new SearchAdapter(this);
        final LinearLayoutManager llm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
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
                    getData(problem,page++);
                }

            }
        });





    }

    private void getData(String problem, int page) {

        if (!Tools.isNetworkAvailable(this)){
            Toast.makeText(SearchActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
            return;
        }

        FinalHttp fh = new FinalHttp();
        AjaxParams params = new AjaxParams();
        params.put("problem", Tools.encode(problem).replace("\n",""));
        params.put("page", (page++) + "");
        fh.post(ConfigInfo.URL.LIKEQUIZ, params, new AjaxCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        super.onSuccess(s);

                        try{
                            JSONObject object = new JSONObject(s);
                            if (object.getInt("code")==100){
                                mAdapter.removeAll();
                                isLast = object.getBoolean("last");
                                mAdapter.setIsLast(isLast);
                                JSONArray array = object.getJSONArray("problems");
                                for (int i = 0;i<array.length();i++){
                                    JSONObject obj = array.getJSONObject(i);
                                    Problem p = new Problem();
                                    p.setpId(obj.getInt("pid"));
                                    p.setProblem(obj.getString("problem"));
                                    mAdapter.addItem(p);
                                }
                            }else{

                            }
                        }catch (JSONException e){
                           // Toast.makeText(SearchActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo, String strMsg) {
                        super.onFailure(t, errorNo, strMsg);
                        Toast.makeText(SearchActivity.this,getResources().getString(R.string.connection_timeout) , Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }


}
