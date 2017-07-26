package com.example.chiangj.spott.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chiangj.spott.R;
import com.example.chiangj.spott.models.Song;

import java.util.List;


public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.SongViewHolder>{
    private static final String TAG = SongListAdapter.class.getSimpleName();

    private List<Song> mSongList;

    public static class SongViewHolder extends RecyclerView.ViewHolder{
        private final TextView songName;
        private final TextView artistName;

        public SongViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");
                }
            });
            songName = (TextView) itemView.findViewById(R.id.song_name);
            artistName = (TextView) itemView.findViewById(R.id.artist_name);
        }

        public TextView getSongName(){
            return songName;
        }

        public TextView getArtistName(){
            return artistName;
        }
    }

    public SongListAdapter(List<Song> dataSet){
        mSongList = dataSet;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_item, parent, false);

        return new SongViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        holder.getSongName().setText(mSongList.get(position).getSongName());
        holder.getArtistName().setText(mSongList.get(position).getArtistName());
    }

    @Override
    public int getItemCount() {
        return mSongList.size();
    }

}
