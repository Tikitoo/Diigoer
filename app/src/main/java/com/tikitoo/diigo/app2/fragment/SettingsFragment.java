package com.tikitoo.diigo.app2.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tikitoo.diigo.app2.R;
import com.tikitoo.diigo.app2.util.DiigoUtil;

import java.io.File;

/**
 * 
 * 帐号密码为空（或者错误）提示：onFailure statusCode = 401Error MessageUnauthorized
 * Created by tikitoo on 1/7/15.
 */
public class SettingsFragment extends Fragment {

    private static final String TAG = SettingsFragment.class.getName();
    private TextView accountInfoTv;
    private ImageButton logoutIBtn;
    private Button themeBtn, cacheBtn, aboutAppBtn, openSourceBtn, contactsMeBtn;
    private Context mCtx;
    private DiigoUtil diigoUtil;
    private long cacheSize;
    ActionBar actionBar;
    AlertDialog.Builder builder;
    
    public SettingsFragment(Context ctx) {
        mCtx = ctx;
        // diigoUtil = new DiigoUtil(mCtx);
    }
    
    


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
        actionBar.setTitle("Settings");


        accountInfoTv = (TextView) rootView.findViewById(R.id.settings_account_info_text_view);
        logoutIBtn = (ImageButton) rootView.findViewById(R.id.settings_logout_image_button);

        themeBtn = (Button) rootView.findViewById(R.id.settings_theme_button);
        cacheBtn = (Button) rootView.findViewById(R.id.settings_remove_caches_button);
        aboutAppBtn = (Button) rootView.findViewById(R.id.settings_about_app_button);
        openSourceBtn = (Button) rootView.findViewById(R.id.settings_open_source_button);
        contactsMeBtn = (Button) rootView.findViewById(R.id.settings_contacts_me_button);
        
        
        // 设置帐户名
        accountInfoTv.setText(new DiigoUtil(mCtx).getUserPreference().get(0));

        File cacheDir = mCtx.getFilesDir();
        cacheSize = cacheDirSize(cacheDir);
        cacheBtn.setText("Remove Cache（" + cacheSize / 1024 + "k）");
        // Toast.makeText(mCtx, "dirSize: " + cacheSize / 1024 + "k", Toast.LENGTH_SHORT).show();
        
        
        logoutIBtn.setOnClickListener(new MyLogoutListener());
        themeBtn.setOnClickListener(new MySetThemeListener());
        cacheBtn.setOnClickListener(new MyRemoveCacheListener());
        aboutAppBtn.setOnClickListener(new MyAboutAppListener());
        openSourceBtn.setOnClickListener(new MyOpenSourceListener());
        contactsMeBtn.setOnClickListener(new MyContactsMeListener());

        return rootView;
            
    }


    class MyLogoutListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            new DiigoUtil(mCtx).setLogoutStatus();
            // 退出登录登录并且清除缓存
            // 清空Tags 列表
            new DiigoUtil(mCtx).removePrefsCache();
            // 清空文件夹 files
            if (DiigoUtil.removeTagsListCache(mCtx.getFilesDir())) {
                cacheBtn.setText("");
                cacheBtn.setText("Remove Cache（" + 0 + "k）");
            }
        }
    }
    
    class MySetThemeListener implements View.OnClickListener {
        final CharSequence colorThemes[] = new CharSequence[] {
                "Material Button Dark（Default）",
                "Material Button Light",
                "Material Deep Teal Light",
                "Material Deep Teal Dark",
                "Blue Grey",
                "Material Background Dark",
        };

        @Override
        public void onClick(View v) {
            builder = new AlertDialog.Builder(new ContextThemeWrapper( mCtx, R.style.customAlertDialogTheme ));
            builder.setTitle("Select Theme");
            builder.setItems(colorThemes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            actionBar.setBackgroundDrawable(mCtx.getResources().getDrawable(R.color.button_material_dark));
                            break;
                        case 1:
                            actionBar.setBackgroundDrawable(mCtx.getResources().getDrawable(R.color.button_material_light));
                            break;
                        case 2:
                            actionBar.setBackgroundDrawable(mCtx.getResources().getDrawable(R.color.material_deep_teal_200));
                            break;
                        case 3:
                            actionBar.setBackgroundDrawable(mCtx.getResources().getDrawable(R.color.material_deep_teal_500));
                            break;
                        case 4:
                            actionBar.setBackgroundDrawable(mCtx.getResources().getDrawable(R.color.material_blue_grey_800));
                            break;
                        case 5:
                            actionBar.setBackgroundDrawable(mCtx.getResources().getDrawable(R.color.background_material_dark));
                            break;
                        default:
                            break;
                    }
                }
            });
            builder.show();
        }
    }
    
    class MyRemoveCacheListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            // 清空Tags 列表
            new DiigoUtil(mCtx).removePrefsCache();
            // 清空文件夹 files
            if (DiigoUtil.removeTagsListCache(mCtx.getFilesDir())) {
                cacheBtn.setText("");
                cacheBtn.setText("Remove Cache（" + 0 + "k）");
                Toast.makeText(mCtx.getApplicationContext(), "Remove Cache Successful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    
    

    boolean removeCacheDir(File cacheDir) {
        boolean result = false;
        if (cacheDir.exists()) {
            File[] filesList = cacheDir.listFiles();
            for (int i = 0; i < filesList.length; i++) {
                // 如果为文件夹，则继续读取（遍历）
                if (filesList[i].isDirectory()) {
                    cacheDirSize(filesList[i]);
                } else {
                    // 如果为文件，则删除
                    if (!filesList[i].delete()) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }


    long cacheDirSize(File cacheDir) {
        if (cacheDir.exists()) {
            long result = 0;
            File[] filesList = cacheDir.listFiles();
            for (int i = 0; i < filesList.length; i++) {
                // 如果为文件夹，则继续读取（遍历）
                if (filesList[i].isDirectory()) {
                    result += cacheDirSize(filesList[i]);
                } else {
                    // 如果为文件，则计算出文件大小，累加
                    result += filesList[i].length();
                }
            }
            return result;
        }
        return 0;
    }
    
    class MyAboutAppListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            WebView webView = new WebView(mCtx);
            webView.loadUrl("file:///android_res/raw/diigo_demo_about_app.html");
            // webView.loadUrl("https://github.com/Tikitoo/blog/issues/26");
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialogLayout = inflater.inflate(R.layout.dialog_about_app, null);

            builder = new AlertDialog.Builder(/*new ContextThemeWrapper( mCtx, R.style.customAlertDialogTheme )*/mCtx);
            builder.setTitle("About App");
            builder.setView(dialogLayout);
            builder.show();
        }
    }
    
    class MyOpenSourceListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialogLayout = inflater.inflate(R.layout.dialog_open_source, null);
            
            TextView view = (TextView) dialogLayout.findViewById(R.id.diigo_demo_open_source_async_http);
            view.setText(Html.fromHtml(getResources().getString(R.string.diigo_demo_lisense_async_http)));
            
            TextView commonsIOView = (TextView) dialogLayout.findViewById(R.id.diigo_demo_open_source_commons_io);
            commonsIOView.setText(Html.fromHtml(getResources().getString(R.string.diigo_demo_lisense_commons_io)));

            builder = new AlertDialog.Builder(new ContextThemeWrapper( mCtx, R.style.customAlertDialogTheme ));
            builder.setTitle("Open Source");
            builder.setView(dialogLayout);
            builder.show();
        }
    }
    
    class MyContactsMeListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialogLayout = inflater.inflate(R.layout.dialog_about_author, null);
            
            builder = new AlertDialog.Builder(/*new ContextThemeWrapper( mCtx, R.style.customAlertDialogTheme )*/mCtx);
            builder.setTitle("About Me");
            builder.setView(dialogLayout);
            builder.show();
        }

    }
    
    String getUrl(String str) {
        int start = str.indexOf("http");
        String url = str.substring(start, str.length());
        return url;
    }


}
