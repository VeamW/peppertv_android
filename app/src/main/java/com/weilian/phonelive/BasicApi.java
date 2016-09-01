package com.weilian.phonelive;

import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.utils.TLog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by adair.W on 2016/8/26.
 */
public class BasicApi {
    private LinkedHashMap<String, Object> mMap;

    public BasicApi(LinkedHashMap<String, Object> map) {
        mMap = map;
    }

    /**
     * get
     *
     * @param map
     * @return
     */
    public static void doGet(LinkedHashMap<String, String> map, StringCallback callback) {
        GetBuilder builder = OkHttpUtils.get().url(PhoneLiveApi.API_URL);
        if (null == map || map.isEmpty()) {
            builder.build().execute(callback);
            return;
        }
        Set<String> set = map.keySet();
        if (null == set || set.isEmpty()) {
            builder.build().execute(callback);
            return;
        }
        for (String key : set
                ) {
            builder.addParams(key, map.get(key));
        }
        builder.build().execute(callback);
    }

    /**
     * post
     *
     * @param map
     * @param callback
     */
    public static void doPost(LinkedHashMap<String, String> map, StringCallback callback) {
        PostFormBuilder builder = OkHttpUtils.post().url("url");
        if (null == map || map.isEmpty()) {
            builder.build().execute(callback);
            return;
        }
        Set<String> set = map.keySet();
        if (null == set || set.isEmpty()) {
            builder.build().execute(callback);
            return;
        }
        for (String key : set
                ) {
            builder.addParams(key, map.get(key));
        }
        builder.build().execute(callback);
    }


    /**
     * post file
     *
     * @param map
     * @param callback
     */
    public static void postFile(LinkedHashMap<String, Object> map, StringCallback callback) {
        PostFormBuilder builder = OkHttpUtils.post();
        builder.addFile("file", String.valueOf(map.get("type")), (File) map.get("file"));
        if (null == map || map.isEmpty()) {
            builder.build().execute(callback);
            return;
        }
        Set<String> set = map.keySet();
        if (null == set || set.isEmpty()) {
            builder.build().execute(callback);
            return;
        }
        for (String key : set
                ) {
            builder.addParams(key, String.valueOf(map.get(key)));
        }
        builder.url(String.valueOf(map.get("url"))).build().execute(callback);
    }

}
