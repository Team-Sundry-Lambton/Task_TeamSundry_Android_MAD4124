package mad4124.team_sundry.task.ui.taskDetail;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import mad4124.team_sundry.task.R;
import mad4124.team_sundry.task.databinding.ItemTaskAudioBinding;
import mad4124.team_sundry.task.model.MediaFile;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.VH> {

    private List<MediaFile> list = new ArrayList<>();
    private final Context context;
    private final AudioAdapterListener listener;

    interface AudioAdapterListener {
        void playPause(boolean play, MediaFile model);

        void remove(int position, MediaFile model);

        void scrubberProgress(int progress, MediaFile mediaFile);
    }

    public AudioAdapter(Context context, AudioAdapterListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setData(List<MediaFile> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void addData(MediaFile data) {
        list.add(data);
        notifyItemInserted(list.size()-1);
    }

    public List<MediaFile> getList() {
        return list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskAudioBinding binding = ItemTaskAudioBinding.inflate(
                LayoutInflater.from(context), parent, false
        );
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.bind(list.get(position), listener, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class VH extends RecyclerView.ViewHolder {

        ItemTaskAudioBinding binding;

        public VH(ItemTaskAudioBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        boolean isPlaying = false;
        MediaFile model;
        MediaPlayer player = null;
        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int playerProgress = progress * 1000;
                if(player!= null && fromUser){
                    player.seekTo(playerProgress);
                    listener.scrubberProgress(progress, model);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        public void playMedia(){
            Uri uri = Uri.parse(model.getPath());

            player = new MediaPlayer();


            try {
                player.setDataSource(uri.getPath());
                binding.seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
                player.setOnPreparedListener(mp -> {
                    player = mp;
                    int duration = mp.getDuration() / 1000;
                    binding.seekBar.setMax(duration);

                    player.start();

                    new Timer().scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            int currentPosition = player.getCurrentPosition() / 1000;
                            Log.d("Position",""+currentPosition);
                            binding.seekBar.setProgress(currentPosition);
                        }
                    },0,1000);


                });
                player.setOnCompletionListener(mp -> {
                    binding.seekBar.setOnSeekBarChangeListener(null);
                    isPlaying = false;
                    binding.ivPlayPause.setImageResource(R.drawable.ic_outline_play_circle_filled_24);
                });
                player.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        public void stopMedia(){
            player.stop();
            player.release();
            player = null;
            binding.seekBar.setProgress(0);
        }

        public void bind(MediaFile model, AudioAdapterListener listener, int position) {

            this.model = model;
            if (isPlaying) {
                binding.ivPlayPause.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
            } else {
                binding.ivPlayPause.setImageResource(R.drawable.ic_outline_play_circle_filled_24);
            }

            binding.ivPlayPause.setOnClickListener(v -> {
                updatePlayPauseImageResource();
                isPlaying = !isPlaying;
                listener.playPause(isPlaying, model);
            });

            binding.ivDelete.setOnClickListener(v -> {
                list.remove(position);
                listener.remove(position, model);
            });

        }


        void updatePlayPauseImageResource() {
            if (isPlaying) {
                stopMedia();
                binding.ivPlayPause.setImageResource(R.drawable.ic_outline_play_circle_filled_24);
            } else {
                playMedia();
                binding.ivPlayPause.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
            }
        }

    }

}
