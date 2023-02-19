package mad4124.team_sundry.task.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import mad4124.team_sundry.task.R;
import mad4124.team_sundry.task.databinding.TaskRowBinding;
import mad4124.team_sundry.task.model.MediaFile;
import mad4124.team_sundry.task.model.Task;
import mad4124.team_sundry.task.ui.MainViewModel;

public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> {

    final private Context context;
    private OnItemClickListener onItemClickListener;
    private List<Task> tasksList ;

    MainViewModel viewModel;
    boolean isMultiSelection = false;

    public TaskRecyclerViewAdapter(List<Task> tasks, Context context, OnItemClickListener onItemClickListener, MainViewModel viewModel, boolean isMultiSelection){
        this.context = context;
        this.onItemClickListener = onItemClickListener;
        this.tasksList = tasks;
        this.viewModel = viewModel;
        this.isMultiSelection = isMultiSelection;
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
        public void bind(Task model) {
            int id = model.getId();
            List<MediaFile> mediaFiles = viewModel.getAllMedias(id);
            binding.taskTitleRow.setText(model.getTitle());
            if (model.isTask()) {
                binding.categoryRow.setText("Task");
            } else {
                binding.categoryRow.setText("Note");
            }
            binding.taskDescriptionRow.setText(model.getDescription());

            if (model.getDueDate() != 0){
                Long dueDate = model.getDueDate();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(dueDate);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                System.out.println(format.format(calendar.getTime()));
                binding.dueDateRow.setText(format.format(calendar.getTime()));
                binding.dueDateRow.setVisibility(View.VISIBLE);
            }else if (model.getCreatedDate() != 0){
                Long createDate = model.getCreatedDate();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(createDate);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
                System.out.println(format.format(calendar.getTime()));
                binding.dueDateRow.setText(format.format(calendar.getTime()));
                binding.dueDateRow.setVisibility(View.VISIBLE);
            }
            else {
                binding.dueDateRow.setVisibility(View.GONE);
            }
            if(mediaFiles.size() > 0) {
                binding.imageView.setVisibility(View.VISIBLE);
                MediaFile file = mediaFiles.get(0);
                if(file.isImage()){
                    binding.imageView.setBackgroundResource(R.drawable.ic_audio);
                }else {
                    binding.imageView.setBackgroundResource(R.drawable.ic_audio);
                }
            }else {
                binding.imageView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
            if (isMultiSelection) {
                if (binding.checkBox.getVisibility() == View.GONE) {
                    binding.checkBox.setVisibility(View.VISIBLE);
                } else {
                    binding.checkBox.setVisibility(View.GONE);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setDataList(List<Task> tList) {
        this.tasksList = tList;
        notifyDataSetChanged();
    }

    public void setSelection(boolean isMultiSelection) {
        this.isMultiSelection = isMultiSelection;
        notifyDataSetChanged();
    }

}