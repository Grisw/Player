package pers.missingno.player.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import pers.missingno.player.R;
import pers.missingno.player.activity.MainActivity;
import pers.missingno.player.activity.PlayActivity;
import pers.missingno.player.objects.LinkedSet;
import pers.missingno.player.objects.Music;

public class MusicService extends Service {

    public static final int MODE_SEQUENCE=0;
    public static final int MODE_REPEAT=1;
    public static final int MODE_RANDOM=2;

    private LinkedSet<Music> playQueue;
    private MediaPlayer player;

    private boolean isPause=false;
    private int playMode=MODE_SEQUENCE;
    private Thread updateProgressThread;

    private Notification notification;
    private NotificationManager notificationManager;

    public MusicService() {
        player=new MediaPlayer();
        playQueue=new LinkedSet<>();
        player.setOnCompletionListener(new OnMusicCompletionListener());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new MusicBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent intent=PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        notification=new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_album_white_48dp)
                .setTicker("Player")
                .setContentTitle(getResources().getString(R.string.default_music_name))
                .setContentText(getResources().getString(R.string.default_author))
                .setContentIntent(intent)
                .build();
        startForeground(1,notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void play(Music music,int position){
        if(music!=null){
            if(player.isPlaying()){
                player.stop();
            }
            if(!playQueue.isEmpty()){
                Music pre=playQueue.poll();
                playQueue.offer(pre);
            }
            playQueue.offerFirst(music);
            player.reset();
            try {
                player.setDataSource(music.getUrl());
                player.prepare();
                player.setOnPreparedListener(new OnMusicPreparedListener(position));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            player.seekTo(position);
        }
    }

    public void pause(){
        if(player!=null&&player.isPlaying()){
            player.pause();
            isPause=true;
        }
    }

    public void resume(){
        if(player!=null&&isPause){
            player.start();
            isPause=false;
        }
    }

    public Music previous(){
        if(player!=null&&!playQueue.isEmpty()){
            if(player.isPlaying()){
                player.stop();
            }
            Music pre=playQueue.removeLast();
            playQueue.offerFirst(pre);
            player.reset();
            try {
                player.setDataSource(pre.getUrl());
                player.prepare();
                player.setOnPreparedListener(new OnMusicPreparedListener(0));
                return pre;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public Music next(){
        if(player!=null&&!playQueue.isEmpty()){
            if(player.isPlaying()){
                player.stop();
            }
            Music pre=playQueue.poll();
            playQueue.offer(pre);
            if(playMode==MODE_RANDOM){
                int pos=new Random().nextInt(playQueue.size());
                Music current=playQueue.remove(pos);
                playQueue.offerFirst(current);
            }
            player.reset();
            try {
                player.setDataSource(playQueue.peek().getUrl());
                player.prepare();
                player.setOnPreparedListener(new OnMusicPreparedListener(0));
                return playQueue.peek();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onDestroy() {
        if(player!=null){
            player.stop();
            player.release();
        }
        if(updateProgressThread!=null){
            updateProgressThread.interrupt();
        }
        super.onDestroy();
    }

    private class OnMusicPreparedListener implements MediaPlayer.OnPreparedListener{

        private int position;

        public OnMusicPreparedListener(int position){
            this.position=position;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
            if(position>0){
                mp.seekTo(position);
            }
            isPause=false;
            MainActivity.handler.sendMessage(MainActivity.handler.obtainMessage(MainActivity.MSG_PLAY,position,0,playQueue.peek()));
            if(PlayActivity.handler!=null)
                PlayActivity.handler.sendMessage(PlayActivity.handler.obtainMessage(PlayActivity.MSG_UPDATE_MUSIC,position,0,playQueue.peek()));
            if(updateProgressThread!=null){
                updateProgressThread.interrupt();
            }
            updateProgressThread=new Thread(){
                public void run(){
                    try{
                        while(true){
                            PendingIntent intent=PendingIntent.getActivity(MusicService.this, 0, new Intent(MusicService.this, MainActivity.class), 0);
                            notification=new Notification.Builder(MusicService.this)
                                    .setSmallIcon(R.mipmap.ic_album_white_48dp)
                                    .setTicker(playQueue.peek().getTitle())
                                    .setContentTitle(playQueue.peek().getTitle())
                                    .setContentText(playQueue.peek().getSinger())
                                    .setContentIntent(intent)
                                    .setShowWhen(false)
                                    .setProgress(playQueue.peek().getDuration(),player.getCurrentPosition(),false)
                                    .build();
                            notificationManager.notify(1,notification);
                            MainActivity.handler.sendMessage(MainActivity.handler.obtainMessage(MainActivity.MSG_UPDATE_PROGRESS,player.getCurrentPosition(),0));
                            if(PlayActivity.handler!=null)
                                PlayActivity.handler.sendMessage(PlayActivity.handler.obtainMessage(PlayActivity.MSG_UPDATE_PROGRESS,player.getCurrentPosition(),0));
                            Thread.sleep(1000);
                        }
                    }catch (InterruptedException e){

                    }
                }
            };
            updateProgressThread.start();
        }
    }

    private class OnMusicCompletionListener implements MediaPlayer.OnCompletionListener{

        @Override
        public void onCompletion(MediaPlayer mp) {
            Music last=playQueue.poll();
            switch (playMode){
                case MODE_SEQUENCE:
                    playQueue.offer(last);
                    break;
                case MODE_REPEAT:
                    playQueue.offerFirst(last);
                    break;
                case MODE_RANDOM:
                    playQueue.offer(last);
                    int pos=new Random().nextInt(playQueue.size());
                    Music current=playQueue.remove(pos);
                    playQueue.offerFirst(current);
                    break;
            }
            if(!playQueue.isEmpty()){
                player.reset();
                try {
                    player.setDataSource(playQueue.peek().getUrl());
                    player.prepare();
                    player.setOnPreparedListener(new OnMusicPreparedListener(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                player.stop();
            }
        }
    }

    public boolean isPlaying(){
        return !isPause;
    }

    public int getPlayMode(){
        return playMode;
    }

    public void setPlayMode(int mode){
        playMode=mode;
    }

    public class MusicBinder extends Binder {

        public MusicService getService(){
            return MusicService.this;
        }
    }
}
