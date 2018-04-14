package cn.lyh.problem.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import cn.lyh.problem.R;
import cn.lyh.problem.db.UserDb;
import cn.lyh.problem.model.User;
import cn.lyh.problem.utils.ActivityManager;
import cn.lyh.problem.utils.ConfigInfo;
import cn.lyh.problem.utils.Tools;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnReg, mBtnLogin;
    private UserDb mUserDb;
    private RelativeLayout rr_pb;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

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
        mUserDb = new UserDb(WelcomeActivity.this);
        init();


    }

    private void initViews() {
        ActivityManager.getManager().addActivity(this);
        if (Integer.parseInt(android.os.Build.VERSION.SDK + "") >= 19) {
            // level 19+
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnReg = (Button) findViewById(R.id.btn_reg);
        mBtnLogin.setOnClickListener(this);
        mBtnReg.setOnClickListener(this);
        rr_pb = (RelativeLayout) findViewById(R.id.rr_pb);

    }

    private void init() {
        User user = mUserDb.query();
        if (user != null) {
            ConfigInfo.user = user;
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                //登录
                Login();
                break;
            case R.id.btn_reg:
                //注册
                Reg();
                break;
            default:
                break;
        }
    }

    private void Reg() {
        final View view = LayoutInflater.from(WelcomeActivity.this)
                .inflate(R.layout.dialog_reg_layout, null);
        final EditText etUName = (EditText) view.findViewById(R.id.et_u_name);
        final EditText etUPasswd = (EditText) view.findViewById(R.id.et_u_passwd);
        final EditText etEMail = (EditText) view.findViewById(R.id.et_u_email);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.reg))
                .setView(view)
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .setPositiveButton(getResources().getString(R.string.reg), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rr_pb.setVisibility(View.VISIBLE);
                        String name = etUName.getText() + "";
                        String passwd = etUPasswd.getText() + "";
                        String email = etEMail.getText() + "";

                        if (name.equals("") || passwd.equals("") || email.equals("")) {
                            Toast.makeText(WelcomeActivity.this, getResources().getString(R.string.name_email_passwd_not_null), Toast.LENGTH_SHORT).show();
                            rr_pb.setVisibility(View.GONE);
                            return;
                        }
                        if (!Tools.isEmail(email)) {
                            Toast.makeText(WelcomeActivity.this, getResources().getString(R.string.enter_email), Toast.LENGTH_SHORT).show();
                            rr_pb.setVisibility(View.GONE);
                            return;
                        }
                        AjaxParams parmas = new AjaxParams();
                        parmas.put("email", Tools.encode(etEMail.getText() + ""));
                        parmas.put("passwd", Tools.encode(etUPasswd.getText() + ""));
                        parmas.put("name", Tools.encode(etUName.getText() + ""));
                        FH(ConfigInfo.URL.REGISTER, parmas);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void Login() {
        final View view = LayoutInflater.from(WelcomeActivity.this)
                .inflate(R.layout.dialog_login_layout, null);
        final EditText etEMail = (EditText) view.findViewById(R.id.et_u_email);
        final EditText etUPasswd = (EditText) view.findViewById(R.id.et_u_passwd);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.login))
                .setView(view)
                .setNegativeButton(getResources().getString(R.string.cancel), null)
                .setPositiveButton(getResources().getString(R.string.login), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                rr_pb.setVisibility(View.VISIBLE);
                                String email = etEMail.getText() + "";
                                String passwd = etUPasswd.getText() + "";
                                if (email.equals("") || passwd.equals("")) {
                                    Toast.makeText(WelcomeActivity.this, getResources().getString(R.string.email_passwd_not_null),
                                            Toast.LENGTH_SHORT).show();
                                    rr_pb.setVisibility(View.GONE);
                                    return;
                                }
                                if (!Tools.isEmail(email)) {
                                    Toast.makeText(WelcomeActivity.this, getResources().getString(R.string.enter_email),
                                            Toast.LENGTH_LONG).show();
                                    rr_pb.setVisibility(View.GONE);
                                    return;
                                }
                                AjaxParams parmas = new AjaxParams();
                                parmas.put("email", Tools.encode(email));
                                parmas.put("passwd", Tools.encode(passwd));
                                FH(ConfigInfo.URL.LOGIN, parmas);
                            }
                        }

                );
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void FH(String url, AjaxParams params) {

        if (!Tools.isNetworkAvailable(this)) {
            Toast.makeText(WelcomeActivity.this, getResources().getString(R.string.networkunusable), Toast.LENGTH_SHORT).show();
            return;
        }
        FinalHttp fh = new FinalHttp();
        fh.post(url, params, new AjaxCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                super.onSuccess(s);
                rr_pb.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(s);
                    if (object.getInt("code") == 100) {
                        JSONObject obj = object.getJSONObject("userinfo");
                        int uid = obj.getInt("uid");
                        String passwd = obj.getString("passwd");
                        String sex = obj.getString("sex");
                        String email = obj.getString("email");
                        String name = obj.getString("name");
                        String data = obj.getString("date");
                        String intro = obj.getString("intro");
                        User user = new User(uid, name, data, email, intro, sex, passwd);
                        mUserDb.insert(user);
                        ConfigInfo.user = user;
                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                        finish();
                    } else {

                        Toast.makeText(WelcomeActivity.this, object.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    //Toast.makeText(WelcomeActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                rr_pb.setVisibility(View.GONE);
                Toast.makeText(WelcomeActivity.this,getResources().getString(R.string.connection_timeout) , Toast.LENGTH_SHORT).show();
            }
        });
    }
}

