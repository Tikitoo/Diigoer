package com.tikitoo.diigo.app2.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tikitoo on 1/4/15.
 */
public class DiigoBookMarks {

    /** 网址 */
    private String url;
    /** 网址标题 */
    private String title;
    /** 用户名 */
    private String user;
    /** 标签 */

    private String tags;
    /** 显示的数量 */
    private int count;
    private String list;

    /** 受欢迎程度 */
    private String sort;
    /** 权限 */
    private String filter;
    /** 描述 */
    private String desc;
    /** 是否分享 */
    private String shared;
    private String update_at;
    private String create_at;
    private String annotations;

    private String readLater;

    private int start;

    public DiigoBookMarks(String url, String title, String tags) {
        this.url = url;
        this.title = title;
        this.tags = tags;
    }

    public DiigoBookMarks() {
    }

    public static DiigoBookMarks fromJson(JSONObject jsonObject) {
        DiigoBookMarks marks = new DiigoBookMarks();
        try {
            marks.url = (String) jsonObject.get("url");
            marks.title = (String) jsonObject.get("title");
            marks.tags = (String) jsonObject.get("tags");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return marks;
    }

    public static List<DiigoBookMarks> fromJson(JSONArray jsonArray) {
        List<DiigoBookMarks> markses = new ArrayList<DiigoBookMarks>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject marksObj;
            try {
                marksObj = jsonArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
                continue;
            }

            DiigoBookMarks marks = DiigoBookMarks.fromJson(marksObj);
            if (marks != null) {
                markses.add(marks);
            }
        }
        return markses;
    }



    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getShared() {
        return shared;
    }

    public void setShared(String shared) {
        this.shared = shared;
    }

    public String getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(String update_at) {
        this.update_at = update_at;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getAnnotations() {
        return annotations;
    }

    public void setAnnotations(String annotations) {
        this.annotations = annotations;
    }

    public String getReadLater() {
        return readLater;
    }

    public void setReadLater(String readLater) {
        this.readLater = readLater;
    }

    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
