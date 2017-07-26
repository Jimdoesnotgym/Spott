package com.example.chiangj.spott.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.chiangj.spott.R;
import com.example.chiangj.spott.viewholders.SongViewHolder;
import com.example.chiangj.spott.models.Song;

import java.util.Arrays;
import java.util.List;

public class SongListAdapter extends ArrayAdapter<Song>{
    private List<Song> mListOfSongs;
    private final int mListItemLayoutResource;
    private Context mContext;

    public SongListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull Song[] objects) {
        super(context, resource, objects);

        mListOfSongs = Arrays.asList((Song[])objects);
        mListItemLayoutResource = resource;
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Song currentSong = mListOfSongs.get(position);

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mListItemLayoutResource, null, false);

            SongViewHolder viewHolder = new SongViewHolder();
            viewHolder.songName = (TextView) convertView.findViewById(R.id.song_name);
            viewHolder.artistName = (TextView) convertView.findViewById(R.id.artist_name);

            convertView.setTag(viewHolder);
        }
        TextView songName = ((SongViewHolder)convertView.getTag()).songName;
        TextView artistName = ((SongViewHolder)convertView.getTag()).artistName;

        //TODO get current song
        songName.setText(currentSong.getSongName());

        return convertView;
    }
}
