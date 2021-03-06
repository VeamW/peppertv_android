package com.weilian.phonelive.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.weilian.phonelive.bean.MusicBean;
import com.weilian.phonelive.db.DBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/5/5.
 */
public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }

    /**
     * add music
     * @param musics
     */
    public void add(List<MusicBean> musics) {
        db.beginTransaction();  //开始事务
        try {
            for (MusicBean music : musics) {
                db.execSQL("INSERT INTO music VALUES(null, ?, ?, ?)", new Object[]{music.getSongname(), music.getSongid(), music.getArtistname()});
            }
            db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }
    public void delete(MusicBean music){
        db.delete("music","songid=?",new String[]{music.getSongid()});
    }

    /**
     * update music's songid
     * @param music
     */
    public void updateAge(MusicBean music) {
        ContentValues cv = new ContentValues();
        cv.put("songid", music.getSongid());
        db.update("music", cv, "songname = ?", new String[]{music.getSongname()});
    }

    /**
     * query all music, return list
     * @return List<music>
     */
    public List<MusicBean> query() {
        ArrayList<MusicBean> musics = new ArrayList<MusicBean>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            MusicBean music = new MusicBean();
            music.setSongid(c.getString(c.getColumnIndex("songid")));
            music.setSongname( c.getString(c.getColumnIndex("songname")));
            music.setArtistname( c.getString(c.getColumnIndex("artistname")));
            musics.add(music);
        }
        c.close();
        return musics;
    }

    /**
     * query all music, return cursor
     * @return  Cursor
     */
    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM music", null);
        return c;
    }

    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}
