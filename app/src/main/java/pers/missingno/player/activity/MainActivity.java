package pers.missingno.player.activity;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.AndroidCharacter;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;

import java.util.ArrayList;

import pers.missingno.player.R;
import pers.missingno.player.adapter.MusicPagerAdapter;
import pers.missingno.player.fragment.MusicFragment;
import pers.missingno.player.fragment.MusicListFragment;
import pers.missingno.player.objects.Music;
import pers.missingno.player.service.MusicService;
import pers.missingno.player.view.PlayerBar;

public class MainActivity extends AppCompatActivity {

    public static final int MSG_PLAY=0;
    public static final int MSG_UPDATE_PROGRESS=1;

    public static Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MSG_PLAY:{
                    Music music= (Music) msg.obj;
                    int progress=msg.arg1;
                    playerBar.startMusic(music);
                    playerBar.setPosition(progress);
                }
                return true;
                case MSG_UPDATE_PROGRESS:{
                    int progress=msg.arg1;
                    playerBar.setPosition(progress);
                }
                return  true;
                default:
                    break;
            }
            return false;
        }
    });

    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MainActivity.service=((MusicService.MusicBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            MainActivity.service=null;
        }
    };

    public static MusicService service;

    private ViewPager pager;
    private PagerTabStrip tab;
    private static PlayerBar playerBar;

    private ArrayList<Fragment> pages;

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager= (ViewPager) findViewById(R.id.pager);
        tab= (PagerTabStrip) findViewById(R.id.tab);
        playerBar= (PlayerBar) findViewById(R.id.player_bar);

        initPages();
        new Thread(){
            public void run(){
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                initMusics();
            }
        }.start();

        Intent intent=new Intent(this, MusicService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void initPages(){
        pages=new ArrayList<>();
        tab.setTabIndicatorColor(getResources().getColor(R.color.colorAccent));
        tab.setTextColor(getResources().getColor(android.R.color.white));
        tab.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);

        MusicFragment musicFragment=new MusicFragment();
        Bundle musicBundle=new Bundle();
        musicBundle.putString("name", "音乐列表");
        musicFragment.setArguments(musicBundle);
        pages.add(musicFragment);

        MusicListFragment musicListFragment=new MusicListFragment();
        Bundle musicListBundle=new Bundle();
        musicListBundle.putString("name", "播放列表");
        musicListFragment.setArguments(musicListBundle);
        pages.add(musicListFragment);

        pager.setAdapter(new MusicPagerAdapter(getSupportFragmentManager(), pages));
    }

    private void initMusics(){
        ArrayList<Music> musics=new ArrayList<>();
        ContentResolver contentResolver=getContentResolver();
        Cursor cursor=contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,null,null,MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if(cursor.moveToFirst()){
            do {
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                String url = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                Bitmap pic=getAlbumArt(url);
                Music music=new Music(title,singer,album,size,duration,url,pic);
                musics.add(music);
            } while (cursor.moveToNext());
        }
        cursor.close();
        Message msg=MusicFragment.handler.obtainMessage(MusicFragment.MSG_UPDATE_LIST,pages.get(0));
        Bundle bundle=new Bundle();
        bundle.putSerializable("list",musics);
        msg.setData(bundle);
        MusicFragment.handler.sendMessage(msg);
    }

    public static Bitmap getAlbumArt(String url){
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(url);
            byte[] embedPic = retriever.getEmbeddedPicture();
            bitmap = BitmapFactory.decodeByteArray(embedPic, 0, embedPic.length);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return bitmap;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
