package mad4124.team_sundry.task.ui.taskDetail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mad4124.team_sundry.task.R;
import mad4124.team_sundry.task.databinding.ItemTaskAudioBinding;
import mad4124.team_sundry.task.model.MediaFile;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.VH> {

    private List<MediaFile> list = new ArrayList<>();
    private final Context context;
    private final AudioAdapterListener listener;

    interface AudioAdapterListener {
        void playPause(boolean play, MediaFile model) throws IOException;

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

        public void bind(MediaFile model, AudioAdapterListener listener, int position) {

            updatePlayPauseImageResource();

            binding.ivPlayPause.setOnClickListener(v -> {
                isPlaying = !isPlaying;
                try {
                    listener.playPause(isPlaying, model);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                updatePlayPauseImageResource();
            });

            binding.ivDelete.setOnClickListener(v -> {
                list.remove(position);
                listener.remove(position, model);
            });

            binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        listener.scrubberProgress(progress, model);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });


        }

        void updatePlayPauseImageResource() {
            if (isPlaying) {
                binding.ivPlayPause.setImageResource(R.drawable.ic_baseline_pause_circle_filled_24);
            } else {
                binding.ivPlayPause.setImageResource(R.drawable.ic_outline_play_circle_filled_24);
            }
        }

    }

}
