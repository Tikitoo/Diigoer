package com.tikitoo.diigo.app2.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.tikitoo.diigo.app2.R;
import com.tikitoo.diigo.app2.activity.DetailActivity;
import com.tikitoo.diigo.app2.adapter.CustomListAdapter;
import com.tikitoo.diigo.app2.bean.DiigoBookMarks;
import com.tikitoo.diigo.app2.util.DiigoClient;
import com.tikitoo.diigo.app2.util.DiigoUtil;
import com.tikitoo.diigo.app2.util.InputUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tikitoo on 12/27/14.
 */
public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getName();
    private View rootView;
    private ListView marksListView, allTagsListView;
    private List<DiigoBookMarks> marksList;
    private List<DiigoBookMarks> allTagsList;
    private List<String> singleTagsList;
    private CustomListAdapter marksAdapter;
    private ArrayAdapter allTagsAdapter;
    private DiigoClient client;
    DiigoUtil diigoUtil;
    DiigoBookMarks marks;
    static boolean flag;
    JSONArray readAllMarksJsonArr = new JSONArray();

    private Context mCtx;

    public MainFragment(Context ctx) {
        this.mCtx = ctx;
        diigoUtil = new DiigoUtil(ctx);
    }

    
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int position = getArguments().getInt("position");
        // Log.d(TAG, "position: " + position);
        
        String[] list_item = getResources().getStringArray(R.array.list_item);
        // String[] list_item = diigoUtil.addAllTagsToDrawerList();
        

        // 读取Tag 列表
        if (position == 6) {
            rootView = inflater.inflate(R.layout.fragment_all_bookmarks, container, false);
            allTagsListView = (ListView) rootView.findViewById(R.id.content_list_view);
            
            allTagsList = new ArrayList<DiigoBookMarks>();
            singleTagsList = new ArrayList<String>();
            
            singleTagsList = diigoUtil.getTagToPreference();
            singleTagsList = InputUtil.compareTagsList(singleTagsList);
            // allTagsList = new ArrayList<String>();
            allTagsAdapter = new ArrayAdapter(mCtx, R.layout.drawer_list, singleTagsList);
            allTagsListView.setAdapter(allTagsAdapter);

            allTagsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    TagsListFragment tagsListFragment = new TagsListFragment(mCtx.getApplicationContext());
                    List<String> list = singleTagsList;


                    String[] tags = new String[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        tags[i] = list.get(i);
                    }

                    Bundle data = new Bundle();
                    data.putInt("position", position);
                    data.putStringArray("tagsList", tags);

                    tagsListFragment.setArguments(data);
                    Log.d(TAG, "singleTagsList: " + list.toString());

                    FragmentManager fManager = getFragmentManager();
                    fManager.beginTransaction().replace(R.id.content_frame, tagsListFragment).commit();

                    
                    Toast.makeText(mCtx, "Tags is onItemClick" + tags[position], Toast.LENGTH_SHORT).show();

                }
            });

        } else {
            rootView = inflater.inflate(R.layout.fragment_all_bookmarks, container, false);
            // 如果点击的是All Bookmarks 则跳转掉另一个Fragment

            marksListView= (ListView) rootView.findViewById(R.id.content_list_view);
            
            marksList = new ArrayList<DiigoBookMarks>();
            
            marksAdapter = new CustomListAdapter(mCtx.getApplicationContext(), marksList);
            marksListView.setAdapter(marksAdapter);

            if (!DiigoUtil.cacheIsNull(DiigoUtil.DIIGO_BOOKMARKS)) {

                switch (position) {
                    // public marks，filter=public
                    case 0:
                        readPublicMarks();
                        break;
                    // private marks，filter=private
                    case 1:
                        // marks.setFilter("private");
                        readPrivateMarks();
                        break;
                    // ReadLater 需要遍历，JsonArray[i].getString(“readlater").equals("yes")
                    case 2:
                        readLaterMarks();
                        break;
                    // Recent Add，sort=2,updated_at
                    case 3:
                        // marks.setSort("2");
                        // updated_at
                        readRecentAddMarks();
                        break;
                    // No Sync，未同步的，本地缓存的，从文件读取
                    case 4:
                        // marksAdapter.add(new DiigoBookMarks("No Not Sync", " ", " "));
                        readCacheMarks();
                        break;
                    // All marks，filter=all/*
                    case 5:
                        // marks.setFilter("all");
                        readAllMarks();
                        break;
                    // All Tags，遍历并排序
                    case 6:

                        break;
                    default:
                        break;
                }
            } else {
                marksAdapter.add(new DiigoBookMarks("No Sync", " ", " "));
            }

            marksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // DiigoBookMarks marks = marksList.get(position);
                    // 需要将数据传递到下一个Activity 中（getItem(position)）
                    Bundle bundle = diigoUtil.getBundleByMarks(marksAdapter.getItem(position));
                    Intent intent = new Intent();

                    intent.putExtras(bundle);
                    intent.setClass(getActivity(), DetailActivity.class);
                    startActivity(intent);
                }
            });

        }
        return rootView;
    }

    private void readCacheMarks() {

        if (!DiigoUtil.cacheIsNull(DiigoUtil.CACHE_MARKS_FILES)) {
            String cacheAllMarks = DiigoUtil.readMarksFromFile(DiigoUtil.CACHE_MARKS_FILES);
            JSONArray response = null;
            try {
                response = new JSONArray(cacheAllMarks);
                Log.d(TAG, "response Size: " + response.length());
                Log.d(TAG, "AllMarks: " + cacheAllMarks);

                List<DiigoBookMarks> markses = DiigoBookMarks.fromJson(response);
                for (DiigoBookMarks mark : markses) {
                    marksAdapter.add(mark);
                }
                Log.d(TAG, "readAllMarks meJsonArr Size: " + response.length());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            marksAdapter.add(new DiigoBookMarks("No Not Sync", " ", " "));
        }
    }

    private void readRecentAddMarks() {

        Log.d(TAG, "readRecentAddMarks Method");

        String curDate = InputUtil.getNowDate();
        String allMarks = DiigoUtil.readMarksFromFile();

        // 根据Tag，返回遍历后的JsonArr
        JSONArray meJsonArr = new JSONArray();

        JSONArray response = null;
        try {
            response = new JSONArray(allMarks);
            Log.d(TAG, "response Size: " + response.length());
            for (int i = 0; i < response.length(); i++) {

                JSONObject jsonObject = (JSONObject) response.get(i);
                String recentUpdate = jsonObject.getString("updated_at");
                String subDate = recentUpdate.substring(0, 19);
                // Log.d(TAG, "recent Add: " + recentUpdate + "subDate: " + subDate);
                Log.d(TAG, "curDate: " + curDate+ "; marksDate: " + subDate);

                int dateInt = curDate.compareTo(subDate);
                Log.d(TAG, "datInt" + dateInt);
                if (dateInt < 0) {
                    meJsonArr.put(jsonObject);

                }
            }
            List<DiigoBookMarks> markses = DiigoBookMarks.fromJson(meJsonArr);
            for (DiigoBookMarks mark : markses) {
                marksAdapter.add(mark);
                // 将获取的数据，复制给全局的DigoBookmarks 对象，传递到下一个Activity 中
                // this.marks = mark;
            }
            Log.d(TAG, "meJsonArr Size: " + meJsonArr.length());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void readAllMarks() {
        Log.d(TAG, "readAllMarks  Method");

        String allMarks = DiigoUtil.readMarksFromFile();

        // 根据Tag，返回遍历后的JsonArr
        // JSONArray meJsonArr = new JSONArray();

        JSONArray response = null;
        try {
            response = new JSONArray(allMarks);
            Log.d(TAG, "response Size: " + response.length());
            Log.d(TAG, "AllMarks: " + allMarks);

            List<DiigoBookMarks> markses = DiigoBookMarks.fromJson(response);
            // 
            // marksList = markses;
            for (DiigoBookMarks mark : markses) {
                marksAdapter.add(mark);
            }
            Log.d(TAG, "readAllMarks meJsonArr Size: " + response.length());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void readLaterMarks() {
        Log.d(TAG, "readLaterMarks Method");

        String allMarks = DiigoUtil.readMarksFromFile();

        // 根据Tag，返回遍历后的JsonArr
        JSONArray meJsonArr = new JSONArray();

        JSONArray response = null;
        try {
            response = new JSONArray(allMarks);
            Log.d(TAG, "response Size: " + response.length());
            for (int i = 0; i < response.length(); i++) {

                JSONObject jsonObject = (JSONObject) response.get(i);
                String readlater = jsonObject.getString("readlater");
                Log.d(TAG, "readLater: " + readlater);
                if ("yes".equals(readlater)) {
                    meJsonArr.put(jsonObject);
                }
            }
            List<DiigoBookMarks> markses = DiigoBookMarks.fromJson(meJsonArr);
            for (DiigoBookMarks mark : markses) {
                marksAdapter.add(mark);
            }
            Log.d(TAG, "meJsonArr Size: " + meJsonArr.length());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }    

    private void readPrivateMarks() {
        Log.d(TAG, "readPrivateMarks Method");

        String allMarks = DiigoUtil.readMarksFromFile();

        // 根据Tag，返回遍历后的JsonArr
        JSONArray meJsonArr = new JSONArray();

        JSONArray response = null;
        try {
            response = new JSONArray(allMarks);
            Log.d(TAG, "response Size: " + response.length());
            for (int i = 0; i < response.length(); i++) {

                JSONObject jsonObject = (JSONObject) response.get(i);
                String shared = jsonObject.getString("shared");
                Log.d(TAG, "shared: " + shared);
                if ("no".equals(shared)) {
                    meJsonArr.put(jsonObject);
                }
            }
            List<DiigoBookMarks> markses = DiigoBookMarks.fromJson(meJsonArr);
            for (DiigoBookMarks mark : markses) {
                marksAdapter.add(mark);
            }
            Log.d(TAG, "meJsonArr Size: " + meJsonArr.length());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void readPublicMarks() {
        Log.d(TAG, "readPublicMarks Method");
        // marks.setFilter("public");

        if (!DiigoUtil.cacheIsNull(DiigoUtil.DIIGO_BOOKMARKS)) {
            String allMarks = DiigoUtil.readMarksFromFile();


            // 根据Tag，返回遍历后的JsonArr
            JSONArray meJsonArr = new JSONArray();

            JSONArray response = null;
            try {
                response = new JSONArray(allMarks);
                Log.d(TAG, "response Size: " + response.length());
                for (int i = 0; i < response.length(); i++) {

                    JSONObject jsonObject = (JSONObject) response.get(i);
                    String shared = jsonObject.getString("shared");
                    if ("yes".equals(shared)) {
                        meJsonArr.put(jsonObject);
                    }
                }
                List<DiigoBookMarks> markses = DiigoBookMarks.fromJson(meJsonArr);
                for (DiigoBookMarks mark : markses) {
                    marksAdapter.add(mark);
                }
                Log.d(TAG, "readMarksFromFile  Size: " + meJsonArr.length());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            marksAdapter.add(new DiigoBookMarks("No Not Sync", " ", " "));
        }

        
    }

    /**
     * 更新所有书签，并获取所有Tag 
     */
    public void fetchDiigoBookMarks(final DiigoBookMarks marks, int Count) {

        // DiigoBookMarks marks = new DiigoBookMarks();
        final ProgressDialog pDialog = new ProgressDialog(mCtx);
        pDialog.setMessage("Loading...");
        pDialog.show();

        client = new DiigoClient(mCtx);
        client.getDiigoBookMarks(marks, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(mCtx.getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "statusCode: " + statusCode + "responseString: " + responseString);
                // pDialog.hide();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d(TAG, "statusCode: " + statusCode + "JSONArray errorResponse: " + errorResponse);
                // pDialog.hide();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "statusCode: " + statusCode + "; JSONObject errorResponse: " + errorResponse);
                // pDialog.hide();

            }


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                if (statusCode == 200) {
                    String resp = response.toString();
                    Log.i(TAG, "response: " + resp);

                    // 由于每次只能读取100 个书签，所以要遍历，读取所以书签
                    if (response.length() <= marks.getCount() && response.length() > 0) {


                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                
                                // 获取书签，并将书签存放在Pfers 中
                                String tags = jsonObject.getString("tags");
                                String[] tag = tags.split(",");
                                for (String s : tag) {
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put(DiigoUtil.DIIGO_BOOKMARKS_TAGS + "-" + s, s);
                                    diigoUtil.setTagToPreference(map, DiigoUtil.DIIGO_BOOKMARKS_TAGS);
                                }
                                
                                readAllMarksJsonArr.put(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }// for end
                        marks.setStart(marks.getStart() + marks.getCount());;
                        marks.setCount(marks.getCount());
                        fetchDiigoBookMarks(marks, marks.getCount());
                        Log.d(TAG, response.length() + "");
                        Log.d(TAG, "Marks Start" + marks.getStart());
                    } else if (response.length() == 0) {

                        Toast.makeText(mCtx.getApplicationContext(), "ReadLater complete", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "ReadAll complete");
                        // 数据读取完，将读取的数据写入文件
                        DiigoUtil.writeMarksToFile(readAllMarksJsonArr.toString());

                        Log.d(TAG, "readAllMarksJsonArr leng: " + readAllMarksJsonArr.length() + "");
                        Log.d(TAG, "readAllMarksJsonArr: " + readAllMarksJsonArr.toString() + "");
                    }
                    pDialog.hide();


                }// statusCode end
            }

        });

    }
    
    public void fetchNewDiigoBookMarks(final DiigoBookMarks marks, int Count) {

        // final Map<String, String> map = new HashMap<String, String>();
        // allTags = new ArrayList();

        // DiigoBookMarks marks = new DiigoBookMarks();
        final ProgressDialog pDialog = new ProgressDialog(mCtx);
        pDialog.setMessage("Loading...");
        pDialog.show();

        client = new DiigoClient(mCtx);
        client.getDiigoBookMarks(marks, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(mCtx.getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "statusCode: " + statusCode + "responseString: " + responseString);
                // pDialog.hide();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.d(TAG, "statusCode: " + statusCode + "JSONArray errorResponse: " + errorResponse);
                // pDialog.hide();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d(TAG, "statusCode: " + statusCode + "; JSONObject errorResponse: " + errorResponse);
                // pDialog.hide();

            }


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                if (statusCode == 200) {
                    String resp = response.toString();
                    Log.i(TAG, "response: " + resp);

                    JSONArray newCacheMarks = new JSONArray();

                    // 从缓存文件中读取第一条信息的时间（最晚），和系统的比对，如果有比缓存的还晚，则加入缓存
                    String createAtFile = null;
                    String cacheAllData = DiigoUtil.readMarksFromFile();
                    try {
                        JSONArray cacheAllMarks =  new JSONArray(cacheAllData);
                        JSONObject firstMarks = (JSONObject) cacheAllMarks.get(0);
                        createAtFile = (String) firstMarks.get("created_at");

                        int compare = 0;
                        // 由于每次只能读取100 个书签，所以要遍历，读取所以书签
                        if (response.length() <= marks.getCount() && response.length() > 0) {


                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);

                                String createAtServer = (String) jsonObject.get("created_at");
                                Log.d(TAG, "createAtFile: " + createAtFile +";createAtServer: " + createAtServer);
                                compare = createAtFile.compareTo(createAtServer);
                                Log.d(TAG, "compareTo: " + compare);
                                // 
                                if (compare < 0) {
                                    newCacheMarks.put(jsonObject);
                                    Log.d(TAG, "newCacheMarks put Server Marks: " + jsonObject.toString());
                                }

                               
                            }// for end
                            if (compare < 0) {
                                marks.setStart(marks.getStart() + marks.getCount());;
                                marks.setCount(marks.getCount());
                                fetchDiigoBookMarks(marks, marks.getCount());
                                Log.d(TAG, response.length() + "");
                                Log.d(TAG, "Marks Start" + marks.getStart());
                            } else {
                                for (int i = 0; i < cacheAllMarks.length(); i++) {
                                    JSONObject itemMarks = (JSONObject) cacheAllMarks.get(i);
                                    newCacheMarks.put(itemMarks);
                                }
                                Log.d(TAG, "newCacheMarks leng: " + newCacheMarks.length() + "");
                                Log.d(TAG, "newCacheMarks: " + newCacheMarks.toString() + "");
                                Log.d(TAG, "readAllMarksJsonArr leng: " + readAllMarksJsonArr.length() + "");
                                Log.d(TAG, "readAllMarksJsonArr: " + readAllMarksJsonArr.toString() + "");
                                
                                DiigoUtil.writeMarksToFile(newCacheMarks.toString());
                            }
                        } else if (response.length() == 0) {

                            Toast.makeText(mCtx.getApplicationContext(), "ReadLater complete", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "ReadAll complete");
                            // 数据读取完，将读取的数据写入文件
                            // DiigoUtil.writeMarksToFile(readAllMarksJsonArr.toString());
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    pDialog.hide();


                }// statusCode end
            }

        });

    }
    
    public void officeGetMarks(DiigoBookMarks marks) {
        String allMarks = DiigoUtil.readMarksFromFile();

        // 传入的Tag
        String meTag = marks.getTags();
        // 根据Tag，返回遍历后的JsonArr
        JSONArray meJsonArr = new JSONArray();

        JSONArray response = null;
        try {
            response = new JSONArray(allMarks);
            Log.d(TAG, "response Size: " + response.length());
            for (int i = 0; i < response.length(); i++) {

                JSONObject jsonObject = (JSONObject) response.get(i);
                Log.d(TAG, "jsonObject: " + jsonObject);
                String tags = jsonObject.getString("tags");
                String[] tag = tags.split(",");
                for (String s : tag) {
                    
                    if (meTag.equals(s)) {
                        Log.d(TAG, "meTag: " + meTag + "; s: " + s.toLowerCase());

                        // meJsonArr.put(response.getJSONObject(i));
                        meJsonArr.put(jsonObject);

                        
                    }
                }

            }
            
            List<DiigoBookMarks> markses = DiigoBookMarks.fromJson(meJsonArr);
            for (DiigoBookMarks mark : markses) {
                allTagsAdapter.add(mark);
                // allTagsAdapter.add(mark);

            }
            Log.d(TAG, "meJsonArr Size: " + meJsonArr.length());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
    } 

    
    
}
