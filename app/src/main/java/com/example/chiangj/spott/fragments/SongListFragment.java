package com.example.chiangj.spott.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chiangj.spott.MainActivity;
import com.example.chiangj.spott.R;
import com.example.chiangj.spott.adapters.SongListAdapter;
import com.example.chiangj.spott.models.Song;

import java.util.Arrays;


public class SongListFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private SongListAdapter mSongListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_song_recycler_view, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);


        /*Song[] songs = {
                new Song("A", "1"),
                new Song("B","2"),
                new Song("C", "3"),
                new Song("A", "1"),
                new Song("B","2"),
                new Song("C", "3"),
                new Song("A", "1"),
                new Song("B","2"),
                new Song("C", "3"),
                new Song("A", "1"),
                new Song("B","2"),
                new Song("C", "3")
        };

        mSongListAdapter = new SongListAdapter(Arrays.asList(songs));*/
        mRecyclerView.setAdapter(mSongListAdapter);

        return rootView;
    }
}
