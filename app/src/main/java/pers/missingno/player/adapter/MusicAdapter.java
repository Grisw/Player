package pers.missingno.player.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import pers.missingno.player.R;
import pers.missingno.player.activity.MainActivity;
import pers.missingno.player.objects.Music;
import pers.missingno.player.service.MusicService;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder>{

    private List<Music> musics;

    public MusicAdapter(List<Music> musics){
        this.musics=musics;
    }

    @Override
    public MusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MusicViewHolder holder=new MusicViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_music,parent,false));
        return holder;
    }

    @Override
    public void onBindViewHolder(final MusicViewHolder holder, final int position) {
        holder.musicName.setText(musics.get(position).getTitle());
        holder.authorName.setText(musics.get(position).getSinger());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.service.play(musics.get(holder.getAdapterPosition()),0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musics.size();
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder{

        protected TextView musicName;
        protected TextView authorName;
        protected ImageButton addToQueue;
        protected CheckBox checkBox;
        protected LinearLayout layout;

        public MusicViewHolder(View itemView) {
            super(itemView);
            musicName= (TextView) itemView.findViewById(R.id.music_name);
            authorName= (TextView) itemView.findViewById(R.id.music_author);
            addToQueue= (ImageButton) itemView.findViewById(R.id.add_to_queue);
            checkBox= (CheckBox) itemView.findViewById(R.id.cbx_music);
            layout= (LinearLayout) itemView.findViewById(R.id.layout);
        }
    }
}
