package com.tikitoo.diigo.app2.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tikitoo.diigo.app2.R;
import com.tikitoo.diigo.app2.activity.DetailActivity;
import com.tikitoo.diigo.app2.adapter.CustomListAdapter;
import com.tikitoo.diigo.app2.bean.DiigoBookMarks;
import com.tikitoo.diigo.app2.util.DiigoUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 遍历书签列表
 * Created by tikitoo on 1/7/15.
 */
public class TagsListFragment extends Fragment {
    
    public static final String TAG = TagsListFragment.class.getName();
    private Context mCtx;

    private View rootView;
    private ListView tagListView;
    private List<DiigoBookMarks> tagList;
    private CustomListAdapter tagAdapter;
    private DiigoBookMarks marks;

    public TagsListFragment(Context ctx) {
        mCtx = ctx;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_bookmarks, container, false);

        final String[] tags = getArguments().getStringArray("tagsList");
        int position = getArguments().getInt("position");

        String queryTag = getArguments().getString("tag");
        
            
        tagListView = (ListView) rootView.findViewById(R.id.content_list_view);
        tagList = new ArrayList<DiigoBookMarks>();
        tagAdapter = new CustomListAdapter(mCtx.getApplicationContext(), tagList);
        tagListView.setAdapter(tagAdapter);

        marks = new DiigoBookMarks();

        marks.setStart(0);
        marks.setCount(100);
        if (position == 10000) {
            marks.setTags(queryTag);
        } else {
            marks.setTags(tags[position]);
        }

        officeGetMarks(marks);
        tagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
                // 需要传入数据
                Bundle bundle = new DiigoUtil(mCtx).getBundleByMarks(tagAdapter.getItem(position));
                Intent intent = new Intent();

                intent.putExtras(bundle);
                intent.setClass(getActivity(), DetailActivity.class);
                startActivity(intent);
            }
        });


        return rootView;
            
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
                Log.d(TAG, "split tag: " + tags);
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
                tagAdapter.add(mark);
                // allTagsAdapter.add(mark);

            }
            Log.d(TAG, "meJsonArr Size: " + meJsonArr.length());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    

}
