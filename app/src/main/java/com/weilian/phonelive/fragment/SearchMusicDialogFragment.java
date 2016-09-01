package com.weilian.phonelive.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.weilian.phonelive.AppConfig;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.adapter.SearchMusicAdapter;
import com.weilian.phonelive.api.remote.PhoneLiveApi;
import com.weilian.phonelive.bean.MusicBean;
import com.weilian.phonelive.utils.DBManager;
import com.weilian.phonelive.utils.TLog;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * 音乐搜索
 */
public class SearchMusicDialogFragment extends DialogFragment {
    @InjectView(R.id.iv_search_music_back)
    ImageView mSearchBack;
    @InjectView(R.id.tv_search_btn)
    TextView mSearchBtn;
    @InjectView(R.id.lv_search_music)
    ListView mSearchListView;
    @InjectView(R.id.et_search_input)
    EditText mInputEdit;
    @InjectView(R.id.iv_close)
    ImageView mIvClose;
    private List<MusicBean> mMusicList = new ArrayList<>();
    private SearchMusicAdapter mAdapter;
    private DBManager mDbManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_music,null);
        ButterKnife.inject(this,view);
        initView(view);
        initData();
        return view;
    }


    public void initView(View view) {
        mSearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchMusic();
            }
        });
        mSearchListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                File file = new File(AppConfig.DEFAULT_SAVE_MUSIC_PATH + mMusicList.get(position).getSongname() + ".mp3");
                if(file.exists()){
                    file.delete();
                    mDbManager.delete(mMusicList.get(position));
                    mMusicList.remove(position);
                    mAdapter.notifyDataSetChanged();

                }
                return false;
            }
        });
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void searchMusic() {
        String keyword = mInputEdit.getText().toString().trim();
        if(keyword.equals("")){
            AppContext.showToastAppMsg(getActivity(),"请输入有效的关键词~");
            return;
        }
        PhoneLiveApi.searchMusic(keyword,new StringCallback(){
            @Override
            public void onError(Call call, Exception e) {
                AppContext.showToastAppMsg(getActivity(),"查询失败,请换首歌试试~");
            }

            @Override
            public void onResponse(String response) {
                try {
                    mMusicList.clear();
                    JSONObject resJson = new JSONObject(response.toString());
                    if(!(resJson.getInt("error_code") == 22000)){
                        AppContext.showToastAppMsg(getActivity(),"查询失败,请换首歌试试~");
                    }else {
                        JSONArray musicListJson = resJson.getJSONArray("song");
                        Gson g = new Gson();
                        if(musicListJson.length() > 0){
                            for(int i = 0; i <musicListJson.length(); i++){
                                mMusicList.add(g.fromJson(musicListJson.getString(i),MusicBean.class));
                            }
                        }
                        fillUI();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fillUI() {
        mAdapter.notifyDataSetChangedMusicList(mMusicList);
    }


    public void initData() {
        mDbManager = new DBManager(getActivity());
        mMusicList =  mDbManager.query();
        mAdapter = new SearchMusicAdapter(mMusicList,this, mDbManager);
        mSearchListView.setAdapter(mAdapter);
        AppContext.showToastAppMsg(getActivity(),"长按删除歌曲~");

    }

    /**
     * @dw 下载歌曲
     * */
    public void downloadMusic(final MusicBean music,final CircularProgressButton mBtnDownload) {
        PhoneLiveApi.getMusicFileUrl(music.getSongid(),new StringCallback(){

            @Override
            public void onError(Call call, Exception e) {
                AppContext.showToastAppMsg(getActivity(),"获取歌曲失败");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject musicFileInfoJson = new JSONObject(response.toString());
                    if(musicFileInfoJson.getInt("error_code") == 22000){
                        String musicUrl = musicFileInfoJson.getJSONArray("bitrate").getJSONObject(0).getString("file_link");
                        String musicLrc = musicFileInfoJson.getJSONObject("songinfo").getString("lrclink");
                        //下载歌曲
                        PhoneLiveApi.downloadMusic(musicUrl,new FileCallBack(AppConfig.DEFAULT_SAVE_MUSIC_PATH,music.getSongname() + ".mp3"){

                            @Override
                            public void onError(Call call, Exception e) {

                                mBtnDownload.setErrorText("下载失败");
                            }

                            @Override
                            public void onResponse(File response) {
                                List<MusicBean> list = new ArrayList<MusicBean>();
                                list.add(music);
                                mDbManager.add(list);
                                TLog.log(response.getAbsolutePath());
                            }

                            @Override
                            public void inProgress(float progress, long total) {
                                mBtnDownload.setProgress((int) (progress*100));
                            }
                        });
                        try {
                            //下载歌词s
                            PhoneLiveApi.downloadLrc(musicLrc,new FileCallBack(AppConfig.DEFAULT_SAVE_MUSIC_PATH,music.getSongname() + ".lrc"){

                                @Override
                                public void onError(Call call, Exception e) {
                                    AppContext.showToastAppMsg(getActivity(),"没有找到相应的歌词~");
                                }

                                @Override
                                public void onResponse(File response) {
                                    TLog.log(response.getPath());
                                }

                                @Override
                                public void inProgress(float progress, long total) {

                                }
                            });
                        }catch (Exception e){
                            AppContext.showToastAppMsg(getActivity(),"歌词下载失败");
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("选择音乐"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(getActivity());          //统计时长
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("选择音乐"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(getActivity());
    }
    //歌曲选中回调接口
    public interface SearchMusicFragmentInterface{
        void onSelectMusic(Intent intent);
    }
}
