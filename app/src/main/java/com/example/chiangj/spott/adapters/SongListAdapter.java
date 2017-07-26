package com.example.chiangj.spott.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class SongListAdapter<Song> extends ArrayAdapter{
    private List<Song> mListOfSongs;
    private final int mListItemLayoutResource;

    public SongListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);

        mListOfSongs = objects;
        mListItemLayoutResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = 
        }
        return convertView;
    }
}
