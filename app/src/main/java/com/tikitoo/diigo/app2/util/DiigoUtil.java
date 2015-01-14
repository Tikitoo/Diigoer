package com.tikitoo.diigo.app2.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tikitoo.diigo.app2.R;
import com.tikitoo.diigo.app2.activity.LoginActivity;
import com.tikitoo.diigo.app2.activity.MainActivity;
import com.tikitoo.diigo.app2.bean.DiigoBookMarks;
import com.tikitoo.diigo.app2.bean.DiigoUserBean;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by tikitoo on 1/6/15.
 */
public class DiigoUtil {
    private static final String TAG = DiigoUtil.class.getName();
    private static Map<String, String> userMap;
    private static Context mCtx;

    public static final String DIIGO_BOOKMARKS_TAGS = "diigo-bookmarks-tag";
    public static final String DIIGO_BOOKMARKS = "diigo-bookmarks.json";
    public static final String CACHE_MARKS_FILES = "cache-marks-files";

    static final String LOGIN_USERNAME = "login-username";
    static final String LOGIN_PASSWORD = "login-password";
    public static final String LOGIN_STATUS = "login-status";
    public static final String LOGIN_STATUS_YES = "login-status-yes";

    /**
     * 构造方法
     * @param ctx  上下文
     */
    public DiigoUtil(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * 将所以书签Tag 保存SharedPreferences
     * @param map 传入所有书签Tag
     */
    public void setTagToPreference(Map<String, String> map, String PREFS_FILE_NAME) {

        SharedPreferences setData = mCtx.getApplicationContext().getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = setData.edit();
        for (String key : map.keySet()) {
            String value = map.get(key);
            editor.putString(key, value);

            // 记得提交
            editor.commit();
            Log.d(TAG, "set Tag key-value" + key + "-" + value);
        }

    }

    /**
     * 从 SharedPreferences 中获取所以书签Tag
     * @return 返回List 列表
     */
    public List<String> getTagToPreference() {
        List<String> list = new ArrayList<String>();
        SharedPreferences getData = mCtx.getApplicationContext().getSharedPreferences(DIIGO_BOOKMARKS_TAGS, Context.MODE_PRIVATE);
        Map map = getData.getAll();

        // SharedPreferences.Editor data = getData.edit();
        for (Object key : map.keySet()) {

            String value = (String) map.get(key);
            /*if (value.equals("Tikitoo") || value.equals("1055102871")) {
                data.remove((String) key);
                Log.d(TAG, "remove successful key: " + key );
            }
            data.commit();
            */
            
            list.add(value);
            Log.d(TAG, "get Tag key-value" + key + "-" + value);
        }

        return list;
    }

    public List<String> getUserPreference() {
        List<String> list = new ArrayList<String>();
        SharedPreferences getData = mCtx.getApplicationContext().getSharedPreferences(LOGIN_STATUS, Context.MODE_PRIVATE);
        Map map = getData.getAll();

        String username = (String) map.get(LOGIN_USERNAME);
        String password = (String) map.get(LOGIN_PASSWORD);
        Log.d(TAG, "getUserPreference: " + username + password);
        list.add(username);
        list.add(password);

        return list;
    }

    /**
     * 移除Tags 列表Prefs
     */
    public void removePrefsCache() {
        SharedPreferences getData = mCtx.getSharedPreferences(DiigoUtil.DIIGO_BOOKMARKS_TAGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = getData.edit();
        editor.clear();
        editor.commit();
        Log.d(TAG, "removePrefsCache Successful");
    }

    // files
    public static boolean removeTagsListCache(File cacheDir) {
        boolean flag = false;
        File[] filesList = cacheDir.listFiles();
        for (int i = 0; i < filesList.length; i++) {
            if (filesList[i].isDirectory() && filesList[i].toString().equals("files")) {
                removeTagsListCache(filesList[i]);
            } else {
                filesList[i].delete();
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 将map 集合获取key-value，返回String 
     * @param map
     * @return key-value
     */
    public static String mapToString(Map<String, String> map) {
        String key_value = null;
        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();
            String value = entry.getValue();
            key_value = key + "," + value;

        }
        return key_value;
    }


    /**
     * 将所以Tag 添加到String[]，左边侧滑栏
     * 从string-array 中获取数据，再将获取的tag，存入新的String[]
     * @return
     */
    public String[] addAllTagsToDrawerList() {
        String[] list_items = mCtx.getResources().getStringArray(R.array.list_item);

        List<String> strArrList = new ArrayList<String>();
        for (int i = 0; i < list_items.length; i++) {
            strArrList.add(list_items[i]);
        }

        List<String> tagsList = new DiigoUtil(mCtx).getTagToPreference();
        strArrList.addAll(tagsList);


        // List<String> tagsAllSort = tagsListSort(strArrList);

        String[] stringArray = strArrList.toArray(new String[strArrList.size()]);
        // Log.d(TAG, "" + "All Tags+ stringArray: " + stringArray.toString());
        // Log.d(TAG, "" + "All Tags+ stringArray: " + strArrList.toString());
        

        return stringArray;

    }

    /**
     * 查看书签详细，将书签的详细数据传入下一个Activity，返回Bundle 对象
     * @param marks 书签对象
     * @return
     */
    public Bundle getBundleByMarks(DiigoBookMarks marks) {

        String urlString = marks.getUrl();
        String titleString= marks.getTitle();
        String tagsString = marks.getTags();
        String descString = marks.getDesc();
        String commentsString = marks.getAnnotations();
        String create_atString = marks.getCreate_at();
        String update_atString = marks.getUpdate_at();
        Log.d(TAG, "urlString: " + urlString + titleString + tagsString + descString + commentsString + create_atString + update_atString);

        Bundle bundle = new Bundle();
        bundle.putString("url", urlString);
        bundle.putString("title", titleString);
        bundle.putString("tags", tagsString);
        bundle.putString("desc", descString);
        bundle.putString("comments", commentsString);
        bundle.putString("create_at", create_atString);
        bundle.putString("update_at", update_atString);
        return bundle;

    }

    /**
     * 判断网络是否连接
     * @return
     */
    public boolean isNetworkAvailable() {

        ConnectivityManager cm =
                (ConnectivityManager) mCtx.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // 如果不为Null，且连接成功，则返回True
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(mCtx.getApplicationContext(), "Network Error, Please Check Network", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    /**
     * 给定一个String 数组，返回一个新的数组
     * @param list_items
     * @return
     */
    public String[] stringArrDell(String[] list_items) {

        List<String> strArrList = new ArrayList<String>();
        for (int i = 0; i < list_items.length; i++) {
            strArrList.add(list_items[i]);
        }

        List<String> tagsList = getTagToPreference();
        strArrList.addAll(tagsList);

        String[] stringArray = strArrList.toArray(new String[strArrList.size()]);
        // Log.d(TAG, "" + "stringArray: " + stringArray.toString());
        // Log.d(TAG, "" + "strArrList: " + strArrList.toString());

        return stringArray;

    }

    /**
     * 从Diigo 服务器中读取数据存入文件* 
     * @param data
     */
    public static void writeMarksToFile(String data) {
        // "/data/data/com.tikitoo.diigo.app2/files"
        File filesDir = mCtx.getFilesDir();
        File markFile = new File(filesDir, DIIGO_BOOKMARKS);
        try {
            FileUtils.writeStringToFile(markFile, data);

            Log.d(TAG, "writeStringToFile Successful");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static String readMarksFromFile() {
        String allMarks = null;
        File filesDir = mCtx.getFilesDir();
        File markFile = new File(filesDir, DIIGO_BOOKMARKS);
        try {
            allMarks = FileUtils.readFileToString(markFile);
            Log.d(TAG, "readFileToString Successful");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allMarks;
    }

    /**
     * 将书签缓存写入文件* 
     * @param data
     * @param cacageFileName
     */
    public static void writeMarksToFile(String data, String cacageFileName) {
        // "/data/data/com.tikitoo.diigo.app2/files"
        File filesDir = mCtx.getFilesDir();
        File markFile = new File(filesDir, cacageFileName);
        try {
                FileUtils.writeStringToFile(markFile, data);
            Log.d(TAG, "writeMarksToFile Cache  Successful" + data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取文件中缓存的书签，准备上传服务器
     * @param cacageFileName
     * @return
     */
    public static String readMarksFromFile(String cacageFileName) {
        String allMarks = null;
        File filesDir = mCtx.getFilesDir();
        File markFile = new File(filesDir, cacageFileName);

        try {
            if (markFile.exists() && markFile != null) {
                allMarks = FileUtils.readFileToString(markFile);
            }
            Log.d(TAG, "readMarksFromFile Cache Successful" + allMarks);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allMarks;
    }

    /**
     * 将Url 处理之后，返回网址前缀
     * @param url
     * @return
     */
    public static String subUrl(String url) {
        if (url.contains("//")) {
            int start = url.indexOf("//") +"//".length();
            url = url.substring(start, url.length());
            if (url.contains("/")) {
                int end = url.indexOf("/", start);
                System.out.println(start + "" + end);
                return url.substring(start, end);
            } else {
                return url;
            }
        } else {
            int end = url.indexOf("/") + 1;
            return url.substring(0, end);
        }

    }

    /**
     * 根据文件名判断是否为空
     * @param fileName
     * @return
     */
    public static boolean cacheIsNull(String fileName) {
        boolean flag;
        String cacheStr = readMarksFromFile(fileName);
        if (cacheStr == null) {
            return true;
        } else {
            flag = false;
        }
        return flag;
    }

    /**
     * 登录后将帐号密码存入SharedPreference 
     */
    public void loginToPreference(DiigoUserBean userBean) {
        userMap = new HashMap<String, String>();
        userMap.put(LOGIN_USERNAME, userBean.getUsername());
        userMap.put(LOGIN_PASSWORD, userBean.getPassword());
        setTagToPreference(userMap, LOGIN_STATUS);
        Log.d(TAG, "loginToPreference");

    }

    /**
     * 退出登录后将帐号密码从SharedPreference 清空 
     */
    public void logoutToPreference() {
        userMap = new HashMap<String, String>();
        userMap.put(LOGIN_USERNAME, "");
        userMap.put(LOGIN_PASSWORD, "");
        setTagToPreference(userMap, LOGIN_STATUS);

    }

    /**
     * 设置登出状态
     */
    public void setLogoutStatus() {
        SharedPreferences prefs = mCtx.getSharedPreferences(LOGIN_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(DiigoUtil.LOGIN_STATUS_YES, "logout");
        editor.commit();
        Intent intent = new Intent(mCtx, LoginActivity.class);
        mCtx.startActivity(intent);

    }

    /**
     * 设置登录状态
     */
    public void setLoginStatus() {
        SharedPreferences prefs = mCtx.getSharedPreferences(LOGIN_STATUS, Context.MODE_PRIVATE);
        // SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mCtx);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(LOGIN_STATUS_YES, "login");
        editor.commit();
    }

    public void loginLoginActivity() {
        SharedPreferences prefs = mCtx.getSharedPreferences(LOGIN_STATUS, Context.MODE_PRIVATE);
        if (prefs.getString(LOGIN_STATUS_YES, null) != null) {
            Intent intent = new Intent(mCtx, MainActivity.class);
            mCtx.startActivity(intent);
            //finish();
        }
    }

}
