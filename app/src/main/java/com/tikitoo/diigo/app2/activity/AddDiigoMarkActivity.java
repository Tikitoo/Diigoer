package com.tikitoo.diigo.app2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.tikitoo.diigo.app2.R;
import com.tikitoo.diigo.app2.bean.DiigoBookMarks;
import com.tikitoo.diigo.app2.util.DiigoClient;
import com.tikitoo.diigo.app2.util.DiigoUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by tikitoo on 1/7/15.
 */
public class AddDiigoMarkActivity extends ActionBarActivity {

    public static final String TAG = AddDiigoMarkActivity.class.getName();
    private EditText addUrlEt, addTitleEt, addDescEt;
    private CheckBox addSharedBox, addReadLaterBox;
    private AutoCompleteTextView tagsAutoTv;
    private Button addMarksBtn;

    private ArrayAdapter<String> adapter;
    private DiigoClient client;
    private DiigoBookMarks marks;
    private String[] COUNTRIES;
    private Intent shareIntent;
    private String sharedText;
    
    String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_add_mark);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.button_material_dark));


        List<String> singleTagsList = new DiigoUtil(this).getTagToPreference();
        COUNTRIES = new String[singleTagsList.size()];
        for (int i = 0; i < singleTagsList.size(); i++) {
            COUNTRIES[i] = singleTagsList.get(i);
        }

        adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_dropdown_item_1line, 
                COUNTRIES);
        tagsAutoTv = (AutoCompleteTextView) findViewById(R.id.add_tags_edit_text);
        tagsAutoTv.setAdapter(adapter);
        tagsAutoTv.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                
                return false;
            }
        });


        getOtherAppsData();

        addUrlEt = (EditText) findViewById(R.id.add_url_edit_text);
        addTitleEt = (EditText) findViewById(R.id.add_title_edit_text);
        // tagsAutoTv = (AutoCompleteTextView) findViewById(R.id.add_tags_edit_text);
        addDescEt = (EditText) findViewById(R.id.add_desc_edit_text);
        addSharedBox = (CheckBox) findViewById(R.id.shared_check_box);
        addReadLaterBox = (CheckBox) findViewById(R.id.read_later_check_box);
        addMarksBtn = (Button) findViewById(R.id.add_marks_button);
        
        addMarksBtn.setOnClickListener(new MyAddMarksListener());

        
        getMainActivity();
    }

    /**
     * 处理其他应用的数据 
     */
    public void getOtherAppsData() {
        shareIntent = getIntent();
        String action = shareIntent.getAction();
        String type = shareIntent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(shareIntent); // 处理文本
            }
        }  else {

        }
    }

    
    void handleSendText(Intent intent) {
        sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            Toast.makeText(this, "sharedText: " + sharedText, Toast.LENGTH_SHORT).show();
            new Thread(new MyThread()).start();
            // String title = getTitleByUrl(sharedText);
            // addTitleEt.setText(title);
        }
    }
    
    class MyThread implements Runnable {
        @Override
        public void run() {
            addUrlEt.setText(sharedText);
        }
    }

    void getMainActivity() {
        Intent mainIntent = getIntent();
        String share = mainIntent.getStringExtra("action");
        if ("share".equals(share)) {
            Log.d(TAG, "from MainActivity");
        }
    }

    public void onAddTest(View view) {
        marks = new DiigoBookMarks();
        marks.setUrl(getViewString(addUrlEt));
        marks.setTitle(getViewString(addTitleEt));
        marks.setTags(getViewString(tagsAutoTv));
        marks.setDesc(getViewString(addDescEt));
        marks.setShared(getViewString(addSharedBox));
        marks.setReadLater(getViewString(addReadLaterBox));
        Log.d(TAG, "onAddTest Complete");
    }

    // 添加一条数据
    class MyAddMarksListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 判断是否有网络，如果有网络直接提交服务器；如果没有网络，则添加本地缓存
            if (new DiigoUtil(AddDiigoMarkActivity.this).isNetworkAvailable()) {
                DiigoBookMarks marks1 = addMarks();
                onAddMarksToServer(marks1);
            } else {
                onAddMarksToFile();
            }

            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            Toast.makeText(getBaseContext(), "add Cache Marks Sucessful", Toast.LENGTH_SHORT).show();
            
            
        }
    }

    /**
     * 将DiigoBookMarks 封装成JsonObj 对象 
     * @param marks
     * @return
     */
    public JSONObject marksToJson(DiigoBookMarks marks) {
        JSONObject singleMarks = new JSONObject();
        try {
            singleMarks.put("url", marks.getUrl());
            singleMarks.put("title", marks.getTitle());
            singleMarks.put("tags", marks.getTags());
            singleMarks.put("desc", marks.getDesc());
            singleMarks.put("shared", marks.getShared());
            singleMarks.put("readlater", marks.getReadLater());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return singleMarks;
    }
    
    public DiigoBookMarks addMarks() {
        marks = new DiigoBookMarks();
        marks.setUrl(getViewString(addUrlEt));
        marks.setTitle(getViewString(addTitleEt));
        marks.setTags(getViewString(tagsAutoTv));
        marks.setDesc(getViewString(addDescEt));
        marks.setShared(getViewString(addSharedBox));
        marks.setReadLater(getViewString(addReadLaterBox));
        return marks;

    }
    
    /**
     * 如果网络没有连接，则添加书签到本地文件 
     */
    private  void onAddMarksToFile() {
        String allCacheMarks = null;

        // 将每条数据封装成JsonObj，存入JsonArr
        JSONArray allCacheJsonArr = new JSONArray();

        // 先将文件有的数据读取出来，然后在写入
        String cacheDdata = DiigoUtil.readMarksFromFile(DiigoUtil.CACHE_MARKS_FILES);
        if (cacheDdata != null) {
            try {
                JSONArray cacheJsonArr = new JSONArray(cacheDdata);
                for (int i = 0; i < cacheJsonArr.length(); i++) {
                    JSONObject cacheJsonObj = (JSONObject) cacheJsonArr.get(i);
                    allCacheJsonArr.put(cacheJsonObj);
                    Log.d(TAG, "allCacheJsonArr put readFile: " + cacheJsonObj.toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        marks = new DiigoBookMarks();
        marks.setUrl(getViewString(addUrlEt));
        marks.setTitle(getViewString(addTitleEt));
        marks.setTags(getViewString(tagsAutoTv));
        marks.setDesc(getViewString(addDescEt));
        marks.setShared(getViewString(addSharedBox));
        marks.setReadLater(getViewString(addReadLaterBox));

        JSONObject singleMarks = marksToJson(marks);
        allCacheJsonArr.put(singleMarks);
        Log.d(TAG, "allCacheJsonArr put add: " + singleMarks.toString());

        allCacheMarks = allCacheJsonArr.toString();
        DiigoUtil.writeMarksToFile(allCacheMarks, DiigoUtil.CACHE_MARKS_FILES);

        // 添加成功，返回主页
        /*Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
        Toast.makeText(this, "add Cache Marks Sucessful", Toast.LENGTH_SHORT).show();*/
    }

    /**
     * 如果网络畅通，则添加书签到网络* 
     */
    private void onAddMarksToServer(DiigoBookMarks marks) {
        client = new DiigoClient(this);

        marks = new DiigoBookMarks();
        marks.setUrl(getViewString(addUrlEt));
        marks.setTitle(getViewString(addTitleEt));
        marks.setTags(getViewString(tagsAutoTv));
        marks.setDesc(getViewString(addDescEt));
        marks.setShared(getViewString(addSharedBox));
        marks.setReadLater(getViewString(addReadLaterBox));

        client.saveDiigoBookMarks(marks, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "onSuccess response Object: " + response);
                // 添加成功，返回主页
                /*Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);*/
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                String respArray = response.toString();
                Log.d(TAG, "onSuccess Array: " + respArray);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                String respString = responseString.toString();
                Log.d(TAG, "onFailure response String: " + respString);
            }
        });
    }

    private String getViewString(TextView view) {
        String textString = view.getText().toString();
        Log.d(TAG, "TextView Value: " + textString);
        return textString;
    }

    private String getViewString(CheckBox box) {
        String status;
        boolean flag = box.isSelected();
        if (flag) {
            status = "yes";
        } else {
            status = "no";
        }
        Log.d(TAG, "CheckBox Status: " + flag);
        Log.d(TAG, "CheckBox Status: " + status);
        return status;
    }

    /**
     * 点击Url 右边的Button，来获取标题 
     * @param view
     */
    public void getTitleByUrl(View view) {
        String url = addUrlEt.getText().toString();
        url = urlTrim(url);
        if (url == null) {
            Toast.makeText(this, "sharedText: Url is Null ", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "sharedText: " + url, Toast.LENGTH_SHORT).show();

        new DiigoClient(this).getTitleByUrl(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                String htmlBody = response.toString();
                int start = htmlBody.indexOf("<title>") + "<title>".length();
                int end = htmlBody.indexOf("</title>");
                String title = htmlBody.substring(start, end);
                String title_len = htmlBody.substring(10, end);

                Log.d(TAG, "getTitleByUrl Title: " + title);
                Log.d(TAG, "getTitleByUrl Title Leng: " + title_len.length());
                Log.d(TAG, "onSuccess statusCode: " + statusCode );
                addTitleEt.setText(title);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Log.d(TAG, "onSuccess statusCode: " + statusCode );

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                Log.d(TAG, "onFailure: " + statusCode + "responseString: " + responseString);
                String htmlBody = responseString.toString();
                int start = htmlBody.indexOf("<title>") + "<title>".length();
                int end = htmlBody.indexOf("</title>");
                String title2 = htmlBody.substring(start, end);
                String title_len = htmlBody.substring(10, end);

                Log.d(TAG, "getTitleByUrl Title" + title2);
                Log.d(TAG, "getTitleByUrl Title Leng: " + title_len.length());
                Log.d(TAG, "onFailure statusCode: " + statusCode );

                addTitleEt.setText(title2);

            }
        });
        addTitleEt.setText(title);
    }


    /**
     * 取出Url 空格 
     * @param url
     * @return
     */
    public String urlTrim(String url) {

        boolean flag = Patterns.WEB_URL.matcher(url).matches();
        if (!flag) {
            Toast.makeText(this, "Url is Null ", Toast.LENGTH_SHORT).show();
            return null;
        }

        return url;
    }

}
