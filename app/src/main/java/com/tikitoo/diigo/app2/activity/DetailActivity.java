package com.tikitoo.diigo.app2.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.tikitoo.diigo.app2.R;

/**
 * "title":"简图 | 写字，配图，分享到微信和微博",
 * "url":"http:\/\/jiantuapp.com","comments":[]*
 * "tags":"no_tag",
 * "desc":"",
 * "annotations":[],
 * "created_at":"2015\/01\/13 04:37:57 +0000",
 * "updated_at":"2015\/01\/13 04:37:57 +0000",
 * "shared":"no",
 * "readlater":"no",
 * 显示书签详细界面
 * 设置Url 可以点击分享 
 * Created by tikitoo on 1/7/15.
 */
public class DetailActivity extends ActionBarActivity {

    TextView tvUrl, tvTitle, tvTags, tvDesc, tvComments, tvShared, tvReadLater, tvCreateAt, tvUpdateAt;
    Intent shareIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tvUrl = (TextView) findViewById(R.id.detail_url_text_view);
        tvTitle = (TextView) findViewById(R.id.detail_title_text_view);
        tvTags = (TextView) findViewById(R.id.detail_tags_text_view);
        
        
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
                                                                                                                                                
        String url = bundle.getString("url");
        String title = bundle.getString("title");
        String tags = bundle.getString("tags");
        
        StringBuffer tagSb = new StringBuffer();
        String[] tagss = tags.split(",");
        for (String tag : tagss) {
            tagSb.append(tag + " ");
        }
        
        tvUrl.setText(url);
        tvTitle.setText(Html.fromHtml("<b>Title: </b>" + title));
        tvTags.setText(Html.fromHtml("<b>Tags: </b>" + tagSb));

        tvUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.detail_url_text_view:
                        // 设置为Url 打开
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tvUrl.getText().toString()));
                        // shareIntent = new Intent(Intent.ACTION_SEND, Uri.parse(tvUrl.getText().toString()));
                        startActivity(Intent.createChooser(intent, "Share Url"));

                }

            }
        });
        
    }
    
    public void otherInitData() {
        /*tvDesc = (TextView) findViewById(R.id.detail_desc_text_view);
        tvComments = (TextView) findViewById(R.id.detail_comments_text_view);
        tvCreateAt = (TextView) findViewById(R.id.detail_create_at_text_view);
        tvUpdateAt = (TextView) findViewById(R.id.detail_update_at_text_view);
        */
        /*String desc = bundle.getString("desc");
        String comments = bundle.getString("comments");
        String create_at = bundle.getString("create_at");
        String update_at = bundle.getString("update_at");
        */
        // Url 显示为点击
        /*tvDesc.setText(Html.fromHtml("<b>Description: </b>" + desc));
        tvComments.setText(Html.fromHtml("<b>Comments: </b>" + comments));
        tvCreateAt.setText(Html.fromHtml("<b>Create Time: </b>" + create_at));
        tvUpdateAt.setText(Html.fromHtml("<b>Update Time: </b>" + update_at));
        */

    }

    /**
     * 设置Menu 
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detail_marks, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Menu 操作
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_share_detail:
                shareMarkData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 将书签Url 分享
     */
    public void shareMarkData() {
        
        shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, tvUrl.getText().toString());
        shareIntent.setType("text/plain");
        startActivity(shareIntent);
    }

}
