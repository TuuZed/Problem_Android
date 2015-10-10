package cn.lyh.problem;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
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

import cn.lyh.problem.db.ManuscriptDb;
import cn.lyh.problem.model.Problem;
import cn.lyh.problem.utils.ConfigInfo;
import cn.lyh.problem.utils.Tools;

public class AddReplyActivity extends AppCompatActivity {
    private Toolbar mTolbar;
    private EditText mEtReply;

    private int pid = 0;
    private String problme = "";


    private int rid = 0;
    private String reply = "";
    private boolean isUpdate = false;

    private ManuscriptDb db = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reply);
        initViews();
        initData();
        initToobar();
        db = new ManuscriptDb(this);
    }

    private void initViews() {
        mTolbar = (Toolbar) findViewById(R.id.toobar);
        mEtReply = (EditText) findViewById(R.id.et_reply);
    }

    private void initData() {
        pid = getIntent().getIntExtra("pid", 0);
        problme = getIntent().getStringExtra("problem");

        isUpdate = getIntent().getBooleanExtra("isUpdate", false);

        reply = getIntent().getStringExtra("reply");
        rid = getIntent().getIntExtra("rid", 0);
        if (reply!=null) {
            mEtReply.setText(reply);
            mEtReply.setSelection(reply.length());
        }
        mTolbar.setTitle(getResources().getString(R.string.title_activity_add_reply));
        if (rid != 0) {
            mTolbar.setTitle(getResources().getString(R.string.title_activity_update_reply));
        }


    }

    private void initToobar() {
        mTolbar.setSubtitle(problme);
        setSupportActionBar(mTolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_reply, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_release) {
            //回答
            if (!Tools.isNetworkAvailable(this)) {
                Toast.makeText(AddReplyActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
                return true;
            }
            if ((mEtReply.getText() + "").equals("")) {
                Toast.makeText(AddReplyActivity.this, getResources().getString(R.string.reply_not_null), Toast.LENGTH_SHORT).show();
                return true;
            }
            AjaxParams params = new AjaxParams();
            String url = null;
            if (rid != 0) {
                url = ConfigInfo.URL.UPDATAREPLY;
                params.put("rid", rid + "");
                params.put("reply", Tools.encode(mEtReply.getText() + ""));
            } else {
                url = ConfigInfo.URL.REPLY;
                params.put("uid", ConfigInfo.user.getuId() + "");
                params.put("pid", pid + "");
                params.put("reply", Tools.encode(mEtReply.getText() + ""));
            }
            FH(url, params);


            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            save();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void save() {
        if ((mEtReply.getText()+"").equals("")){
            finish();
            return;
        }
        if (reply.equals(mEtReply.getText().toString())){
            finish();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.save))
                .setMessage(getResources().getString(R.string.sure_save_manuscript))
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNeutralButton(getResources().getString(R.string.wait), null)
                .setPositiveButton(getResources().getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Problem p = new Problem();
                        p.setpId(pid);
                        p.setReply(mEtReply.getText().toString());
                        p.setProblem(problme);
                        p.setrId(rid);
                        if (isUpdate) {
                            db.update(mEtReply.getText().toString(), pid, rid);
                        } else {
                            db.insert(p);
                        }
                        finish();
                    }
                }).show();
    }

    private void FH(String url, AjaxParams params) {
        FinalHttp fh = new FinalHttp();

        fh.post(url, params, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                try {
                    JSONObject object = new JSONObject(s);
                    if (object.getInt("code") == 100) {
                        Toast.makeText(AddReplyActivity.this, getResources().getString(R.string.reply_success), Toast.LENGTH_SHORT).show();
                        db.delect(pid,rid);
                        finish();
                    }
                } catch (JSONException e) {
                    //Toast.makeText(AddReplyActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    System.out.print(e.toString());
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Toast.makeText(AddReplyActivity.this,getResources().getString(R.string.connection_timeout) , Toast.LENGTH_SHORT).show();
            }
        });
    }

}
