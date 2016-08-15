package com.weilian.phonelive.adapter;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.weilian.phonelive.AppConfig;
import com.weilian.phonelive.AppContext;
import com.weilian.phonelive.R;
import com.weilian.phonelive.bean.MusicBean;
import com.weilian.phonelive.fragment.SearchMusicDialogFragment;
import com.weilian.phonelive.ui.StartLiveActivity;
import com.weilian.phonelive.utils.DBManager;

import java.io.File;
import java.util.List;

/**
 * 搜索音乐列表
 */
public class SearchMusicAdapter extends BaseAdapter {
    private List<MusicBean> mMusicList;
    private SearchMusicDialogFragment mFragment;
    private DBManager mDbManager;

    public SearchMusicAdapter(List<MusicBean> MusicList, SearchMusicDialogFragment fragment, DBManager dbManager){
        this.mMusicList =  MusicList;
        this.mFragment = fragment;
        this.mDbManager = dbManager;
    }
    public void notifyDataSetChangedMusicList(List<MusicBean> MusicList){
        this.mMusicList =  MusicList;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mMusicList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMusicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        viewHolder = new ViewHolder();
        convertView = View.inflate(AppContext.getInstance(), R.layout.item_search_music,null);
        viewHolder.mMusicName = (TextView) convertView.findViewById(R.id.item_tv_search_music_name);
        viewHolder.mMusicAuthor = (TextView) convertView.findViewById(R.id.item_tv_search_music_author);
        viewHolder.mBtnDownload = (CircularProgressButton) convertView.findViewById(R.id.item_btn_search_music_download);

        final MusicBean music = mMusicList.get(position);
        viewHolder.mMusicName.setText(music.getSongname());
        viewHolder.mMusicAuthor.setText(music.getArtistname());
        final File file = new File(AppConfig.DEFAULT_SAVE_MUSIC_PATH + music.getSongname() + ".mp3");
        if(file.exists()){
            viewHolder.mBtnDownload.setText(R.string.select);
        }
        //点击下载或播放
        viewHolder.mBtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if(file.exists()){
                    intent = new Intent();
                    //mFragment.getActivity().setResult(1,intent.putExtra("filepath",file.getPath()));
                    //mFragment.getActivity().finish();
                    ((StartLiveActivity)mFragment.getActivity()).onSelectMusic(intent.putExtra("filepath",file.getPath()));
                }else {
                    mFragment.downloadMusic(music,(CircularProgressButton)v);
                }
            }
        });

        return convertView;
    }

    class ViewHolder{
        TextView mMusicName,mMusicAuthor;
        CircularProgressButton mBtnDownload;
    }
}
