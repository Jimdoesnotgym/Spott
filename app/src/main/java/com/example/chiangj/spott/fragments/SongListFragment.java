package com.example.chiangj.spott.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chiangj.spott.BuildConfig;
import com.example.chiangj.spott.MainActivity;
import com.example.chiangj.spott.R;
import com.example.chiangj.spott.adapters.SongListAdapter;
import com.example.chiangj.spott.models.Song;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SongListFragment extends Fragment {

    private static final String TAG = SongListFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private SongListAdapter mSongListAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Song> songs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_song_recycler_view, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        songs = new ArrayList<Song>();

        mSongListAdapter = new SongListAdapter(songs);
        mRecyclerView.setAdapter(mSongListAdapter);

        return rootView;
    }

    public void updateAdapterList(List<Song> list){
        songs.addAll(list);
        mSongListAdapter.notifyItemInserted(list.size() - 1);
    }
}
