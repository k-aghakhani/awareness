package com.aghakhani.awareness;

import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.io.IOException;
import java.util.List;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.AudioViewHolder> {
    private Context context;
    private List<Audio> audioList;
    private MediaPlayer mediaPlayer;
    private int currentPlayingPosition = -1;
    private Handler handler = new Handler();
    private Runnable runnable;
    private Typeface vazirRegular;

    // Constructor
    public AudioAdapter(Context context, List<Audio> audioList) {
        this.context = context;
        this.audioList = audioList;
        this.mediaPlayer = new MediaPlayer();
        this.vazirRegular = Typeface.createFromAsset(context.getAssets(), "fonts/Vazir-Regular.ttf");
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.audio_item, parent, false);
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        Audio audio = audioList.get(position);
        holder.title.setText(audio.getTitle());
        holder.title.setTypeface(vazirRegular);
        holder.playButton.setTypeface(vazirRegular);
        holder.stopButton.setTypeface(vazirRegular);
        Glide.with(context).load(audio.getThumbnailUrl()).into(holder.thumbnail);

        // Reset UI for new item
        holder.progressBar.setVisibility(View.GONE);
        holder.playButton.setVisibility(View.VISIBLE);
        holder.stopButton.setVisibility(View.GONE);

        // Play button click listener
        holder.playButton.setOnClickListener(v -> {
            if (currentPlayingPosition != -1 && currentPlayingPosition != position) {
                stopCurrentPlayback();
            }
            currentPlayingPosition = position;
            playAudio(holder, audio);
        });

        // Stop button click listener
        holder.stopButton.setOnClickListener(v -> {
            stopCurrentPlayback();
            holder.progressBar.setVisibility(View.GONE);
            holder.playButton.setVisibility(View.VISIBLE);
            holder.stopButton.setVisibility(View.GONE);
            handler.removeCallbacks(runnable);
            currentPlayingPosition = -1;
        });
    }

    private void playAudio(AudioViewHolder holder, Audio audio) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audio.getAudioUrl());
            mediaPlayer.prepare();
            mediaPlayer.start();
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.progressBar.setMax(100);
            holder.playButton.setVisibility(View.GONE);
            holder.stopButton.setVisibility(View.VISIBLE);
            Toast.makeText(context, "در حال پخش: " + audio.getTitle(), Toast.LENGTH_SHORT).show();

            // Update SeekBar
            handler.postDelayed(runnable = () -> {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int progress = (int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100);
                    holder.progressBar.setProgress(progress);
                    handler.postDelayed(runnable, 1000);
                }
            }, 0);

            mediaPlayer.setOnCompletionListener(mp -> {
                holder.progressBar.setVisibility(View.GONE);
                holder.playButton.setVisibility(View.VISIBLE);
                holder.stopButton.setVisibility(View.GONE);
                handler.removeCallbacks(runnable);
                currentPlayingPosition = -1;
            });

        } catch (IOException e) {
            Toast.makeText(context, "خطا در پخش فایل صوتی", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopCurrentPlayback() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    // ViewHolder class
    public static class AudioViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView title;
        Button playButton;
        Button stopButton;
        SeekBar progressBar;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            title = itemView.findViewById(R.id.title);
            playButton = itemView.findViewById(R.id.playButton);
            stopButton = itemView.findViewById(R.id.stopButton);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    // Release MediaPlayer
    public void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            handler.removeCallbacks(runnable);
        }
    }
}