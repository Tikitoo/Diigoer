package com.tikitoo.diigo.app2.util;


import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.Base64;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tikitoo.diigo.app2.bean.DiigoBookMarks;
import com.tikitoo.diigo.app2.bean.DiigoUserBean;

import java.util.List;

/**
 * Diigo API 来获取书签列表
 * username/password 问题
 *   https://github.com/loopj/android-async-http/issues/113
 * Created by tikitoo on 1/6/15.
 */
public class DiigoClient {

    public static final String TAG = DiigoClient.class.toString();

    private static final String API_KEY = "1a36cc41cfb69d45";
    private static final String API_BASE_URL = "https://secure.diigo.com/api/v2/bookmarks";
    String title = "Default Title";
    private AsyncHttpClient client;
    private Context mCtx;

    public DiigoClient() {
        client = new AsyncHttpClient();

    }
    
    public DiigoClient(Context ctx) {
        mCtx = ctx;
        client = new AsyncHttpClient();
        
    }

    private String getApiUrl(String relativeUrl) {
        return API_BASE_URL + relativeUrl;

    }

    public void getDiigoBookMarks(DiigoBookMarks marks, JsonHttpResponseHandler handler) {
        String url = getApiUrl("");
        RequestParams params = new RequestParams();
        params.add("key", API_KEY);
        params.add("user", marks.getUser());
        params.add("sort", marks.getSort());
        
        params.add("start", marks.getStart() + "");
        params.add("tags", marks.getTags());
        params.add("count", marks.getCount() + "");
        params.add("filter", marks.getFilter());
        params.add("list", marks.getList());

        String encoded = loginBase64(loginSucc());
        client.addHeader("Authorization", "Basic " + encoded);

        client.get(url, params, handler);
    }
    
    public void saveDiigoBookMarks(DiigoBookMarks marks, JsonHttpResponseHandler handler) {
        String url = getApiUrl("");
        RequestParams params = new RequestParams();
        params.add("key", API_KEY);
        params.add("url", marks.getUrl());
        params.add("title", marks.getTitle());
        params.add("tags", marks.getTags());
        params.add("shared", marks.getShared());
        params.add("desc", marks.getDesc());
        params.add("readLater", marks.getReadLater());

        String encoded = loginBase64(loginSucc());
        client.addHeader("Authorization", "Basic " + encoded);

        client.post(url, params, handler);
        
    }

    public void getDiigoLoginAuth(DiigoUserBean userBean, DiigoBookMarks marks, JsonHttpResponseHandler handler) {
        String url = getApiUrl("");
        RequestParams params = new RequestParams();
        params.put("key", API_KEY);
        params.put("user", marks.getUser());
        params.put("filter", marks.getFilter());
        params.put("count", marks.getCount());

        String encoded = loginBase64(userBean);
        client.addHeader("Authorization", "Basic " + encoded);
        client.get(url, params, handler);

    }
    
    public DiigoUserBean loginSucc() {
        List<String> list = new DiigoUtil(mCtx).getUserPreference();
        DiigoUserBean userBean = new DiigoUserBean();
        
        Log.d(TAG, "list: " + list.toString());
        
        userBean.setUsername(list.get(0));
        userBean.setPassword(list.get(1));
        return userBean;
        
    }
    
    public String loginBase64(DiigoUserBean userBean) {
        String username = userBean.getUsername();
       String password = userBean.getPassword();
        String userPass = username + ":" + password;
        String encoded = new String(Base64.encodeToString(userPass.getBytes(), Base64.NO_WRAP));

        return encoded;

    }

    public String getTitleByUrl(String url, JsonHttpResponseHandler handler) {
        client.get(url, null, handler);
        return title;
    }

}

