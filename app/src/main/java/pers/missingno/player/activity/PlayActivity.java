package pers.missingno.player.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;

import pers.missingno.player.R;
import pers.missingno.player.objects.Music;
import pers.missingno.player.service.MusicService;
import pers.missingno.player.view.PlayerBar;

public class PlayActivity extends AppCompatActivity {

    public static final int MSG_UPDATE_PROGRESS=0;
    public static final int MSG_UPDATE_MUSIC=1;

    public static Handler handler;

    private Toolbar toolbar;
    private ImageView imageView;
    private static SeekBar seekBar;
    private ImageButton play,previous,next,playMode,playlist;
    private TextView currentTime,duaration;

    private Music music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case MSG_UPDATE_PROGRESS:{
                        int progress=msg.arg1;
                        if(seekBar!=null)
                            seekBar.setProgress(progress);
                    }
                    return  true;
                    case MSG_UPDATE_MUSIC:{
                        Music music= (Music) msg.obj;
                        int progress=msg.arg1;
                        setMusic(music);
                        setPosition(progress);
                    }
                    return true;
                    default:
                        break;
                }
                return false;
            }
        });

        toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageView= (ImageView) findViewById(R.id.imageView);
        seekBar= (SeekBar) findViewById(R.id.seekBar);
        play= (ImageButton) findViewById(R.id.play);
        next= (ImageButton) findViewById(R.id.next);
        previous= (ImageButton) findViewById(R.id.previous);
        playMode= (ImageButton) findViewById(R.id.play_mode);
        playlist= (ImageButton) findViewById(R.id.play_list);
        currentTime= (TextView) findViewById(R.id.current_time);
        duaration= (TextView) findViewById(R.id.duration);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    MainActivity.service.play(null,progress);
                }
                int minute=progress/60000;
                int second=(progress/1000)%60;
                DecimalFormat format=new DecimalFormat("00");
                currentTime.setText(format.format(minute)+":"+format.format(second));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.service != null) {
                    if (MainActivity.service.isPlaying()) {
                        MainActivity.service.pause();
                        play.setImageDrawable(getResources().getDrawable(R.mipmap.ic_play_circle_outline_white_48dp, null));
                    } else {
                        MainActivity.service.resume();
                        play.setImageDrawable(getResources().getDrawable(R.mipmap.ic_pause_circle_outline_white_48dp, null));
                    }
                    PlayerBar.handler.sendMessage(PlayerBar.handler.obtainMessage(PlayerBar.MSG_UPDATE_STATE, getApplicationContext()));
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.service!=null){
                    Music music= MainActivity.service.previous();
                    if(music!=null){
                        play.setImageDrawable(getResources().getDrawable(R.mipmap.ic_pause_circle_outline_white_48dp, null));
                    }
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.service!=null){
                    Music music=MainActivity.service.next();
                    if(music!=null){
                        play.setImageDrawable(getResources().getDrawable(R.mipmap.ic_pause_circle_outline_white_48dp, null));
                    }
                }
            }
        });

        playMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.service!=null){
                    switch (MainActivity.service.getPlayMode()){
                        case MusicService.MODE_SEQUENCE:{
                            MainActivity.service.setPlayMode(MusicService.MODE_REPEAT);
                            playMode.setImageDrawable(getResources().getDrawable(R.mipmap.ic_repeat_one_white_48dp,null));
                        }
                        break;
                        case MusicService.MODE_REPEAT:{
                            MainActivity.service.setPlayMode(MusicService.MODE_RANDOM);
                            playMode.setImageDrawable(getResources().getDrawable(R.mipmap.ic_shuffle_white_48dp,null));
                        }
                        break;
                        case MusicService.MODE_RANDOM:{
                            MainActivity.service.setPlayMode(MusicService.MODE_SEQUENCE);
                            playMode.setImageDrawable(getResources().getDrawable(R.mipmap.ic_repeat_white_48dp,null));
                        }
                        break;
                    }
                }
            }
        });

        setMusic((Music) getIntent().getSerializableExtra("music"));
        setPosition(getIntent().getIntExtra("position", 0));

        if(MainActivity.service!=null){
            switch (MainActivity.service.getPlayMode()){
                case MusicService.MODE_SEQUENCE:{
                    playMode.setImageDrawable(getResources().getDrawable(R.mipmap.ic_repeat_white_48dp,null));
                }
                break;
                case MusicService.MODE_REPEAT:{
                    playMode.setImageDrawable(getResources().getDrawable(R.mipmap.ic_repeat_one_white_48dp,null));
                }
                break;
                case MusicService.MODE_RANDOM:{
                    playMode.setImageDrawable(getResources().getDrawable(R.mipmap.ic_shuffle_white_48dp,null));
                }
                break;
            }
        }
    }

    public void setMusic(Music music){
        if(music==null)
            return;
        this.music=music;
        if(music.getPic()!=null){
            imageView.setImageBitmap(music.getPic());
        }else{
            imageView.setImageDrawable(getResources().getDrawable(R.mipmap.ic_album_white_48dp, null));
        }
        toolbar.setTitle(music.getTitle());
        toolbar.setSubtitle(music.getSinger());
        seekBar.setMax(music.getDuration());
        int minute=music.getDuration()/60000;
        int second=(music.getDuration()/1000)%60;
        DecimalFormat format=new DecimalFormat("00");
        duaration.setText(format.format(minute)+":"+format.format(second));
        if(MainActivity.service!=null){
            if(MainActivity.service.isPlaying()){
                play.setImageDrawable(getResources().getDrawable(R.mipmap.ic_pause_circle_outline_white_48dp, null));
            }else{
                play.setImageDrawable(getResources().getDrawable(R.mipmap.ic_play_circle_outline_white_48dp, null));
            }
        }else{
            play.setImageDrawable(getResources().getDrawable(R.mipmap.ic_play_circle_outline_white_48dp, null));
        }
    }

    public Music getMusic(){
        return music;
    }

    public void setPosition(int progress){
        seekBar.setProgress(progress);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_to_play_list) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
