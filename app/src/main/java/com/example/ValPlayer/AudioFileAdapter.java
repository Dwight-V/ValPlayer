package com.example.ValPlayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// From https://github.com/codepath/android_guides/wiki/Using-the-RecyclerView
public class AudioFileAdapter extends RecyclerView.Adapter<AudioFileAdapter.ViewHolder> {
    // region ViewHolder
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView txtArtist, txtSong, txtPath, txtTime;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            txtArtist = itemView.findViewById(R.id.textview_row_item_artist);
            txtSong = itemView.findViewById(R.id.textview_row_item_song);
            txtPath = itemView.findViewById(R.id.textview_row_item_path);
            txtTime = itemView.findViewById(R.id.textview_row_item_time);
        }
    }
    // endregion

    // Store a member variable for the files
    private List<AudioFile> allAudio;

    // Pass in the contact array into the constructor
    public AudioFileAdapter(List<AudioFile> allAudio) {
        this.allAudio = allAudio;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_audio_file, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Get the data model based on position
        AudioFile audioFile = allAudio.get(position);

        // Set item views based on your views and data model
//        TextView textView = holder.txtArtist;
        holder.txtArtist.setText(audioFile.getArtist());
        holder.txtSong.setText(audioFile.getSong());
        holder.txtTime.setText(audioFile.getFormattedDuration());
        holder.txtPath.setText(audioFile.getPath());
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return allAudio.size();
    }
}