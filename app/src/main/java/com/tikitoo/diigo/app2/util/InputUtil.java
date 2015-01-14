package com.tikitoo.diigo.app2.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by tikitoo on 1/10/15.
 */
public class InputUtil {
    private static final String TAG = InputUtil.class.getName();
    private static boolean mFlag;
    private static Context mCtx;


    public InputUtil(Context ctx) {
        mCtx = ctx.getApplicationContext();
    }


    public static boolean isNotNull(String...str) {
        int i = str.length;
        for (int j = 0; j < i; j++) {
            if (null != str[j] && !(str[j].trim()).equals("")) {
                mFlag = true;
            } else {
                Toast.makeText(mCtx, "Input is NUll", Toast.LENGTH_SHORT).show();
                mFlag = false;
            }
        }
        return mFlag;
    }
    
    public static List compareTagsList(List tagsList) {

        Collections.sort(tagsList, new Comparator() {
            @Override
            public int compare(Object leftTag, Object rightTag) {
                return leftTag.toString().compareTo(rightTag.toString());
            }
        });
        return tagsList;
        
    }

    /**
     * 返回这样格式的时间："yyyy/MM/dd HH:mm:ss"
     * @return
     */
    public static String getNowDate() {


        /*Time time = new Time("GMT+8");
        time.setToNow();
        int hour = time.hour; // 0-23
        String nowTime = time.toString();
        Log.d(TAG, "getNowDate" + nowTime);
        */
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        // 设置小一个月份
        curDate.setMonth(curDate.getMonth() - 1);
        String str = format.format(curDate);
        Log.d(TAG, "getCurrentTime" + str);

        return str;
    }
}
