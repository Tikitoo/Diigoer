package com.tikitoo.diigo.app2.activity;

import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.tikitoo.diigo.app2.R;
import com.tikitoo.diigo.app2.bean.DiigoBookMarks;
import com.tikitoo.diigo.app2.fragment.MainFragment;
import com.tikitoo.diigo.app2.fragment.SettingsFragment;
import com.tikitoo.diigo.app2.fragment.TagsListFragment;
import com.tikitoo.diigo.app2.util.DiigoClient;
import com.tikitoo.diigo.app2.util.DiigoUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * DiigoDemo
 *   实现一个Diigo（一个社会化书签工具） 客户端
 * 框架
 *   * *
 * Http 加载使用* 
 * 布局文件来自CustomListView
 * * 
 */
public class MainActivity extends ActionBarActivity {
    private static final String TAG = MainActivity.class.getName();

    private EditText urlEditText;
    private SearchView searchView;

    /** 获取工具类对象 */
    public DiigoUtil diigoUtil;

    DrawerLayout mDrawerLayout;
    ListView mDrawerList;
    ActionBarDrawerToggle mDrawerToggle;
    String mTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_02);

        // 初始化视图
        initView();
        diigoUtil = new DiigoUtil(getApplicationContext());

        handleIntent(getIntent());


        // Navigation Drawer
        mTitle = (String) getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.color.button_material_dark));

        mDrawerList = (ListView) findViewById(R.id.drawer_list);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_menu_white,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                actionBar.setTitle(mTitle);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                actionBar.setTitle("Select a list itm");
                invalidateOptionsMenu();
            }
        };
        
        // 把Tag 添加到侧滑栏中
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        // 为侧滑栏的ListView 设置适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.drawer_list,
                getResources().getStringArray(R.array.list_item)
                // diigoUtil.addAllTagsToDrawerList()
        );
        mDrawerList.setAdapter(adapter);

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        // 为侧滑栏的ListView 设置点击监听器
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] list_items = getResources().getStringArray(R.array.list_item);
                mTitle = list_items[position];

                // 点击Tag，跳转到相应的Fragment
                MainFragment mainFragment = new MainFragment(getApplicationContext());
                Bundle data = new Bundle();
                data.putInt("position", position);
                mainFragment.setArguments(data);

                FragmentManager fManager = getFragmentManager();
                fManager.beginTransaction().replace(R.id.content_frame, mainFragment).commit();

                mDrawerLayout.closeDrawer(mDrawerList);
            }
        });
        

    }
    
    private void initView() {
        MainFragment mainFragment = new MainFragment(getApplicationContext());
        Bundle data = new Bundle();
        data.putInt("position", 5);
        mainFragment.setArguments(data);

        FragmentManager fManager = getFragmentManager();
        fManager.beginTransaction().replace(R.id.content_frame, mainFragment).commit();


    }

    /**
     * ActionBar 搜索*
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(getIntent());
    }

    /**
     * 获取搜索框输入的内容 
     * @param intent
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // 获取输入框的内容
            String query = intent.getStringExtra(SearchManager.QUERY);
            /*Intent intent1 = new Intent(this, MainActivity.class);
            startActivity(intent1);*/
            
            TagsListFragment tagsListFragment = new TagsListFragment(this);
            Bundle data = new Bundle();
            // data.putInt("position", 3);
            data.putString("tag", query);
            data.putInt("position", 10000);

            tagsListFragment.setArguments(data);

            FragmentManager fManager = getFragmentManager();
            fManager.beginTransaction().replace(R.id.content_frame, tagsListFragment).commit();
            Toast.makeText(this, "searchQuery: " + query, Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        

        // 配置SearchView
        SearchManager searchManager
                = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView
                = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();
        FragmentManager fManager = getFragmentManager();

        // action Listener begin
        switch (id) {
            case R.id.action_search:
                handleIntent(getIntent());
                return true;
            /*case R.id.action_logout:
                new DiigoUtil(this).logoutToPreference();
                logoutLoginActivity();
                return true;
            */
            case R.id.action_settings:
                SettingsFragment settingsFragment = new SettingsFragment(this);
                fManager.beginTransaction().replace(R.id.content_frame, settingsFragment).commit();
                return true;
            // 同步获取All Tags，首先判断缓存是否存在
            case R.id.action_sync:
                /*if (new DiigoUtil(this).isNetworkAvailable()) {
                
                } else {
                    Toast.makeText(this, "Network Error" , Toast.LENGTH_SHORT).show();
                }*/
                if (DiigoUtil.cacheIsNull(DiigoUtil.DIIGO_BOOKMARKS)) {
                    onActionSyncFirst();
                } else {
                    if (!DiigoUtil.cacheIsNull(DiigoUtil.CACHE_MARKS_FILES)) {
                        uploadNoSyncMarks();
                        removeSyncMarks();
                    }
                    onActionSync();
                }

                /*Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                */Toast.makeText(getBaseContext(), "Sync ...", Toast.LENGTH_SHORT).show();

                return true;
            case R.id.action_add:
                Intent addIntent = new Intent(this, AddDiigoMarkActivity.class);
                startActivity(addIntent);
                return true;
            default:
                break;
            // action Listener end
        } // switch end
        return super.onOptionsItemSelected(item);
    }

    private void onActionSyncFirst() {
        DiigoBookMarks marks = new DiigoBookMarks();
        marks.setUser(new DiigoUtil(this).getUserPreference().get(0));
        
        marks.setFilter("all");
        marks.setCount(100);
        marks.setStart(0);

        // 获取服务器上的数据（遍历）
        new MainFragment(this).fetchDiigoBookMarks(marks, marks.getCount());


    }

    private void onActionSync() {
        // 将服务器的数据同步到客户端
        DiigoBookMarks marks = new DiigoBookMarks();
        marks.setUser(new DiigoUtil(this).getUserPreference().get(0));
        marks.setFilter("all");
        marks.setCount(30);
        marks.setStart(0);


        // 获取服务器上的数据（遍历）
        new MainFragment(this).fetchNewDiigoBookMarks(marks, marks.getCount());

    }

    /**
     * 将客户端的数据同步到服务器
     * 
     */
    private void uploadNoSyncMarks() {
        String cacheStr = DiigoUtil.readMarksFromFile(DiigoUtil.CACHE_MARKS_FILES);
        DiigoBookMarks cacheMarks = new DiigoBookMarks();
        try {
            JSONArray cacheData = new JSONArray(cacheStr);
            List<DiigoBookMarks> marksList = DiigoBookMarks.fromJson(cacheData);
            for (int i = 0; i < marksList.size(); i++) {
                cacheMarks = marksList.get(i);
                postCacheMarks(cacheMarks);
            }
            Log.d(TAG, "add Cache Marks: " + cacheData.toString() );

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * 将本地为上传的书签删除（当书签上传服务器时）
     */
    private void removeSyncMarks() {
        DiigoUtil.writeMarksToFile(null, DiigoUtil.CACHE_MARKS_FILES);
    }
    
    private void postCacheMarks(DiigoBookMarks marks) {
        DiigoClient client = new DiigoClient(this);
        
        client.saveDiigoBookMarks(marks, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "onSuccess response Object: " + response.toString());
                // 添加成功，返回主页
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                String respArray = response.toString();
                Log.d(TAG, "onSuccess Array: " + respArray.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                String respString = responseString.toString();
                Log.d(TAG, "onFailure response String: " + respString);
            }
        });
        
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_login).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * 按返回键退出*
     */
    /*@Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(Intent.ACTION_MAIN);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(setIntent);
    }*/

    public void logoutLoginActivity() {
        SharedPreferences prefs = getSharedPreferences(DiigoUtil.LOGIN_STATUS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(DiigoUtil.LOGIN_STATUS_YES, "logout");
        editor.commit();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();

        // startActivity(intent);

    }


}
