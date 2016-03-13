package pers.missingno.player.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pers.missingno.player.R;
import pers.missingno.player.activity.MainActivity;
import pers.missingno.player.activity.PlayActivity;
import pers.missingno.player.objects.Music;

public class PlayerBar extends LinearLayout {

    public static final int MSG_UPDATE_STATE=0;

    public static Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MSG_UPDATE_STATE:{
                    Context context= (Context) msg.obj;
                    if(MainActivity.service.isPlaying()){
                        play.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_pause_grey, null));
                    }else{
                        play.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_play_arrow_grey, null));
                    }
                }
                return true;
                default:break;
            }
            return false;
        }
    });

    private ImageView albumImg;
    private TextView musicName;
    private TextView authorName;
    private ProgressBar progressBar;
    private static ImageButton play;
    private ImageButton next;

    private Music music;

    public PlayerBar(Context context) {
        super(context);
        init(context);
    }

    public PlayerBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        View view = inflate(context, R.layout.view_playerbar, this);
        albumImg= (ImageView) view.findViewById(R.id.album_img);
        musicName= (TextView) view.findViewById(R.id.music_name);
        authorName= (TextView) view.findViewById(R.id.music_author);
        progressBar= (ProgressBar) view.findViewById(R.id.progress_bar);

        play= (ImageButton) view.findViewById(R.id.play);
        next= (ImageButton) view.findViewById(R.id.next);
        LinearLayout layout= (LinearLayout) view.findViewById(R.id.layout);

        layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PlayActivity.class);
                intent.putExtra("music",music);
                intent.putExtra("position",progressBar.getProgress());
                v.getContext().startActivity(intent);
            }
        });

        play.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.service!=null){
                    if(MainActivity.service.isPlaying()){
                        MainActivity.service.pause();
                        play.setImageDrawable(getResources().getDrawable(R.mipmap.ic_play_arrow_grey, null));
                    }else{
                        MainActivity.service.resume();
                        play.setImageDrawable(getResources().getDrawable(R.mipmap.ic_pause_grey, null));
                    }
                }
            }
        });

        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.service!=null){
                    Music music = MainActivity.service.next();
                    if(music!=null){
                        play.setImageDrawable(getResources().getDrawable(R.mipmap.ic_pause_grey, null));
                    }
                }
            }
        });
    }

    public void startMusic(Music music){
        this.music=music;
        if(music.getPic()!=null){
            albumImg.setImageBitmap(music.getPic());
        }else{
            albumImg.setImageDrawable(getResources().getDrawable(R.mipmap.ic_album_grey600_48dp, null));
        }
        musicName.setText(music.getTitle());
        authorName.setText(music.getSinger());
        progressBar.setMax(music.getDuration());
        play.setImageDrawable(getResources().getDrawable(R.mipmap.ic_pause_grey, null));
    }

    public Music getMusic(){
        return music;
    }

    public void setPosition(int progress){
        progressBar.setProgress(progress);
    }
}
