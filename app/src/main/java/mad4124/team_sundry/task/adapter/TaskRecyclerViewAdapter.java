package mad4124.team_sundry.task.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import mad4124.team_sundry.task.R;
import mad4124.team_sundry.task.databinding.TaskRowBinding;
import mad4124.team_sundry.task.db.AppDatabase;
import mad4124.team_sundry.task.model.MediaFile;
import mad4124.team_sundry.task.model.Task;
import mad4124.team_sundry.task.ui.MainViewModel;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> {

    final private Context context;
    private OnItemClickListener onItemClickListener;
    private List<Task> tasksList ;

    MainViewModel viewModel;

    public TaskRecyclerViewAdapter(List<Task> tasks, Context context, OnItemClickListener onItemClickListener, MainViewModel viewModel){
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.tasksList = tasks;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TaskRowBinding binding = TaskRowBinding.inflate(
                LayoutInflater.from(context),parent,false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(tasksList.get(position));
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TaskRowBinding binding;
        public ViewHolder(TaskRowBinding binding){
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }
        public void bind(Task model){
            int id = model.getId();
            List<MediaFile> mediaFiles = viewModel.getAllMedias(id);
            binding.titleRow.setText(model.getTitle());
            if(model.isTask()) {
                binding.categoryRow.setText("Task");
            }else{
                binding.categoryRow.setText("Note");
            }
            binding.descriptionRow.setText(model.getDescription());
            binding.dueDateRow.setText(model.getDueDate().toString());
            if(mediaFiles.size() > 0) {
                binding.imageView.setVisibility(View.VISIBLE);
                MediaFile file = mediaFiles.get(0);
                if(file.isImage()){
                    binding.imageView.setBackgroundResource(R.drawable.ic_audio);
                }else {
                    binding.imageView.setBackgroundResource(R.drawable.ic_audio);
                }
            }else {
                binding.imageView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setDataList(List<Task> tList) {
        this.tasksList = tList;
        notifyDataSetChanged();
    }

}