package pers.missingno.player.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pers.missingno.player.R;

public class MusicListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_music_list, container, false);
        RecyclerView recyclerView= (RecyclerView) view.findViewById(R.id.music_list_recycler);
        return view;
    }

}
