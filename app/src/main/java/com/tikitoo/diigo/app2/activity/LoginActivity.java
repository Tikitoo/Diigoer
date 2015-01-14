package com.tikitoo.diigo.app2.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.tikitoo.diigo.app2.R;
import com.tikitoo.diigo.app2.bean.DiigoBookMarks;
import com.tikitoo.diigo.app2.bean.DiigoUserBean;
import com.tikitoo.diigo.app2.util.DiigoClient;
import com.tikitoo.diigo.app2.util.DiigoUtil;
import com.tikitoo.diigo.app2.util.InputUtil;

import org.apache.http.Header;
import org.json.JSONArray;

/**
 * Created by tikitoo on 1/7/15.
 */
public class LoginActivity extends ActionBarActivity{
    public static final String TAG = LoginActivity.class.getName();
    private final String MARKS_FILTER_PRIVATE = "private";
    private final int MARKS_COUNT_1= 1;
    private final String LOGIN_STATUS = "login-status";
    private final String LOGIN_STATUS_YES = "login-status-yes";

    private boolean flag;
    static String username, password;

    private DiigoClient client;
    private DiigoUserBean userBean;
    private DiigoBookMarks marks;
    private EditText userNameEt, passWordEt;
    private Button submitBtn;

    Context mCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);
        
        mCtx = getBaseContext();

        // 如果登录成功，则跳转到MainActivity
        startMainActivity();

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        userNameEt = (EditText) findViewById(R.id.login_username_edit_text);
        passWordEt = (EditText) findViewById(R.id.login_password_edit_text);
        submitBtn = (Button) findViewById(R.id.login_submit_button);

        // 登录按钮验证
        submitBtn.setOnClickListener(new MyLoginBtnListener());

    }
    
    class MyLoginBtnListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            username = userNameEt.getText().toString();
            password = passWordEt.getText().toString();

            // Toast.makeText(mCtx.mCtx, "username:password = " + username + " : " + password, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "username:password = " + username + " : " + password);

            // 判断网络连接
            if (new InputUtil(mCtx).isNotNull(username, password) && new DiigoUtil(mCtx).isNetworkAvailable()) {
                // 登录验证
                final ProgressDialog pDialog = new ProgressDialog(LoginActivity.this);
                pDialog.setMessage("Loading...");
                pDialog.show();
                if (loginAuth()) {
                    pDialog.hide();

                    Log.d(TAG, "username: " + username + "; password: " + password);
                    Toast.makeText(mCtx, "Login Successful", Toast.LENGTH_SHORT).show();
                    
                    Intent intent = new Intent(mCtx, MainActivity.class);
                    startActivity(intent);
                    finish();


                    userBean = new DiigoUserBean();
                    userBean.setUsername(username);
                    userBean.setPassword(password);

                    // 登录成功，将登录信息保存到SharedPreference
                    new DiigoUtil(mCtx).loginToPreference(userBean);
                    new DiigoUtil(mCtx).setLoginStatus();
                        /*Fragment mainFragment = new MainFragment(mCtx);
                        Bundle bundle = new Bundle();
                        bundle.putInt("position", 1);
                        mainFragment.setArguments(bundle);

                        FragmentManager fManager = getFragmentManager();
                        FragmentTransaction transaction = fManager.beginTransaction();
                        transaction.replace(R.id.content_frame, mainFragment);
                        transaction.commit();
                        */

                } else {
                    pDialog.hide();
                    
                }
            } else {
                Toast.makeText(mCtx, "isNetworkAvailable Error", Toast.LENGTH_SHORT).show();


            }
        }// OnClick end
    }

    /**
     * 登录验证
     * @return 登录状态
     */
    public boolean loginAuth() {
        userBean = new DiigoUserBean();
        userBean.setUsername(username);
        userBean.setPassword(password);

        marks = new DiigoBookMarks();
        marks.setUser(userBean.getUsername());
        marks.setFilter(MARKS_FILTER_PRIVATE);
        marks.setCount(MARKS_COUNT_1);

        

        client = new DiigoClient();
        client.getDiigoLoginAuth(userBean, marks, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG, "loginAuth() onSuccess statusCode = " + statusCode);
                Log.d(TAG, "jsonObj = " + response.toString());
                flag = true;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(mCtx, "onFailure Request Error", Toast.LENGTH_SHORT).show();

                Log.d(TAG, "loginAuth() onFailure statusCode = " + statusCode + "Error Message" + throwable.getMessage());
                flag = false;
            }
        });
        return flag;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            Toast.makeText(mCtx, "onActivityResult Successful", Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * 设置登录成功状态
     */
    public void startMainActivity() {
        SharedPreferences prefs = getSharedPreferences(LOGIN_STATUS, Context.MODE_PRIVATE);
        // 如果没有网络，可以免去登录来测试
        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mCtx);
        /*SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LOGIN_STATUS_YES, "login");
        editor.commit();
        */String loginStatus = prefs.getString(LOGIN_STATUS_YES, null);

        if (loginStatus == null || loginStatus.equals("logout")) {

        } else if(loginStatus.equals("login")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            // finishActivity(0);
        }
    }

}
