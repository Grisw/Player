package pers.missingno.player.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import pers.missingno.player.R;
import pers.missingno.player.adapter.DividerItemDecoration;
import pers.missingno.player.adapter.MusicAdapter;
import pers.missingno.player.objects.Music;

public class MusicFragment extends Fragment{

    public static final int MSG_UPDATE_LIST=0;

    private RecyclerView recyclerView;

    public static Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MSG_UPDATE_LIST:{
                    Bundle bundle=msg.getData();
                    MusicFragment fragment= (MusicFragment) msg.obj;
                    List<Music> list = (List<Music>) bundle.getSerializable("list");
                    if(fragment.recyclerView!=null){
                        fragment.recyclerView.setAdapter(new MusicAdapter(list));
                    }
                }
                    return true;
                default:
                    break;
            }

            return false;
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        recyclerView= (RecyclerView) view.findViewById(R.id.music_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(container.getContext(),DividerItemDecoration.VERTICAL_LIST));
        return view;
    }

}
