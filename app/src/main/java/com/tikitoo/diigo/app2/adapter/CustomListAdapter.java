package com.tikitoo.diigo.app2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tikitoo.diigo.app2.R;
import com.tikitoo.diigo.app2.bean.DiigoBookMarks;

import java.util.List;

/**
 * Created by tikitoo on 1/4/15.
 */
public class CustomListAdapter extends ArrayAdapter<DiigoBookMarks> {
    private TextView urlTextView, titleTextView, tagsTextView, dateTextView, createAtTv;
    public CustomListAdapter(Context context, List<DiigoBookMarks> markses) {
        super(context, 0,markses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DiigoBookMarks mark = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item, parent, false);

        }
        urlTextView = (TextView) convertView.findViewById(R.id.url_text_item);
        titleTextView = (TextView) convertView.findViewById(R.id.title_text_item);
        tagsTextView = (TextView) convertView.findViewById(R.id.tags_text_item);

        

        titleTextView.setText(mark.getTitle());

            /*String urlData = mark.getUrl();
            String urlData2 = DiigoUtil.subUrl(urlData);
            urlTextView.setText(urlData2);*/
        urlTextView.setText(mark.getUrl());


        if (mark.getTags().equals("no_tag")) {
            tagsTextView.setText("");
        } else {
            String[] tags = mark.getTags().split(",");
            StringBuffer sb = new StringBuffer();
            for (String tag : tags) {
                sb.append(tag + " ");
            }
            tagsTextView.setHint(sb);
        }
        return convertView;
    }
}
